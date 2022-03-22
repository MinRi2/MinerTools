package MinerTools.core;

import arc.*;
import arc.func.*;
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
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.meta.*;

import static MinerTools.MinerVars.mSettings;
import static mindustry.Vars.*;

public class Drawer{
    public static float defEnemyRadius;
    public static float turretAlertRadius;

    private static float enemyRadius = defEnemyRadius;

    private static final Boolf<TurretBuild> alertValid = turretBuild -> {
        Turret block = (Turret)turretBuild.block;
        return (turretBuild.team != player.team()) && // isEnemy
        (turretBuild.cons.status() == BlockStatus.active && turretBuild.hasAmmo()) && // hasAmmo
        (player.unit().isFlying() ? block.targetAir : block.targetGround) && // can hit player
        (turretBuild.within(player, turretAlertRadius + block.range)); // within player
    };

    public static void setEvents(){
        readDef();

        Events.run(Trigger.draw, () -> {
            Groups.build.each(building -> {
                if(building instanceof TurretBuild turretBuild){
                    if(mSettings.getBool("turretAlert")){
                        turretAlert(turretBuild);
                    }

                    if(mSettings.getBool("itemTurretAmmoShow") && building instanceof ItemTurretBuild itemTurretBuild){
                        itemTurretAmmo(itemTurretBuild);
                    }
                }
            });

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

    public static void readDef(){
        defEnemyRadius = mSettings.getInt("enemyUnitIndicatorRadius") * tilesize;
        turretAlertRadius = mSettings.getInt("turretAlertRadius") * tilesize;
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
     * 炮塔警戒
     */
    public static void turretAlert(TurretBuild turret){
        if(alertValid.get(turret)){
            Draw.z(Layer.turret + 0.1f);

            Lines.stroke(1.2f, turret.team.color);
            Lines.dashCircle(turret.x, turret.y, turret.range());

            float dst = turret.dst(player);
            if(dst > turret.range()){
                Tmp.v1.set(turret).sub(player).setLength(dst - turret.range());
                Draw.rect(turret.block.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f, 10f,  Tmp.v1.angle() - 90f);
            }

            Draw.reset();
        }
    }

    /**
     * 炮塔子弹显示
     */
    public static void itemTurretAmmo(ItemTurretBuild turret){
        if(!turret.ammo.isEmpty()){
            ItemTurret block = (ItemTurret)turret.block;
            ItemEntry entry = (ItemEntry)turret.ammo.peek();

            Item item = Reflect.get(entry, "item");

            Draw.z(Layer.turret + 0.1f);

            float size = Math.max(6f, block.size * tilesize / 2f);
            float x = turret.x + block.size * tilesize / 3f;
            float y = turret.y + block.size * tilesize / 3f;

            float s = Mathf.lerp(6f, size , (float)entry.amount / block.maxAmmo);
            Draw.rect(item.uiIcon, x, y, s, s);
            Draw.alpha(0.75f);
            Draw.rect(item.uiIcon, x, y, size, size);

            Draw.reset();
        }
    }
}
