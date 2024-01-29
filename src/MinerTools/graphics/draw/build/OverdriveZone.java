package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.OverdriveProjector.*;

import static mindustry.Vars.renderer;

public class OverdriveZone extends BuildDrawer<OverdriveBuild>{

    public OverdriveZone(){
        super(block -> block instanceof OverdriveProjector);
    }

    @Override
    public boolean isEnabled(){
        return MinerVars.settings.getBool("overdriveZone");
    }

    @Override
    public boolean shouldDraw(OverdriveBuild building){
        return super.shouldDraw(building) && building.canConsume();
    }

    @Override
    public void drawShader(){
        Draw.drawRange(MLayer.overdriveZone, 0.2f, () -> renderer.effectBuffer.begin(Color.clear), () -> {
            renderer.effectBuffer.end();
            renderer.effectBuffer.blit(MShaders.overdriveZone);
        });
    }

    @Override
    protected void draw(OverdriveBuild build){
        OverdriveProjector block = (OverdriveProjector)build.block;

        float realRange = block.range + build.phaseHeat * block.phaseRangeBoost;

        Draw.z(MLayer.overdriveZone);

        Draw.color(block.baseColor);
        Fill.circle(build.x, build.y, realRange);

        Draw.reset();
    }

}
