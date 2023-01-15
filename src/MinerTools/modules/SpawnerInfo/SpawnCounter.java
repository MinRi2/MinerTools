package MinerTools.modules.SpawnerInfo;

import arc.struct.*;
import mindustry.game.*;
import mindustry.type.*;

public class SpawnCounter{
    private static final SpawnCounter counter = new SpawnCounter();
    public final ObjectIntMap<UnitType> units = new ObjectIntMap<>();
    public float totalHealth, totalShield;

    private SpawnCounter(){
    }

    public static SpawnCounter count(Seq<SpawnGroup> spawnGroups, IntSeq spawnerPos, int wave){
        counter.clear();

        for(int pos : spawnerPos.items){
            for(SpawnGroup spawnGroup : spawnGroups){
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
    }
}
