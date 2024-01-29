package MinerTools.graphics.provider;

import MinerTools.graphics.draw.*;
import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;

public class BuildProvider extends CameraProvider<BuildDrawer<?>>{

    @Override
    public void globalProvide(Seq<BuildDrawer<?>> validDrawers){
        for(TeamData data : Vars.state.teams.present){
            var buildings = data.buildings;

            for(Building building : buildings){
                for(BuildDrawer<?> drawer : validDrawers){
                    drawer.tryDraw(building);
                }
            }
        }
    }

    @Override
    public void cameraProvide(Seq<BuildDrawer<?>> validDrawers){
        Rect bounds = Core.camera.bounds(Tmp.r1);

        for(TeamData data : Vars.state.teams.present){
            var buildingTree = data.buildingTree;

            if(buildingTree == null) continue;

            buildingTree.intersect(bounds, build -> {
                for(BuildDrawer<?> drawer : validDrawers){
                    drawer.tryDraw(build);
                }
            });
        }
    }
}
