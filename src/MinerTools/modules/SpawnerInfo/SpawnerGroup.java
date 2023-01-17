package MinerTools.modules.SpawnerInfo;

import MinerTools.math.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;

public class SpawnerGroup{
    public final Seq<Vec2> spawnerPos = new Seq<>();
    private Vec2 centroid = null;

    public static void getSpawnerGroups(Seq<SpawnerGroup> groups, Seq<Vec2> rawSpawnerPos, float maxDst){
        handleSpawnerPos(groups, rawSpawnerPos, maxDst);
    }

    private static void handleSpawnerPos(Seq<SpawnerGroup> out, Seq<Vec2> spawnerPos, float maxDst){
        for(Vec2 spawner : spawnerPos){
            out.add(new SpawnerGroup().addSpawner(spawner));
        }

        float maxDst2 = maxDst * maxDst;

        int size = spawnerPos.size;
        int lastSize = size - 1;
        
        outer:
        for(int i = 0; i < lastSize; i++){
            Vec2 pos = spawnerPos.get(i);

            SpawnerGroup group = out.get(i);

            for(int j = i + 1; j < size; j++){
                SpawnerGroup otherGroup = out.get(j);

                if(otherGroup == group){
                    continue;
                }

                Vec2 otherPos = spawnerPos.get(j);
                                
                if(pos.dst2(otherPos) <= maxDst2){
                    group.addGroup(otherGroup);
                    out.set(j, group);
                    
                    if(group.spawnerPos.size == size){
                        break outer;
                    }
                }
            }
        }

        out.distinct().filter(SpawnerGroup::isValid);
    }

    public SpawnerGroup addSpawner(Vec2 spawner){
        if(!spawnerPos.contains(spawner, true)){
            spawnerPos.add(spawner);
        }
        return this;
    }

    public void addGroup(SpawnerGroup other){
        for(Vec2 spawner : other.spawnerPos){
            addSpawner(spawner);
        }
        other.spawnerPos.clear();
    }

    public boolean isValid(){
        return spawnerPos.any();
    }

    public Vec2 getCentroid(){
        if(centroid == null){
            centroid = new Vec2();
            
            float[] points = new float[spawnerPos.size * 2];

            for(int i = 0, size = spawnerPos.size; i < size; i++){
                Vec2 pos = spawnerPos.get(i);

                points[i * 2] = pos.x;
                points[i * 2 + 1] = pos.y;
            }

            Mathu.getCentroid(points, centroid);
        }

        return centroid;
    }

}
