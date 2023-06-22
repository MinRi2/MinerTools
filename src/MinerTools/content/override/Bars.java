package MinerTools.content.override;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.heat.HeatProducer.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.power.NuclearReactor.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.Reconstructor.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;

public class Bars{

    public static void override(Block block){
        block.addBar("health", e -> new Bar(
        () -> String.format("%.1f", e.health) + "/" + e.maxHealth + "(" + (int)(100 * e.healthf()) + "%" + ")",
        () -> Pal.health, e::healthf).blink(Color.white));

        if(block.hasLiquids){
            addLiquidBars(block);

            if(block instanceof GenericCrafter crafter && crafter.outputLiquids != null){
                crafter.removeBar("liquid");

                for(var stack : crafter.outputLiquids){
                    addLiquidBar(crafter, stack.liquid);
                }
            }
        }

        if(block instanceof UnitFactory factory){
            block.addBar("progress", (UnitFactoryBuild e) -> new Bar(
            () -> {
                float ticks = e.currentPlan == -1 ? 0 : (1 - e.fraction()) * factory.plans.get(e.currentPlan).time / e.timeScale();
                return Core.bundle.get("bar.progress") + ":" + UI.formatTime(ticks) + "(" + (int)(100 * e.fraction()) + "%" + ")";
            },
            () -> Pal.ammo, e::fraction));
        }

        if(block instanceof Reconstructor reconstructor){
            block.addBar("progress", (ReconstructorBuild e) -> new Bar(
            () -> Core.bundle.get("bar.progress") + ":" + UI.formatTime((1 - e.fraction()) * reconstructor.constructTime / e.timeScale()) + "(" + (int)(100 * e.fraction()) + "%" + ")",
            () -> Pal.ammo, e::fraction));
        }

        if(block instanceof HeatProducer producer){
            producer.addBar("heat", (HeatProducerBuild e) -> new Bar(
            () -> Core.bundle.format("bar.heatamount", (int)e.heat),
            () -> Pal.lightOrange, () -> e.heat / producer.heatOutput)
            );
        }

        if(block instanceof NuclearReactor reactor){
            reactor.addBar("heat", (NuclearReactorBuild e) -> new Bar(
            () -> Core.bundle.format("bar.heatamount", Strings.autoFixed(e.heat, 2)),
            () -> Pal.lightOrange, () -> e.heat));
        }
    }

    private static void addLiquidBars(Block block){
        boolean added = false;
        for(var consume : block.consumers){
            if(consume instanceof ConsumeLiquid liq){
                added = true;
                addLiquidBar(block, liq.liquid);
            }else if(consume instanceof ConsumeLiquids multi){
                added = true;
                for(var stack : multi.liquids){
                    addLiquidBar(block, stack.liquid);
                }
            }
        }

        //nothing was added, so it's safe to add a dynamic liquid bar (probably?)
        if(!added){
            addLiquidBar(block, build -> build.liquids.current());
        }
    }

    private static void addLiquidBar(Block block, Liquid liq){
        block.addBar("liquid-" + liq.name, e -> new Bar(
        () -> liq.localizedName + ": " + Strings.autoFixed(e.liquids.get(liq), 2),
        liq::barColor,
        () -> e.liquids.get(liq) / block.liquidCapacity
        ));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Building> void addLiquidBar(Block block, Func<T, Liquid> current){
        block.addBar("liquid", e -> new Bar(
        () -> {
            if(current.get((T)e) == null || e.liquids.get(current.get((T)e)) <= 0.001f){
                return Core.bundle.get("bar.liquid");
            }
            Liquid liq = current.get((T)e);
            return liq.localizedName + ": " + Strings.autoFixed(e.liquids.get(liq), 2);
        },
        () -> current.get((T)e) == null ? Color.clear : current.get((T)e).barColor(),
        () -> current.get((T)e) == null ? 0f : e.liquids.get(current.get((T)e)) / block.liquidCapacity)
        );
    }

}
