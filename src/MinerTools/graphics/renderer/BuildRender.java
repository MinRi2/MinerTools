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

public class BuildRender<T extends Building> extends BaseRender<T>{
    private final Seq<Block> types;

    /* Tiles for render in camera */
    private QuadTree<Tile> tiles;

    public BuildRender(Seq<Block> types){
        this.types = types;

        Events.on(WorldLoadEvent.class, e -> {
            tiles = new QuadTree<>(Vars.world.getQuadBounds(Tmp.r1));

            for(Tile tile : Vars.world.tiles){
                tiles.insert(tile);
            }
        });
    }

    @Override
    public void globalRender(Seq<BaseDrawer<T>> validDrawers){
        for(TeamData data : Vars.state.teams.getActive()){
            var buildingTypes = data.buildingTypes;

            for(Block type : types){
                for(Building building : buildingTypes.get(type)){
                    for(BaseDrawer<T> drawer : validDrawers){
                        drawer.tryDraw((T)building);
                    }
                }
            }
        }
    }

    @Override
    public void cameraRender(Seq<BaseDrawer<T>> validDrawers){
        tiles.intersect(Core.camera.bounds(Tmp.r1), tile -> {
            Building building = tile.build;

            if(building != null){
                for(BaseDrawer<T> drawer : validDrawers){
                    drawer.tryDraw((T)building);
                }
            }
        });
    }
}
