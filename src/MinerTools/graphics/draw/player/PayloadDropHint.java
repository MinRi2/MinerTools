package MinerTools.graphics.draw.player;

import MinerTools.*;
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
    public boolean enabled(){
        return MinerVars.settings.getBool("payloadDropHint");
    }

    @Override
    public boolean isValid(Player player){
        return super.isValid(player) && player.unit() instanceof PayloadUnit;
    }

    @Override
    protected void draw(Player player){
        PayloadUnit unit = (PayloadUnit)player.unit();

        Draw.z(Layer.flyingUnit + 0.1f);

        if(unit.payloads.any()){
            Payload payload = unit.payloads.peek();

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

            Block block = build.block;

            float size = block.size * Vars.tilesize + block.offset;

            int rot = (int)((unit.rotation + 45f) / 90f) % 4  * 90;

            Draw.mixcol(!valid ? Pal.breakInvalid : Color.white, (!valid ? 0.4f : 0.24f) + Mathf.absin(Time.globalTime, 6f, 0.28f));
            Draw.alpha(0.8f);
            Draw.rect(block.fullIcon, (on.x + 0.5f) * Vars.tilesize, (on.y + 0.5f) * Vars.tilesize, size, size, rot);

            Draw.reset();
        }
    }

    private static void buildPickUp(PayloadUnit unit){
        Tile tile = unit.tileOn();

        if(tile != null){
            Building build = tile.build;

            if(build == null || !unit.canPickup(tile.build)) return;

            Block block = build.block;

            float size = block.size * Vars.tilesize + block.offset;

            Draw.mixcol(Color.white, 0.24f + Mathf.absin(Time.globalTime, 6f, 0.28f));
            Draw.alpha(0.8f);
            Draw.rect(block.fullIcon, tile.build.x, tile.build.y, size, size, build.rotation * 90);

            Draw.reset();
        }
    }

    private static void pickUpHint(PayloadUnit unit){
        Tile tile = unit.tileOn();

        if(tile == null) return;

        float drawX = tile.drawx(), drawY = tile.drawy();
        float size = Vars.tilesize;

        if(tile.build != null){
            Building build = tile.build;

            if(unit.canPickup(build)){
                size *= build.block.size;

                drawX = build.x;
                drawY = build.y;
            }
        }

        Drawf.dashRect(Tmp.c1.set(Pal.accent).a(0.6f), drawX - size / 2, drawY - size / 2, size, size);

        Draw.reset();
    }

}