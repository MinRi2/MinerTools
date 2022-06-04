package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.ItemTurret.*;

import java.lang.reflect.*;

import static mindustry.Vars.tilesize;


/**
 * 炮塔子弹类型显示
 */
public class TurretAmmoDisplay extends BuildDrawer<ItemTurretBuild>{
    private static Field itemField;

    static{
        itemField = MinerUtils.getField(ItemEntry.class, "item");
    }

    public TurretAmmoDisplay(){
        super(block -> block instanceof ItemTurret);
    }

    @Override
    public void readSetting(){}

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("itemTurretAmmoShow");
    }

    @Override
    public boolean isValid(ItemTurretBuild turret){
        return super.isValid(turret) && turret.ammo.any();
    }

    @Override
    protected void draw(ItemTurretBuild turret){
        ItemTurret block = (ItemTurret)turret.block;
        ItemEntry entry = (ItemEntry)turret.ammo.peek();

        Item item = MinerUtils.getValue(itemField, entry);

        Draw.z(Layer.turret + 0.1f);

        float maxSize = Math.max(6f, block.size * tilesize / 2f);
        float x = turret.x + block.size * tilesize / 3f;
        float y = turret.y + block.size * tilesize / 3f;

        float realSize = Mathf.lerp(3f, maxSize, Math.min(1f, (float)entry.amount / block.maxAmmo));

        Draw.alpha(0.45f);
        Draw.rect(item.uiIcon, x, y, maxSize, maxSize);

        Draw.alpha(1f);
        Draw.rect(item.uiIcon, x, y, realSize, realSize);

        Draw.reset();
    }

}
