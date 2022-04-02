package MinerTools.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.ConstructBlock.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.ItemTurret.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.meta.*;

import static MinerTools.MinerVars.mSettings;
import static arc.Core.input;
import static mindustry.Vars.*;

public class Drawer{
    public static float defEnemyRadius;

    /* Alert */
    public static float unitAlertRadius;
    public static float turretAlertRadius;

    private static float enemyRadius = defEnemyRadius;

    private static final Seq<Building> tmp = new Seq<>();

    public static void setEvents(){
        readDef();

        Events.run(Trigger.draw, () -> {
            Vec2 v = input.mouseWorld();
            Building select = world.build(World.toTile(v.x), World.toTile(v.y));
            if(select != null){
                drawSelect(select);
            }

            Seq<TeamData> activeTeams = state.teams.getActive();
            for(TeamData data : activeTeams){
                if(data.buildings == null){
                    continue;
                }

                tmp.clear();

                data.buildings.getObjects(tmp);
                for(Building building : tmp){
                    if(building instanceof TurretBuild turretBuild){
                        if(!player.isNull() && mSettings.getBool("turretAlert")){
                            turretAlert(turretBuild);
                        }

                        if(mSettings.getBool("itemTurretAmmoShow") && building instanceof ItemTurretBuild itemTurretBuild){
                            itemTurretAmmo(itemTurretBuild);
                        }
                    }
                }

                tmp.clear();

                var cores = player.team().cores();
                for(Unit unit : data.units){
                    if(!player.isNull() && mSettings.getBool("unitAlert")){
                        unitAlert(unit);
                    }

                    if(mSettings.getBool("enemyUnitIndicator") && cores.any()){
                        enemyIndicator(unit, cores);
                    }
                }
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
        unitAlertRadius = mSettings.getInt("unitAlertRadius") * tilesize;
    }

    public static float drawText(String text, float scl, float dx, float dy, Color color, int halign){
        Font font = Fonts.outline;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(scl);
        layout.setText(font, text);

        float height = layout.height;

        font.setColor(color);
        font.draw(text, dx, dy + layout.height + 1, halign);

        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);

        return height;
    }

    public static void drawSelect(Building building){
        if(building instanceof ConstructBuild c){
            constructInfo(c);
        }
    }

    /**
     * 构造中的建筑的耗材信息
     */
    public static void constructInfo(ConstructBuild c){
        if(c.team.core() != null){
            // BlockUnit之上
            Draw.z(Layer.flyingUnit + 0.1f);

            float scl = c.block.size / 8f / 2f / Scl.scl(1f);

            drawText(String.format("%.2f", c.progress * 100) + "%", scl, c.x, c.y + c.block.size * tilesize / 2f, Pal.accent, Align.center);

            float nextPad = 0f;
            for(int i = 0; i < c.current.requirements.length; i++){
                ItemStack stack = c.current.requirements[i];

                float dx = c.x - (c.block.size * tilesize) / 2f, dy = c.y - (c.block.size * tilesize) / 2f + nextPad;
                boolean hasItem = (1.0f - c.progress) * state.rules.buildCostMultiplier * stack.amount <= c.team.core().items.get(stack.item);

                nextPad += drawText(
                stack.item.emoji() + (int)(c.progress * state.rules.buildCostMultiplier * stack.amount) + "/" +
                (int)(state.rules.buildCostMultiplier * stack.amount) + "/" +
                UI.formatAmount(c.team.core().items.get(stack.item)),
                scl, dx, dy, hasItem ? Pal.accent : Pal.remove, Align.left);
                nextPad ++;
            }
        }
    }

    private static boolean unitAlertValid(Unit unit){
        UnitType type = unit.type;
        return (type.hasWeapons()) && // has weapons
        (unit.team != player.team()) && // isEnemy
        (!state.rules.unitAmmo || unit.ammo > 0f) && // hasAmmo
        (player.unit().isFlying() ? type.targetAir : type.targetGround) && // can hit player
        (unit.within(player, unitAlertRadius + type.maxRange)); // within player
    };

    private static boolean turretAlertValid(TurretBuild turretBuild){
        Turret block = (Turret)turretBuild.block;
        return (turretBuild.team != player.team()) && // isEnemy
        (turretBuild.cons.status() == BlockStatus.active && turretBuild.hasAmmo()) && // hasAmmo
        (player.unit().isFlying() ? block.targetAir : block.targetGround) && // can hit player
        (turretBuild.within(player, turretAlertRadius + block.range)); // within player
    };

    /**
     * 敌方单位警戒
     */
    public static void unitAlert(Unit unit){
        if(unitAlertValid(unit)){
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
        Draw.rect(unit.type.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f, 10f, Tmp.v1.angle() - 90f);

        Draw.reset();
    }

    /**
     * 敌方炮塔警戒
     */
    public static void turretAlert(TurretBuild turret){
        if(turretAlertValid(turret)){
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
    }

    /**
     * 炮塔子弹显示
     */
    public static void itemTurretAmmo(ItemTurretBuild turret){
        if(!turret.ammo.isEmpty()){
            ItemTurret block = (ItemTurret)turret.block;
            ItemEntry entry = (ItemEntry)turret.ammo.peek();

            Item item = entry.item;

            Draw.z(Layer.turret + 0.1f);

            float size = Math.max(6f, block.size * tilesize / 2f);
            float x = turret.x + block.size * tilesize / 3f;
            float y = turret.y + block.size * tilesize / 3f;

            float s = Mathf.lerp(6f, size, Math.min(1f, (float)entry.amount / block.maxAmmo));
            Draw.rect(item.uiIcon, x, y, s, s);
            Draw.alpha(0.75f);
            Draw.rect(item.uiIcon, x, y, size, size);

            Draw.reset();
        }
    }
}
