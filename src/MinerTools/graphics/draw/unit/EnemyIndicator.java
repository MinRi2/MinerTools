package MinerTools.graphics.draw.unit;

import MinerTools.graphics.draw.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.storage.CoreBlock.*;

import static MinerTools.MinerVars.mSettings;
import static mindustry.Vars.*;

/**
 * 敌方单位指示器
 */
public class EnemyIndicator extends UnitDrawer{
    public float defEnemyRadius;

    private float enemyRadius = defEnemyRadius;

    private Seq<CoreBuild> cores;

    public EnemyIndicator(){
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if(state.rules.polygonCoreProtection){
                enemyRadius = defEnemyRadius;
            }else{
                enemyRadius = Math.max(state.rules.enemyCoreBuildRadius, defEnemyRadius);
            }
        });
    }

    @Override
    public void readSetting(){
        defEnemyRadius = mSettings.getInt("enemyUnitIndicatorRadius") * tilesize;
    }

    @Override
    public boolean enabled(){
        return mSettings.getBool("enemyUnitIndicator");
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
        return unit.team != player.team();
    }

    @Override
    public void draw(Unit unit){
        final float[] length = {0f};

        var wCores = cores.select(c -> c.within(unit, enemyRadius));
        if(wCores.isEmpty()) return;

        CoreBuild core = wCores.min(c -> length[0] = unit.dst(c));

        Draw.z(Layer.overlayUI);

        float indicatorLength = Mathf.lerp(20f, 55f, length[0] / enemyRadius);

        Tmp.v1.set(unit).sub(player).setLength(indicatorLength);

        Draw.color(unit.team().color);
        Draw.rect(unit.type.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f, 10f, Tmp.v1.angle() - 90f);

        Draw.reset();
    }

}
