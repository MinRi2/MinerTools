package MinerTools.graphics.renderer.build;

import MinerTools.graphics.renderer.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.ItemTurret.*;

public class ItemTurretRender extends BuildRender<ItemTurretBuild>{

    public ItemTurretRender(){
        super(block -> block instanceof ItemTurret);
    }

}
