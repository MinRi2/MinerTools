package MinerTools.graphics.draw;

import mindustry.gen.*;

public abstract class UnitDrawer extends BaseDrawer<Unit>{
    @Override
    public boolean isValid(Unit unit){
        return unit.isValid();
    }
}