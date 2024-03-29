package MinerTools.graphics.draw.unit;

import MinerTools.*;
import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.payloads.*;

public class UnitInfoBar extends UnitDrawer{
    public static float healthBarStroke = 1.7f, healthBarAlpha = 0.85f;
    public static float backBarStroke = healthBarStroke + 1.3f, backBarAlpha = healthBarAlpha - 0.25f;

    @Override
    public boolean isEnabled(){
        return MinerVars.settings.getBool("unitInfoBar");
    }

    @Override
    protected void draw(Unit unit){
        float startX = unit.x - unit.hitSize / 2f, startY = unit.y + unit.hitSize / 2f;
        float endX = unit.x + unit.hitSize / 2f;

        float iconSize = unit.hitSize / Vars.tilesize;

        Draw.z(Layer.flyingUnit + 0.1f);

        /* HealthBar */
        if(unit.damaged()){
            MDrawf.drawProgressBar(
            startX, startY, endX, startY, unit.healthf(),
            backBarStroke, backBarAlpha, unit.team().color,
            healthBarStroke, healthBarAlpha, Pal.health
            );

            startY += backBarStroke + 0.5f;
        }

        Draw.color();

        /* Shield Bar */
        var abilities = unit.abilities;
        if(abilities.length > 0){
            Ability ability = Structs.find(abilities, a -> a instanceof ForceFieldAbility);

            if(ability instanceof ForceFieldAbility forceFieldAbility){
                MDrawf.drawProgressBar(
                startX, startY, endX, startY, unit.shield / forceFieldAbility.max,
                backBarStroke, backBarAlpha, unit.team().color,
                healthBarStroke, healthBarAlpha, Pal.shield
                );

                startY += healthBarStroke + 0.5f;
            }


            Draw.color();
        }

        if(!Mathf.zero(unit.drownTime)){
            MDrawf.drawProgressBar(
            startX, startY, endX, startY, (1f - unit.drownTime),
            backBarStroke, backBarAlpha, unit.team().color,
            healthBarStroke, healthBarAlpha, Liquids.water.color
            );

            startY += healthBarStroke + 0.5f;
        }

        /* Status */
        Bits applied = unit.statusBits();
        int statusSize = applied.length();

        if(statusSize != 0){
            startY += iconSize / 2;

            int index = 0;
            for(StatusEffect effect : Vars.content.statusEffects()){
                if(applied.get(effect.id)){
                    Draw.color(effect.color);
                    Draw.rect(effect.uiIcon, startX + (index++ * iconSize), startY, iconSize, iconSize);
                }
            }

            startY += iconSize / 2;
        }

        Draw.color();

        /* Payloads */
        if(unit instanceof PayloadUnit payloadUnit){
            Seq<Payload> payloads = payloadUnit.payloads;

            startY += iconSize / 2;

            int index = 0;
            if(payloads.any()){
                for(Payload payload : payloads){
                    Draw.rect(payload.icon(), startX + (index++ * iconSize), startY, iconSize, iconSize);
                }
            }

//            startY += iconSize / 2;
        }

        Draw.reset();
    }
}
