package MinerTools.modules.SpawnerInfo;

import MinerTools.math.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;

public class SpawnerGroup{
    public final IntSeq spawnerPos = new IntSeq();
    private Vec2 centroid = null;

    public static Seq<SpawnerGroup> getSpawnerGroups(IntSeq rawSpawnerPos, int maxDst){
        return handleSpawnerPos(rawSpawnerPos, maxDst);
    }

    private static Seq<SpawnerGroup> handleSpawnerPos(IntSeq spawnerPos, int maxDst){
        Seq<SpawnerGroup> out = new Seq<>();

        for(int spawner : spawnerPos.items){
            out.add(new SpawnerGroup().addSpawner(spawner));
        }

        int maxDst2 = maxDst * maxDst;

        int size = spawnerPos.size;
        int lastSize = size - 1;
        for(int i = 0; i < lastSize; i++){
            int pos = spawnerPos.get(i);
            int px = Point2.x(pos), py = Point2.y(pos);

            SpawnerGroup group = new SpawnerGroup().addSpawner(pos);
            out.set(i, group);

            for(int j = i + 1; j < size; j++){
                int otherPos = spawnerPos.get(j);
                int ox = Point2.x(otherPos), oy = Point2.y(otherPos);

                SpawnerGroup otherGroup = out.get(j);

                if(otherGroup == group){
                    continue;
                }

                if(Mathf.dst2(px, py, ox, oy) <= maxDst2){
                    group.addGroup(otherGroup);
                    out.set(j, group);
                }
            }
        }

        out.distinct().filter(SpawnerGroup::isValid);

        return out;
    }

    public SpawnerGroup addSpawner(int spawner){
        spawnerPos.add(spawner);
        return this;
    }

    public void addGroup(SpawnerGroup other){
        spawnerPos.addAll(other.spawnerPos);
        other.spawnerPos.clear();
    }

    public boolean isValid(){
        return !spawnerPos.isEmpty();
    }

    public Vec2 getCentroid(){
        if(centroid == null){
            float[] points = new float[spawnerPos.size * 2];

            for(int i = 0, size = spawnerPos.size; i < size; i++){
                int pos = spawnerPos.get(i);

                points[i * 2] = Point2.x(pos) * Vars.tilesize;
                points[i * 2 + 1] = Point2.y(pos) * Vars.tilesize;
            }

            Mathu.getCentroid(points, centroid);
        }

        return centroid;
    }

}
