package MinerTools.modules.SpawnerInfo;

import MinerTools.modules.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;

public class SpawnerRange extends SettingModule{
    private final Seq<Vec2> groundSpawners = new Seq<>();
    private final Seq<Vec2> flyerSpawners = new Seq<>();
    private float groundRange = -1f;
    private float flyerRange = -1f;

    public SpawnerRange(SettingModule parent){
        super(parent, "spawnerRange");
    }

    public void setRange(float groundRange, float flyerRange){
        this.groundRange = groundRange;
        this.flyerRange = flyerRange;
    }

    public void setGroundSpawners(Seq<Vec2> spawners){
        groundSpawners.set(spawners);
    }

    public void setFlyerSpawners(Seq<Vec2> spawners){
        flyerSpawners.set(spawners);
    }

    @Override
    public void load(){
        super.load();

        Events.run(Trigger.draw, () -> {
            if(isEnable()){
                draw();
            }
        });
    }

    public void draw(){
        drawFlyerRange();
    }

    private void drawFlyerRange(){
        Draw.z(Layer.overlayUI);

        for(Vec2 spawn : flyerSpawners){
            float spawnX = spawn.x, spawnY = spawn.y;

            Lines.dashCircle(spawnX, spawnY, flyerRange);
        }

        Draw.reset();
    }

}
