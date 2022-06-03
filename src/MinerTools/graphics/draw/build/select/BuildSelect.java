package MinerTools.graphics.draw.build.select;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import mindustry.*;
import mindustry.gen.*;

public class BuildSelect extends BuildDrawer<Building>{

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("buildSelectInfo");
    }

    @Override
    public boolean isValid(Building building){
        return super.isValid(building) && building.team != Vars.player.team();
    }

    @Override
    protected void draw(Building build){
        build.drawSelect();
    }

}
