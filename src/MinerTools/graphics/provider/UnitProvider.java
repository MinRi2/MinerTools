package MinerTools.graphics.provider;

import MinerTools.graphics.draw.*;
import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;

public class UnitProvider extends CameraProvider<UnitDrawer>{
    
    @Override
    public void globalProvide(Seq<UnitDrawer> validDrawers){
        for(TeamData data : Vars.state.teams.present){
            for(Unit unit : data.units){
                for(UnitDrawer drawer : validDrawers){
                    drawer.tryDraw(unit);
                }
            }
        }
    }

    @Override
    public void cameraProvide(Seq<UnitDrawer> validDrawers){
        Rect bounds = Core.camera.bounds(Tmp.r1);

        for(TeamData data : Vars.state.teams.present){
            var unitTree = data.unitTree;

            if(unitTree == null) return;

            unitTree.intersect(bounds, unit -> {
                for(UnitDrawer drawer : validDrawers){
                    drawer.tryDraw(unit);
                }
            });
        }
    }

}
