package MinerTools.modules;

import MinerTools.*;
import MinerTools.modules.AbstractModule.*;
import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;

import java.lang.reflect.*;

public class SpawnerInfo extends SettingModule{
    private static final Method eachFlyerSpawnMethod = MinerUtils.getMethod(
    WaveSpawner.class,
    "eachFlyerSpawn",
    int.class, Floatc2.class
    );
    private static final float drawCircleRange = Vars.tilesize * 3;

    private WaveSpawner spawner;

    public SpawnerInfo(){
        super("spawnerInfo");
    }

    @Override
    public void load(){
        super.load();

        spawner = Vars.spawner;

        Events.run(Trigger.draw, () -> {
            if(!isEnable()){
                return;
            }

            Draw.z(Layer.overlayUI);

            spawner.eachGroundSpawn((spawnWorldX, spawnWorldY) -> {
                float spawnX = spawnWorldX * Vars.tilesize, spawnY = spawnWorldY * Vars.tilesize;

                Lines.dashCircle(spawnX, spawnY, drawCircleRange);
            });

            MinerUtils.invokeMethod(spawner, eachFlyerSpawnMethod, -1, (Floatc2)((spawnX, spawnY) -> {
                Lines.dashCircle(spawnX, spawnY, drawCircleRange);
            }));

            Draw.reset();
        });
    }

}
