package MinerTools.modules.SpawnerInfo;

import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class DestroyBuildings{
    private final ObjectSet<Tile> tiles = new ObjectSet<>();
    private final ObjectSet<Building> buildings = new ObjectSet<>();
    private Seq<Tile> spawners;

    private float spawnRange;

    public void setSpawnRange(float spawnRange){
        this.spawnRange = spawnRange;
    }

    public void setSpawners(Seq<Tile> spawners){
        this.spawners = spawners;
    }

    public void load(){
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

    public void removeTile(Tile tile){
        if(isValidBase(tile) && tiles.contains(tile)){
            buildings.remove(tile.build);
        }
    }

    public void addTile(Tile tile){
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

    public ObjectSet<Building> getBuildings(){
        return buildings;
    }
}
