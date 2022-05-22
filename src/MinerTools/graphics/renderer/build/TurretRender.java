package MinerTools.graphics.renderer.build;

import MinerTools.graphics.renderer.*;
import mindustry.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.Turret.*;

public class TurretRender<T extends TurretBuild> extends BuildRender<T>{

    public TurretRender(){
        super(Vars.content.blocks().select(block -> block instanceof Turret));
    }

}
