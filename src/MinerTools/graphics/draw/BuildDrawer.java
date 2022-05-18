package MinerTools.graphics.draw;

import mindustry.gen.*;

public abstract class BuildDrawer<T extends Building> extends BaseDrawer<T>{

    @Override
    public boolean isValid(Building type){
        return type.isValid();
    }

}
