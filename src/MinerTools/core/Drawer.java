package MinerTools.core;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
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

    /* Alert */
    public static float unitAlertRadius;
    public static float turretAlertRadius;

    private static float enemyRadius = defEnemyRadius;

    private static final Boolf<Unit> unitAlertValid = unit -> {
        UnitType type = unit.type;
        return (type.weapons.any()) && // in white list
        (unit.team != player.team()) && // isEnemy
        (unit.ammo > 0f) && // hasAmmo
        (player.unit().isFlying() ? type.targetAir : type.targetGround) && // can hit player
        (unit.within(player, unitAlertRadius + type.maxRange)); // within player
    };

    private static final Boolf<TurretBuild> turretAlertValid = turretBuild -> {
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

            var cores = player.team().cores();
            Groups.unit.each(unit ->{
                if(mSettings.getBool("unitAlert")){
                    unitAlert(unit);
                }

                if(mSettings.getBool("enemyUnitIndicator") && cores.any()){
                    enemyIndicator(unit, cores);
                }
            });
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
        unitAlertRadius = mSettings.getInt("unitAlertRadius") * tilesize;
    }


    /**
     * 敌方单位警戒
     */
    public static void unitAlert(Unit unit){
        if(unitAlertValid.get(unit)){
            Draw.z(Layer.flyingUnit + 0.1f);

            Lines.stroke(1.2f, unit.team.color);
            Lines.dashCircle(unit.x, unit.y, unit.range());

            float dst = unit.dst(player);
            if(dst > unit.range()){
                Tmp.v1.set(unit).sub(player).setLength(dst - unit.range());
                Draw.rect(unit.type.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f + unit.hitSize / 3f, 10f + unit.hitSize / 3f, Tmp.v1.angle() - 90f);
            }

            Draw.reset();
        }
    }

    /**
     * 敌方单位指示器
     */
    public static void enemyIndicator(Unit unit, Seq<CoreBuild> cores){
        if(unit.team == player.team()) return;

        final float[] length = {0f};

        var wCores = cores.select(c -> c.within(unit, enemyRadius));
        if(wCores.isEmpty()) return;

        CoreBuild core = wCores.min(c -> length[0] = unit.dst(c));

        Draw.z(Layer.flyingUnit + 0.1f);

        float indicatorLength = Mathf.lerp(20f, 55f, length[0] / enemyRadius);

        Tmp.v1.set(unit).sub(player).setLength(indicatorLength);

        Draw.color(unit.team.color);
        Draw.rect(unit.type.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f, 10f,  Tmp.v1.angle() - 90f);

        Draw.reset();
    }

    /**
     * 敌方炮塔警戒
     */
    public static void turretAlert(TurretBuild turret){
        if(turretAlertValid.get(turret)){
            Draw.z(Layer.turret + 0.1f);

            Lines.stroke(1.2f, turret.team.color);
            Lines.dashCircle(turret.x, turret.y, turret.range());

            float dst = turret.dst(player);
            if(dst > turret.range()){
                Tmp.v1.set(turret).sub(player).setLength(dst - turret.range());
                Draw.rect(turret.block.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f + turret.block.size * 3f, 10f + turret.block.size * 3f, Tmp.v1.angle() - 90f);
            }

            Draw.reset();
        }

        UnitTypes.eclipse.weapons.addAll(UnitTypes.reign.weapons);
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
