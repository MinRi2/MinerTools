package MinerTools.core;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.ItemTurret.*;

import static mindustry.Vars.tilesize;

public class Drawer{

    public static void setEvents(){
        Events.run(Trigger.draw, () -> {
            Groups.build.each(building -> building instanceof ItemTurretBuild, Drawer::itemTurretAmmo);
        });
    }

    public static void itemTurretAmmo(Building building){
        if(building instanceof ItemTurretBuild turretBuild && !turretBuild.ammo.isEmpty()){
            ItemTurret block = (ItemTurret)turretBuild.block;
            ItemEntry entry = (ItemEntry)turretBuild.ammo.peek();

            Item item = Reflect.get(entry, "item");

            Draw.z(Layer.turret + 0.1f);

            float size = Math.max(6f, block.size * tilesize / 2f);
            float x = turretBuild.x + block.size * tilesize / 3f;
            float y = turretBuild.y + block.size * tilesize / 3f;

            float s = Mathf.lerp(6f, size , (float)entry.amount / block.maxAmmo);
            Draw.rect(item.uiIcon, x, y, s, s);
            Draw.alpha(0.75f);
            Draw.rect(item.uiIcon, x, y, size, size);

            /*
            Font font = Fonts.outline;
            GlyphLayout lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);

            font.getData().setScale(1f / 4f / Scl.scl(1f));
            lay.setText(font, "" + entry.amount);

            font.setColor(turretBuild.team.color);
            font.draw("" + entry.amount, x - lay.width / 2f, y + lay.height / 2f + 1);

            font.getData().setScale(1f);
            Pools.free(lay);
            */
            Draw.reset();
        }
    }
}
