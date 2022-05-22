package MinerTools.graphics.renderer;

import MinerTools.graphics.draw.*;
import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;

public class UnitRender extends BaseRender<Unit>{

    @Override
    public void globalRender(Seq<BaseDrawer<Unit>> validDrawers){
        for(TeamData data : Vars.state.teams.getActive()){
            for(Unit unit : data.units){
                for(BaseDrawer<Unit> drawer : validDrawers){
                    drawer.tryDraw(unit);
                }
            }
        }
    }

    @Override
    public void cameraRender(Seq<BaseDrawer<Unit>> validDrawers){
        for(TeamData data : Vars.state.teams.getActive()){
            data.unitTree.intersect(Core.camera.bounds(Tmp.r1), unit -> {
                for(BaseDrawer<Unit> drawer : validDrawers){
                    drawer.tryDraw(unit);
                }
            });
        }
    }

}
