package MinerTools.modules.SpawnerInfo;

import MinerTools.*;
import MinerTools.modules.AbstractModule.*;
import arc.*;
import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.core.*;
import mindustry.game.EventType.*;

import java.lang.reflect.*;

import static mindustry.Vars.spawner;

public class SpawnerInfo extends SettingModule{
    private static final IntSeq tmp = new IntSeq();

    private static final Method eachFlyerSpawnMethod = MinerUtils.getMethod(
    WaveSpawner.class,
    "eachFlyerSpawn",
    int.class, Floatc2.class
    );

    private final GroupStat stat = new GroupStat();
    private final SpawnerTables fragment = new SpawnerTables();
    private final SpawnerRender render = new SpawnerRender();

    public int groundRange = 0;
    public int flayerRange = 6;

    public SpawnerInfo(){
        super("spawnerInfo");
    }

    public static void eachGroundSpawn(Intc2 intc2){
        spawner.eachGroundSpawn(intc2);
    }

    public static void eachFlyerSpawn(Floatc2 floatc2){
        eachFlyerSpawn(-1, floatc2);
    }

    public static void eachFlyerSpawn(int filterPos, Floatc2 floatc2){
        MinerUtils.invokeMethod(spawner, eachFlyerSpawnMethod, filterPos, floatc2);
    }

    @Override
    public boolean isEnable(){
        return super.isEnable() && spawner.countSpawns() < 30;
    }

    @Override
    public void load(){
        super.load();

        fragment.setup();

        Events.on(WorldLoadEvent.class, e -> {
            groundRange = World.toTile(Vars.state.rules.dropZoneRadius);

            loadSpawnerPos();

            render.setRange(groundRange, flayerRange);
            fragment.setGroups(stat.ground, stat.flyer);

            fragment.load();
        });

        Events.run(Trigger.draw, render::draw);
    }

    private void loadSpawnerPos(){

        eachGroundSpawn((spawnX, spawnY) -> {
            tmp.add(Point2.pack(spawnX, spawnY));
        });

        stat.ground = SpawnerGroup.getSpawnerGroups(tmp, groundRange);
        render.setGroundSpawners(tmp);

        tmp.clear();
        eachFlyerSpawn((spawnWorldX, spawnWorldY) -> {
            int spawnX = World.toTile(spawnWorldX), spawnY = World.toTile(spawnWorldY);
            tmp.add(Point2.pack(spawnX, spawnY));
        });

        stat.flyer = SpawnerGroup.getSpawnerGroups(tmp, flayerRange);
        render.setFlyerSpawners(tmp);

        tmp.clear();
    }

    private static class GroupStat{
        public Seq<SpawnerGroup> ground;
        public Seq<SpawnerGroup> flyer;

    }

}
