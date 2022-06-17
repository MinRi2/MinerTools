package MinerTools.override;

import arc.*;
import arc.graphics.*;
import mindustry.core.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.Reconstructor.*;
import mindustry.world.blocks.units.UnitFactory.*;

public class Bars{

    public static void override(Block block){

        block.addBar("health", e -> new Bar(
        () -> String.format("%.2f", e.health) + "/" + e.maxHealth + "(" + (int)(100 * e.healthf()) + "%" + ")",
        () -> Pal.health, e::healthf).blink(Color.white));

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
    }

}
