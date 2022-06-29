package MinerTools.override;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.Reconstructor.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class Bars{

    public static void override(Block block){
        BlockBars bars = block.bars;

        bars.add("health", e -> new Bar(
        () -> String.format("%.2f", e.health) + "/" + e.maxHealth + "(" + (int)(100 * e.healthf()) + "%" + ")",
        () -> Pal.health, e::healthf).blink(Color.white));

        if(block.hasLiquids){
            addLiquidBars(block);
        }

        if(block instanceof UnitFactory factory){
            bars.add("progress", (UnitFactoryBuild e) -> new Bar(
            () -> {
                float ticks = e.currentPlan == -1 ? 0 : (1 - e.fraction()) * factory.plans.get(e.currentPlan).time / e.timeScale();
                return Core.bundle.get("bar.progress") + ":" + UI.formatTime(ticks) + "(" + (int)(100 * e.fraction()) + "%" + ")";
            },
            () -> Pal.ammo, e::fraction));
        }

        if(block instanceof Reconstructor reconstructor){
            bars.add("progress", (ReconstructorBuild e) -> new Bar(
            () -> Core.bundle.get("bar.progress") + ":" + UI.formatTime((1 - e.fraction()) * reconstructor.constructTime / e.timeScale()) + "(" + (int)(100 * e.fraction()) + "%" + ")",
            () -> Pal.ammo, e::fraction));
        }
    }

    private static void addLiquidBars(Block block){
        if(block.hasLiquids){
            Func<Building, Liquid> current;
            if(block.consumes.has(ConsumeType.liquid) && block.consumes.get(ConsumeType.liquid) instanceof ConsumeLiquid consumeLiquid){
                Liquid liquid = consumeLiquid.liquid;
                current = entity -> liquid;
            }else{
                current = entity -> entity.liquids == null ? Liquids.water : entity.liquids.current();
            }
            block.bars.add("liquid", e -> new Bar(
            () -> {
                Liquid liquid = current.get(e);
                float amount = e.liquids.get(liquid);
                if(amount <= 0.001f){
                    return Core.bundle.get("bar.liquid");
                }else{
                    return current.get(e).localizedName + ":" + Strings.autoFixed(amount, 2);
                }
            },
            () -> current.get(e).barColor(),
            () -> e == null || e.liquids == null ? 0f : e.liquids.get(current.get(e)) / block.liquidCapacity));
        }
    }

}
