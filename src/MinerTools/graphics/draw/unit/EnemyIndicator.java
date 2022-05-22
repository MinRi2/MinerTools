package MinerTools.graphics.draw.unit;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.storage.CoreBlock.*;

import static mindustry.Vars.*;

/**
 * 敌方单位指示器
 */
public class EnemyIndicator extends UnitDrawer{
    public float defEnemyRadius;

    private float enemyRadius = defEnemyRadius;

    private final Vec2 cameraPos;
    private Seq<CoreBuild> cores;

    public EnemyIndicator(){
        cameraPos = Core.camera.position;

        Events.on(EventType.WorldLoadEvent.class, e -> resetEnemyRadius());
    }

    private void resetEnemyRadius(){
        if(state.rules.polygonCoreProtection){
            enemyRadius = defEnemyRadius;
        }else{
            enemyRadius = Math.max(state.rules.enemyCoreBuildRadius, defEnemyRadius);
        }
    }

    @Override
    public void readSetting(){
        defEnemyRadius = MinerVars.settings.getInt("enemyUnitIndicatorRadius") * tilesize;
        resetEnemyRadius();
    }

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("enemyUnitIndicator");
    }

    @Override
    public boolean isValid(){
        return player.team().data().hasCore();
    }

    @Override
    public void init(){
        cores = player.team().cores();
    }

    @Override
    public boolean isValid(Unit unit){
        return unit.team != player.team() && unit.hasWeapons();
    }

    @Override
    public void draw(Unit unit){
        var wCores = cores.select(c -> c.within(unit, enemyRadius));
        if(wCores.isEmpty()) return;

        CoreBuild core = wCores.min(c -> unit.dst(c));

        Draw.z(Layer.overlayUI);

        float indicatorLength = Mathf.lerp(20f, 55f, unit.dst(core) / enemyRadius);

        Tmp.v1.set(unit).sub(cameraPos).setLength(indicatorLength);

        Draw.color(unit.team().color);
        Draw.rect(unit.type.fullIcon, cameraPos.x + Tmp.v1.x, cameraPos.y + Tmp.v1.y, 10f, 10f, Tmp.v1.angle() - 90f);

        Draw.reset();
    }

}
