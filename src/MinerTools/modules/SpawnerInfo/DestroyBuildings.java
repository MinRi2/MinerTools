package MinerTools.modules.SpawnerInfo;

import MinerTools.modules.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class DestroyBuildings extends SettingModule{
    private final ObjectSet<Tile> tiles = new ObjectSet<>();
    private final ObjectSet<Building> buildings = new ObjectSet<>();
    private Seq<Tile> spawners;

    private float spawnRange;

    public DestroyBuildings(SettingModule parent){
        super(parent, "destroyBuildings");
    }

    public void setSpawnRange(float spawnRange){
        this.spawnRange = spawnRange;
    }

    public void setSpawners(Seq<Tile> spawners){
        this.spawners = spawners;
    }

    public void load(){
        Events.on(TilePreChangeEvent.class, e -> {
            if(isEnable() && e.tile != null){
                removeTile(e.tile);
            }
        });

        Events.on(TileChangeEvent.class, e -> {
            if(isEnable() && e.tile != null){
                addTile(e.tile);
            }
        });

        Events.run(Trigger.draw, () -> {
            if(isEnable() && !buildings.isEmpty()){
                drawDestroyBuildings();
            }
        });
    }

    public void worldLoad(){
        if(!isEnable()) return;

        tiles.clear();
        buildings.clear();

        for(Tile spawner : spawners){
            eachSpawnerTiles(spawner, tile -> {
                tiles.add(tile);

                if(isValidBase(tile) && tile.isCenter()){
                    buildings.add(tile.build);
                }
            });
        }
    }

    @Override
    public void enable(){
        if(!Vars.state.isGame()){
            return;
        }

        worldLoad();
    }

    @Override
    public void disable(){
        tiles.clear();
        buildings.clear();
    }

    private void removeTile(Tile tile){
        if(isValidBase(tile) && tiles.contains(tile)){
            buildings.remove(tile.build);
        }
    }

    private void addTile(Tile tile){
        if(isValidBase(tile) && tiles.contains(tile)){
            buildings.add(tile.build);
        }
    }
    
    private boolean isValidBase(Tile tile){
        Building building = tile.build;
        return building != null && state.rules.waveTeam.isEnemy(building.team());
    }

    private void eachSpawnerTiles(Tile spawner, Cons<Tile> cons){
        int trad = (int)(spawnRange / tilesize);
        
        float maxDst2 = trad * trad;
        int x = spawner.x, y = spawner.y;

        for(int dx = -trad; dx <= trad; dx++){
            for(int dy = -trad; dy <= trad; dy++){
                Tile tile = world.tile(x + dx, y + dy);

                if(tile != null && dx*dx + dy*dy <= maxDst2){
                    cons.get(tile);
                }
            }
        }
    }

    private void drawDestroyBuildings(){
        Draw.z(Layer.overlayUI);

        Draw.color(Color.white, 0.4f);
        Draw.mixcol(Pal.remove, 0.4f + Mathf.absin(Time.globalTime, 6f, 0.28f));

        Rect bounds = Core.camera.bounds(Tmp.r1);
        for(Building building : buildings){
            if(!bounds.contains(building.x, building.y)){
                continue;
            }

            float size = building.hitSize();
            Fill.rect(building.x, building.y, size, size);
        }

        Draw.reset();
    }

}
