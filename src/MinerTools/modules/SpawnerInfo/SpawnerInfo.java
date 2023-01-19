package MinerTools.modules.SpawnerInfo;

import MinerTools.modules.*;
import MinerTools.utils.*;
import arc.*;
import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.game.*;
import mindustry.game.EventType.*;

import java.lang.reflect.*;

import static mindustry.Vars.spawner;

public class SpawnerInfo extends SettingModule{
    // World Position
    private static final Seq<Vec2> tmp = new Seq<>();

    private static final Method eachFlyerSpawnMethod = MinerUtils.getMethod(
    WaveSpawner.class,
    "eachFlyerSpawn",
    int.class, Floatc2.class
    );

    private final GroupStat ground = new GroupStat();
    private final GroupStat flyer = new GroupStat();

    private final SpawnerTables tables;
    private final DestroyBuildings destroy;
    private final SpawnerRange render;
    private final SpawnerViewer viewer;

    public float groundRange = 0f;
    public float flayerRange = 6 * Vars.tilesize;

    public SpawnerInfo(){
        super("spawnerInfo");
        
        tables = new SpawnerTables(this);
        destroy = new DestroyBuildings(this);
        render = new SpawnerRange(this);
        viewer = new SpawnerViewer(this);
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
        return super.isEnable() && spawner.countSpawns() <= 20;
    }

    @Override
    public void load(){
        super.load();

        tables.setGroupStats(ground, flyer);

        Events.on(WorldLoadEvent.class, e -> {
            if(isEnable()){
                Core.app.post(this::worldLoad);
            }
        });
    }

    private void worldLoad(){
        ground.clear();
        flyer.clear();
        viewer.clear();

        groundRange = Vars.state.rules.dropZoneRadius;

        loadSpawnerPos();
        loadSpawnGroups();
        
        destroy.setSpawnRange(groundRange);
        render.setRange(groundRange, flayerRange);

        viewer.addViewGroup(ground.groups);
        viewer.addViewGroup(flyer.groups);

        destroy.worldLoad();
        tables.worldLoad();
        viewer.worldLoad();
    }

    private void loadSpawnerPos(){
        eachGroundSpawn((spawnX, spawnY) -> {
            tmp.add(new Vec2(spawnX * Vars.tilesize, spawnY * Vars.tilesize));
        });

        SpawnerGroup.getSpawnerGroups(ground.groups, tmp, groundRange * 2.5f);
        render.setGroundSpawners(tmp);

        tmp.clear();
        eachFlyerSpawn((spawnWorldX, spawnWorldY) -> {
            tmp.add(new Vec2(spawnWorldX, spawnWorldY));
        });

        SpawnerGroup.getSpawnerGroups(flyer.groups, tmp, flayerRange * 2.5f);
        render.setFlyerSpawners(tmp);

        tmp.clear();
        
        destroy.setSpawners(spawner.getSpawns());
    }

    private void loadSpawnGroups(){
        for(SpawnGroup spawnGroup : Vars.state.rules.spawns){
            if(spawnGroup.type.flying){
                flyer.spawnGroups.add(spawnGroup);
            }else{
                ground.spawnGroups.add(spawnGroup);
            }
        }
    }

    static class GroupStat{
        public Seq<SpawnerGroup> groups = new Seq<>();
        public Seq<SpawnGroup> spawnGroups = new Seq<>();

        public void clear(){
            groups.clear();
            spawnGroups.clear();
        }

    }

}
