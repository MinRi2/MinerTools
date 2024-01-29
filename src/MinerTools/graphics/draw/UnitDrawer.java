package MinerTools.graphics.draw;

import mindustry.*;
import mindustry.gen.*;

public abstract class UnitDrawer extends Drawer<Unit>{

    @Override
    public boolean shouldDraw(Unit unit){
        return unit.isValid() && !unit.inFogTo(Vars.player.team());
    }

    @Override
    protected abstract void draw(Unit unit);
}