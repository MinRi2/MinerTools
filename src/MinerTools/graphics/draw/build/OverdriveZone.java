package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.OverdriveProjector.*;

public class OverdriveZone extends BuildDrawer<OverdriveBuild>{

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("overdriveZone");
    }

    public OverdriveZone(){
        super(block -> block instanceof OverdriveProjector);
    }

    @Override
    protected void draw(OverdriveBuild build){
        OverdriveProjector  block = (OverdriveProjector)build.block;

        float realRange = block.range + build.phaseHeat * block.phaseRangeBoost;

        Draw.z(MLayer.overdriveZone);

        if(build.canConsume()){
            Draw.color(block.baseColor);
            Fill.circle(build.x, build.y, realRange);
        }

        Draw.reset();
    }

}
