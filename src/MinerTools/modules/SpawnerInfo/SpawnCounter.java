package MinerTools.modules.SpawnerInfo;

import arc.math.geom.*;
import arc.struct.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.type.*;

public class SpawnCounter{
    private static final SpawnCounter counter = new SpawnCounter();
    public final ObjectIntMap<UnitType> units = new ObjectIntMap<>();
    public float totalHealth, totalShield;

    private SpawnCounter(){
    }

    public static SpawnCounter count(Seq<SpawnGroup> spawnGroups, Seq<Vec2> spawnerPos, int wave){
        counter.clear();

        for(Vec2 worldPos: spawnerPos){
            for(SpawnGroup spawnGroup : spawnGroups){
                int pos = Point2.pack(World.toTile(worldPos.x), World.toTile(worldPos.y));

                if(!spawnGroup.canSpawn(pos)){
                    continue;
                }

                int count = spawnGroup.getSpawned(wave);

                if(count == 0){
                    continue;
                }

                UnitType type = spawnGroup.type;
                float shield = spawnGroup.getShield(wave);

                counter.units.increment(type, count);
                counter.totalHealth += type.health;
                counter.totalShield += shield;
            }
        }

        return counter;
    }

    private void clear(){
        units.clear();
        totalHealth = 0f;
        totalShield = 0f;
    }
}
