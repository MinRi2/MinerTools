package MinerTools.graphics.renderer;

import MinerTools.graphics.draw.*;
import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;

public class UnitRender extends BaseRender<UnitDrawer>{

    @Override
    public void globalRender(Seq<UnitDrawer> validDrawers){
        for(TeamData data : Vars.state.teams.getActive()){
            for(Unit unit : data.units){
                for(UnitDrawer drawer : validDrawers){
                    drawer.tryDraw(unit);
                }
            }
        }
    }

    @Override
    public void cameraRender(Seq<UnitDrawer> validDrawers){
        Rect bounds = Core.camera.bounds(Tmp.r1);

        QuadTree<Unit> unitTree = Groups.unit.tree();

        if(unitTree == null) return;

        unitTree.intersect(bounds, unit -> {
            for(UnitDrawer drawer : validDrawers){
                drawer.tryDraw(unit);
            }
        });
    }

}
