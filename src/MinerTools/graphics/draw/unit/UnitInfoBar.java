package MinerTools.graphics.draw.unit;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.payloads.*;

public class UnitInfoBar extends UnitDrawer{
    public static float healthBarStroke = 1.7f, healthBarAlpha = 0.85f;
    public static float backBarStroke = healthBarStroke + 1.3f, backBarAlpha = healthBarAlpha - 0.25f;

    @Override
    public boolean enabled(){
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
            /* Background */
            Lines.stroke(backBarStroke, unit.team().color);
            Draw.alpha(backBarAlpha);
            Lines.line(startX, startY, endX, startY);

            Lines.stroke(healthBarStroke, Pal.health);
            Draw.alpha(healthBarAlpha);
            Lines.line(startX, startY, startX + (endX - startX) * unit.healthf(), startY);

            startY += backBarStroke;
        }

        Draw.color();

        /* Shield Bar */
        var abilities = unit.abilities;
        if(abilities.length > 0){
            Ability ability = Structs.find(abilities, a -> a instanceof ForceFieldAbility);

            if(ability instanceof ForceFieldAbility forceFieldAbility){
                Lines.stroke(healthBarStroke, Pal.shield);
                Draw.alpha(healthBarAlpha);

                Lines.line(startX, startY, startX + (endX - startX) * (unit.shield / forceFieldAbility.max), startY);
                
                startY += healthBarStroke;
            }


            Draw.color();
        }

        /* Status */
        Bits applied = unit.statusBits();
        int statusSize = applied.length();

        if(statusSize != 0){
            startY += iconSize / 2;

            int index = 0;
            for(StatusEffect effect : Vars.content.statusEffects()){
                if(applied.get(effect.id) && !effect.isHidden()){
                    Draw.color(effect.color);
                    Draw.rect(effect.fullIcon, startX + (index++ * iconSize), startY, iconSize, iconSize);
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
