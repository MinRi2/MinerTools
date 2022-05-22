package MinerTools.graphics.renderer.build;

import MinerTools.graphics.renderer.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.Turret.*;

public class TurretRender extends BuildRender<TurretBuild>{

    public TurretRender(){
        super(block -> block instanceof Turret);
    }

}
