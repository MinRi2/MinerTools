package MinerTools.graphics.draw.build;

import MinerTools.graphics.draw.*;
import mindustry.*;
import mindustry.gen.*;

public class BuildSelect extends BuildDrawer<Building>{

    @Override
    public boolean isValid(Building building){
        return super.isValid(building) && building.team != Vars.player.team();
    }

    @Override
    protected void draw(Building build){
        build.drawSelect();

        if(!build.enabled && build.block.drawDisabled){
            build.drawDisabled();
        }

        if(Vars.renderer.drawStatus && build.block.hasConsumers){
            build.drawStatus();
        }
    }

}
