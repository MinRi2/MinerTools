package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;

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
            Draw.z(Layer.overlayUI);
            build.drawDisabled();
        }

        Draw.z(Layer.block);
        build.drawTeam();

        if(Vars.renderer.drawStatus && build.block.hasConsumers){
            Draw.z(Layer.block);
            build.drawStatus();
        }

        Draw.reset();
    }

}
