package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.ShieldWall.*;

public class BuildHealthBar extends BuildDrawer<Building>{
    public static float healthBarStroke = 1.7f, healthBarAlpha = 0.85f;
    public static float backBarStroke = healthBarStroke + 1.3f, backBarAlpha = healthBarAlpha - 0.25f;

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("buildHealthBar");
    }

    @Override
    public boolean isValid(Building building){
        return super.isValid(building) && building.team != Team.derelict;
    }

    @Override
    protected void draw(Building build){
        float startX = build.x - build.hitSize() / 2f + build.hitSize() / 6f, startY = build.y - build.hitSize() / 2f + backBarStroke;
        float endX = build.x + build.hitSize() / 2f - build.hitSize() / 6f;

        Draw.z(Layer.power - 1f);

        if(building.damaged()){
            MDrawf.drawProgressBar(
                startX, startY, endX, startY, build.healthf(),
                backBarStroke, backBarAlpha, build.team.color,
                healthBarStroke, healthBarAlpha, Pal.health
            );

            startY += backBarStroke;
        }

        if(build instanceof ShieldWallBuild shieldWall && shieldWall.shield > 0.001f){
            ShieldWall block = (ShieldWall)shieldWall.block;

            MDrawf.drawProgressBar(
                startX, startY, endX, startY, shieldWall.shield / block.shieldHealth,
                backBarStroke, backBarAlpha, shieldWall.team.color,
                healthBarStroke, healthBarAlpha, Pal.shield
            );

            // start += backBarStroke;
        }

        Draw.reset();
    }

}
