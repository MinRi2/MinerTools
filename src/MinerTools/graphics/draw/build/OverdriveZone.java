package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.OverdriveProjector.*;

import java.lang.reflect.*;

public class OverdriveZone extends BuildDrawer<OverdriveBuild>{
    private static final Field heatField = MinerUtils.getField(OverdriveBuild.class, "heat");
    private static final Field phaseHeatField = MinerUtils.getField(OverdriveBuild.class, "phaseHeat");

    public OverdriveZone(){
        super(block -> block instanceof OverdriveProjector);
    }

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("overdriveZone");
    }

    @Override
    public boolean isValid(OverdriveBuild building){
        return super.isValid(building) && building.consValid();
    }

    @Override
    protected void draw(OverdriveBuild build){
        OverdriveProjector  block = (OverdriveProjector)build.block;

        float heat = MinerUtils.getValue(heatField, build);
        float phaseHeat = MinerUtils.getValue(phaseHeatField, build);

        float realRange = heat * block.range + phaseHeat * block.phaseRangeBoost;

        Draw.z(MLayer.overdriveZone);

        Draw.color(block.baseColor);
        Fill.circle(build.x, build.y, realRange);

        Draw.reset();
    }

}
