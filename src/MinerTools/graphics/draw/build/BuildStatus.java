package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;

public class BuildStatus extends BuildDrawer<Building>{

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("buildStatus");
    }

    @Override
    public boolean isValid(Building building){
        return super.isValid(building) && building.team != Team.derelict && building.team != Vars.player.team();
    }

    @Override
    protected void draw(Building build){
        if(!build.enabled && build.block.drawDisabled){
            build.drawDisabled();
        }

        build.drawTeam();

        if(Vars.renderer.drawStatus && build.block.hasConsumers){
            build.drawStatus();
        }
    }

}
