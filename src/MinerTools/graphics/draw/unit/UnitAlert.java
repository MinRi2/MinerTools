package MinerTools.graphics.draw.unit;

import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static MinerTools.MinerVars.mSettings;
import static mindustry.Vars.*;

/**
 * 敌方单位警戒
 */
public class UnitAlert extends UnitDrawer{
    public float unitAlertRadius;

    @Override
    public void readSetting(){
        unitAlertRadius = mSettings.getInt("unitAlertRadius") * tilesize;
    }

    @Override
    public boolean enabled(){
        return mSettings.getBool("unitAlert");
    }

    @Override
    public boolean isValid(){
        return !player.unit().isNull();
    }

    @Override
    public boolean isValid(Unit unit){
        UnitType type = unit.type;
        return (type.hasWeapons()) && // has weapons
        (unit.team != player.team()) && // isEnemy
        (!state.rules.unitAmmo || unit.ammo > 0f) && // hasAmmo
        (player.unit().isFlying() ? type.targetAir : type.targetGround) && // can hit player
        (unit.within(player, unitAlertRadius + type.maxRange)); // within player
    }

    @Override
    public void draw(Unit unit){
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
