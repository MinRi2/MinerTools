package MinerTools.graphics.draw;

import arc.*;
import arc.util.*;
import mindustry.gen.*;

public abstract class UnitDrawer extends BaseDrawer<Unit>{
    @Override
    public boolean isValid(Unit unit){
        return unit.isValid() && drawInCamera && Core.camera.bounds(Tmp.r1).contains(Tmp.v1.set(unit));
    }
}