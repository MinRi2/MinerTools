package MinerTools.core;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.ItemTurret.*;
import mindustry.world.blocks.storage.CoreBlock.*;

import static MinerTools.MinerVars.mSettings;
import static mindustry.Vars.*;

public class Drawer{
    public static float defEnemyRadius;

    private static float enemyRadius = defEnemyRadius;

    public static void setDefEnemyRadius(float r){
        defEnemyRadius = r;
    }

    public static void setEvents(){
        defEnemyRadius = mSettings.getInt("enemyUnitIndicatorRadius") * tilesize;

        Events.run(Trigger.draw, () -> {
            if(mSettings.getBool("itemTurretAmmoShow")){
                Groups.build.each(building -> building instanceof ItemTurretBuild, Drawer::itemTurretAmmo);
            }
            if(mSettings.getBool("enemyUnitIndicator")){
                enemyIndicator();
            }
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            if(state.rules.polygonCoreProtection){
                enemyRadius = defEnemyRadius;
            }else{
                enemyRadius = Math.max(state.rules.enemyCoreBuildRadius, defEnemyRadius);
            }
        });
    }

    /**
     * 敌方单位指示器
     */
    public static void enemyIndicator(){
        Seq<CoreBuild> cores = player.team().cores();

        if(cores.isEmpty()){
            return;
        }

        final float[] length = {0f};

        Draw.z(Layer.flyingUnit + 0.1f);
        Groups.unit.each(unit -> {
            CoreBuild core = cores.min(c -> length[0] = unit.dst(c));
            return unit.team != player.team() && core != null && core.within(unit, enemyRadius);
        }, unit -> {
            float enemyIndicatorLength = Mathf.lerp(20f, 55f, length[0] / enemyRadius);

            Tmp.v1.set(unit).sub(player).setLength(enemyIndicatorLength);

            Draw.color(unit.team.color);
            Draw.rect(unit.type.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f, 10f,  Tmp.v1.angle() - 90f);
        });
        Draw.reset();
    }

    /**
     * 炮塔子弹显示
     */
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
