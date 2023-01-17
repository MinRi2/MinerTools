package MinerTools.modules.SpawnerInfo;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class SpawnerRender{
    private final Seq<Vec2> groundSpawners = new Seq<>();
    private final Seq<Vec2> flyerSpawners = new Seq<>();
    private float groundRange = -1f;
    private float flyerRange = -1f;

    private ObjectSet<Building> destroyBuildings = new ObjectSet<>();

    public void setDestroyBuildings(ObjectSet<Building> buildings){
        destroyBuildings = buildings;
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

    public void draw(){
        drawFlyerRange();

        if(!destroyBuildings.isEmpty()){
            drawDestroyBuildings();
        }
    }

    private void drawFlyerRange(){
        Draw.z(Layer.overlayUI);

        for(Vec2 spawn : flyerSpawners){
            float spawnX = spawn.x, spawnY = spawn.y;

            Lines.dashCircle(spawnX, spawnY, flyerRange);
        }

        Draw.reset();
    }

    private void drawDestroyBuildings(){
        Draw.z(Layer.overlayUI);

        Draw.color(Color.white, 0.4f);
        Draw.mixcol(Pal.remove, 0.4f + Mathf.absin(Time.globalTime, 6f, 0.28f));

        Rect bounds = Core.camera.bounds(Tmp.r1);
        for(Building building : destroyBuildings){
            if(!bounds.contains(building.x, building.y)){
                continue;
            }

            float size = building.hitSize();
            Fill.rect(building.x, building.y, size, size);
        }

        Draw.reset();
    }

}
