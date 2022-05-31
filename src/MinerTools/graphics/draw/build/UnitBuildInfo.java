package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.Reconstructor.*;
import mindustry.world.blocks.units.UnitBlock.*;
import mindustry.world.blocks.units.UnitFactory.*;

public class UnitBuildInfo extends BuildDrawer<UnitBuild>{
    public static float proBarStroke = 1.7f, proBarAlpha = 0.75f;
    public static float backBarStroke = proBarStroke + 1.3f, backBarAlpha = 0.1f;

    public UnitBuildInfo(){
        super(block -> block instanceof UnitBlock);
    }

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("unitBuildInfo");
    }

    @Override
    protected void draw(UnitBuild build){
        UnitBlock block = (UnitBlock)build.block;

        float fraction = 0f;
        float conTime = 0f;
        if(build instanceof UnitFactoryBuild facBuild){
            UnitFactory factory = (UnitFactory)block;
            fraction = facBuild.fraction();
            conTime = facBuild.currentPlan == -1 ? 0 : (1 - facBuild.fraction()) * factory.plans.get(facBuild.currentPlan).time / facBuild.timeScale();
        }else if(build instanceof ReconstructorBuild reconBuild){
            Reconstructor reconstructor = (Reconstructor)block;
            fraction = reconBuild.fraction();
            conTime = (1 - reconBuild.fraction()) * reconstructor.constructTime / reconBuild.timeScale();
        }

        float size = block.size * Vars.tilesize;

        float startX = build.x - size / 2f + 5f;
        float startY = build.y + size / 2f - 5f;

        float endX = startX + size - 5f * 2;

        float drawX = startX + (endX - startX) * fraction;

        Draw.z(Layer.power + 1f);

        // Background bar
        Lines.stroke(backBarStroke, build.team.color);
        Draw.alpha(backBarAlpha);
        Lines.line(startX, startY, endX, startY);

        // Process bar
        Lines.stroke(proBarStroke, Pal.accent);
        Draw.alpha(proBarAlpha);
        Lines.line(startX, startY, drawX, startY);

        startY += backBarStroke;

        float scl = block.size / 8f / 2f / Scl.scl(1f);

        Renderer.drawText(Strings.autoFixed(conTime / 60f, 1) + "s", scl, drawX, startY, Color.white, Align.center);

        Draw.reset();
    }
}
