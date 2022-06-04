package MinerTools.graphics.renderer;

import MinerTools.graphics.draw.*;
import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;

public class BuildRender extends BaseRender<BuildDrawer<?>>{
    public static Seq<Building> tmp = new Seq<>();

    @Override
    public void globalRender(Seq<BuildDrawer<?>> validDrawers){
        for(TeamData data : Vars.state.teams.getActive()){
            var buildings = data.buildings;

            if(buildings == null) return;

            buildings.getObjects(tmp);

            for(Building building : tmp){
                for(BuildDrawer<?> drawer : validDrawers){
                    drawer.tryDraw(building);
                }
            }

            tmp.clear();
        }
    }

    @Override
    public void cameraRender(Seq<BuildDrawer<?>> validDrawers){
        Rect bounds = Core.camera.bounds(Tmp.r1);

        for(TeamData data : Vars.state.teams.getActive()){
            var buildings = data.buildings;

            buildings.intersect(bounds, build -> {
                for(BuildDrawer<?> drawer : validDrawers){
                    drawer.tryDraw(build);
                }
            });
        }
    }
}
