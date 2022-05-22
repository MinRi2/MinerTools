package MinerTools.graphics.draw;

import mindustry.*;
import mindustry.gen.*;

public abstract class BuildDrawer<T extends Building> extends BaseDrawer<T>{

    @Override
    public boolean isValid(Building building){
        return building.isValid() && !building.inFogTo(Vars.player.team());
    }

}
