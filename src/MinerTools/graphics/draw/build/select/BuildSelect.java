package MinerTools.graphics.draw.build.select;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import mindustry.*;
import mindustry.gen.*;

public class BuildSelect extends BuildDrawer<Building>{

    @Override
    public boolean isEnabled(){
        return MinerVars.settings.getBool("buildSelectInfo");
    }

    @Override
    public boolean shouldDraw(Building building){
        return super.shouldDraw(building) && building.team != Vars.player.team();
    }

    @Override
    protected void draw(Building build){
        build.drawSelect();

        Draw.reset();
    }

}
