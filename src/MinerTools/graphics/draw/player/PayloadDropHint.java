package MinerTools.graphics.draw.player;

import MinerTools.*;
import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.entities.*;
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
                buildDropHint(unit, p);
            }else if(payload instanceof UnitPayload p){
                unitDropHint(unit, p);
            }
        }

        if(unit.payloadUsed() + Vars.tilesize < unit.type.payloadCapacity){
            pickUpHint(unit);
        }

        Draw.reset();
    }

    private static void pickUpHint(PayloadUnit unit){
        if(!unitPickUpHint(unit)){
            buildPickUpHint(unit);
        }
    }

    private static void buildPickUpHint(PayloadUnit unit){
        Tile tile = unit.tileOn();

        if(tile != null){
            Building build = tile.build;

            if(build != null && build.interactable(unit.team) && unit.canPickup(tile.build)){
                Block block = build.block;

                float size = block.size * Vars.tilesize + block.offset;

                Draw.mixcol(Pal.accent, 0.24f + Mathf.absin(Time.globalTime, 6f, 0.28f));
                Draw.alpha(0.8f);
                Draw.rect(block.fullIcon, tile.build.x, tile.build.y, size, size, build.rotation * 90);

                Draw.reset();
            }

            float drawX = tile.drawx(), drawY = tile.drawy();
            float size = Vars.tilesize;

            if(build != null && build.interactable(unit.team)){
                if(unit.canPickup(build)){
                    size *= build.block.size;

                    drawX = build.x;
                    drawY = build.y;
                }
            }

            MDrawf.dashRect(Tmp.c1.set(Pal.accent).a(0.6f), drawX - size / 2, drawY - size / 2, size, size);

            Draw.reset();
        }
    }

    private static boolean unitPickUpHint(PayloadUnit unit){
        Unit target = Units.closest(unit.team(), unit.x, unit.y, unit.type.hitSize * 2f, u -> u.isAI() && u.isGrounded() && unit.canPickup(u) && u.within(unit, u.hitSize + unit.hitSize));

        if(target != null && target.team == unit.team){
            Draw.mixcol(Pal.accent, 0.24f + Mathf.absin(Time.globalTime, 6f, 0.28f));
            Draw.alpha(0.8f);
            Draw.rect(target.type.fullIcon, target.x, target.y, target.rotation - 90);

            Draw.reset();

            return true;
        }

        return false;
    }

    private static void buildDropHint(PayloadUnit unit, BuildPayload payload){
        Building build = payload.build;
        int tx = World.toTile(unit.x - build.block.offset);
        int ty = World.toTile(unit.y - build.block.offset);

        Tile on = Vars.world.tile(tx, ty);
        if(on != null){
            boolean valid = Build.validPlace(build.block, build.team, tx, ty, build.rotation, false);

            Block block = build.block;

            float size = block.size * Vars.tilesize + block.offset;

            int rot = block.rotate ? (int)((unit.rotation + 45f) / 90f) % 4  * 90 : 0;

            Draw.mixcol(!valid ? Pal.breakInvalid : Color.white, (!valid ? 0.4f : 0.24f) + Mathf.absin(Time.globalTime, 6f, 0.28f));
            Draw.alpha(0.8f);
            Draw.rect(block.fullIcon, (on.x + 0.5f) * Vars.tilesize, (on.y + 0.5f) * Vars.tilesize, size, size, rot);

            Draw.reset();
        }
    }

    private static void unitDropHint(PayloadUnit unit, UnitPayload payload){
        Unit u = payload.unit;

        boolean valid = u.canPass(unit.tileX(), unit.tileY()) && Units.count(unit.x, unit.y, u.physicSize(), Flyingc::isGrounded) <= 1;

        Draw.mixcol(!valid ? Pal.breakInvalid : Color.white, (!valid ? 0.4f : 0.24f) + Mathf.absin(Time.globalTime, 6f, 0.28f));
        Draw.alpha(0.8f);
        Draw.rect(u.type.fullIcon, unit.x, unit.y, unit.rotation - 90);

        Draw.reset();
    }

}
