package MinerTools.modules.SpawnerInfo;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.graphics.*;

import static mindustry.Vars.tilesize;

public class SpawnerRender{
    private final Seq<Vec2> groundSpawners = new Seq<>();
    private final Seq<Vec2> flyerSpawners = new Seq<>();
    private float groundRange = -1f;
    private float flyerRange = -1f;

    public void setRange(int groundRange, int flyerRange){
        this.groundRange = groundRange * tilesize;
        this.flyerRange = flyerRange * tilesize;
    }

    public void setGroundSpawners(IntSeq spawners){
        groundSpawners.clear();

        for(int pos : spawners.items){
            int x = Point2.x(pos), y = Point2.y(pos);
            float sx = x * tilesize, sy = y * tilesize;

            groundSpawners.add(new Vec2(sx, sy));
        }
    }

    public void setFlyerSpawners(IntSeq spawners){
        flyerSpawners.clear();

        for(int pos : spawners.items){
            int x = Point2.x(pos), y = Point2.y(pos);
            float sx = x * tilesize, sy = y * tilesize;

            flyerSpawners.add(new Vec2(sx, sy));
        }
    }

    public void draw(){
        Draw.z(Layer.overlayUI);

        for(Vec2 spawn : flyerSpawners){
            float spawnX = spawn.x, spawnY = spawn.y;

            Lines.dashCircle(spawnX, spawnY, flyerRange);
        }

        Draw.reset();
    }

}
