package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class BuildHealthBar extends BuildDrawer<Building>{
    public static float healthBarStroke = 1.7f, healthBarAlpha = 0.85f;
    public static float backBarStroke = healthBarStroke + 1.3f, backBarAlpha = healthBarAlpha - 0.25f;

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("buildHealthBar");
    }

    @Override
    public boolean isValid(Building building){
        return super.isValid(building) && building.team != Team.derelict && building.damaged();
    }

    @Override
    protected void draw(Building build){
        float startX = build.x - build.hitSize() / 2f + 5f, startY = build.y - build.hitSize() / 2f + backBarStroke;
        float endX = build.x + build.hitSize() / 2f - 5f;

        MDrawf.drawProgressBar(
            startX, startY, endX, startY, build.healthf(),
            backBarStroke, backBarAlpha, build.team.color,
            healthBarStroke, healthBarAlpha, Pal.health
        );

        Draw.reset();
    }

}
