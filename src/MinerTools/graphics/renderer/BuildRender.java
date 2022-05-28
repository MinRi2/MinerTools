package MinerTools.graphics.renderer;

import MinerTools.graphics.draw.*;
import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.world.*;

public class BuildRender extends BaseRender<BuildDrawer<?>>{
    /* Tiles for render in camera */
    private static QuadTree<Tile> tiles;

    static {
        Events.on(WorldLoadEvent.class, e -> {
            tiles = new QuadTree<>(Vars.world.getQuadBounds(Tmp.r1));

            for(Tile tile : Vars.world.tiles){
                tiles.insert(tile);
            }
        });
    }

    @Override
    public void globalRender(Seq<BuildDrawer<?>> validDrawers){
        for(TeamData data : Vars.state.teams.getActive()){
            var buildings = data.buildings;

            for(Building building : buildings){
                for(BuildDrawer<?> drawer : validDrawers){
                    drawer.tryDraw(building);
                }
            }
        }
    }

    @Override
    public void cameraRender(Seq<BuildDrawer<?>> validDrawers){
        tiles.intersect(Core.camera.bounds(Tmp.r1), tile -> {
            Building building = tile.build;

            if(building != null){
                for(BuildDrawer<?> drawer : validDrawers){
                    drawer.tryDraw(building);
                }
            }
        });
    }
}
