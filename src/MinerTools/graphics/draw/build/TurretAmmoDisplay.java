package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.ItemTurret.*;

import static mindustry.Vars.tilesize;


/**
 * 炮塔子弹类型显示
 */
public class TurretAmmoDisplay extends BuildDrawer<ItemTurretBuild>{

    @Override
    public void readSetting(){}

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("itemTurretAmmoShow");
    }

    @Override
    public boolean isValid(){
        return true;
    }

    @Override
    public boolean isValid(ItemTurretBuild turret){
        return !turret.ammo.isEmpty();
    }

    @Override
    public void draw(ItemTurretBuild turret){
        ItemTurret block = (ItemTurret)turret.block;
        ItemEntry entry = (ItemEntry)turret.ammo.peek();

        Item item = Reflect.get(entry, "item");

        Draw.z(Layer.turret + 0.1f);

        float size = Math.max(6f, block.size * tilesize / 2f);
        float x = turret.x + block.size * tilesize / 3f;
        float y = turret.y + block.size * tilesize / 3f;

        float s = Mathf.lerp(3f, size, Math.min(1f, (float)entry.amount / block.maxAmmo));

        Draw.alpha(0.45f);
        Draw.rect(item.uiIcon, x, y, size, size);
        Draw.alpha(1f);
        Draw.rect(item.uiIcon, x, y, s, s);

        Draw.reset();
    }

}
