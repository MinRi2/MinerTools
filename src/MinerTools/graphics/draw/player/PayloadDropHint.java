package MinerTools.graphics.draw.player;

import MinerTools.graphics.draw.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

public class PayloadDropHint extends PlayerDrawer{

    @Override
    public boolean isValid(Player player){
        return super.isValid(player) && player.unit() instanceof PayloadUnit;
    }

    @Override
    protected void draw(Player player){
        PayloadUnit unit = (PayloadUnit)player.unit();

        Draw.z(Layer.flyingUnit + 0.1f);

        if(unit.payloads.any()){
            Payload payload = unit.payloads.first();

            if(payload instanceof BuildPayload p){
                buildDrop(unit, p);
            }
        }else{
            buildPickUp(unit);
        }

        if(unit.payloadUsed() + Vars.tilesize < unit.type.payloadCapacity){
            pickUpHint(unit);
        }

        Draw.reset();
    }

    private static void buildDrop(Unit unit, BuildPayload payload){
        Building build = payload.build;
        int tx = World.toTile(unit.x - build.block.offset);
        int ty = World.toTile(unit.y - build.block.offset);

        Tile on = Vars.world.tile(tx, ty);
        if(on != null){
            boolean valid = Build.validPlace(build.block, build.team, tx, ty, build.rotation, false);

            float size = build.block.size * Vars.tilesize + build.block.offset;

            int rot = (int)((unit.rotation + 45f) / 90f) % 4  * 90;

            Draw.reset();
            Draw.mixcol(!valid ? Pal.breakInvalid : Color.white, (!valid ? 0.4f : 0.24f) + Mathf.absin(Time.globalTime, 6f, 0.28f));
            Draw.alpha(0.8f);
            Draw.rect(build.block.fullIcon, on.drawx(), on.drawy(), size, size, rot);

            Draw.reset();
        }
    }

    private static void buildPickUp(PayloadUnit unit){
        Tile tile = unit.tileOn();

        if(tile != null){
            Building build = tile.build;

            if(build == null || !unit.canPickup(tile.build)) return;

            float size = build.block.size * Vars.tilesize + build.block.offset;

            Draw.mixcol(Color.white, 0.24f + Mathf.absin(Time.globalTime, 6f, 0.28f));
            Draw.alpha(0.8f);
            Draw.rect(build.block.fullIcon, tile.drawx(), tile.drawy(), size, size, build.rotation * 90);

            Draw.reset();
        }
    }

    private static void pickUpHint(PayloadUnit unit){
        Tile tile = unit.tileOn();

        if(tile == null) return;

        float size = Vars.tilesize;

        if(tile.build != null && unit.canPickup(tile.build)){
            size *= tile.build.block.size;
        }

        Drawf.dashRect(Tmp.c1.set(Pal.accent).a(0.6f), tile.drawx() - size / 2, tile.drawy() - size / 2, size, size);

        Draw.reset();
    }

}
