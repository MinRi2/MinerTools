package MinerTools.graphics.draw.build;

import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitAssembler.*;

public class UnitAssemblerInfo extends BuildDrawer<UnitAssemblerBuild>{
    public static float proBarStroke = 1.7f, proBarAlpha = 0.85f;
    public static float backBarStroke = proBarStroke + 1.3f, backBarAlpha = 0.5f;

    public UnitAssemblerInfo(){
        super(block -> block instanceof UnitAssembler);
    }

    @Override
    public boolean isValid(UnitAssemblerBuild building){
        return super.isValid(building) && Math.min(building.powerWarmup, building.sameTypeWarmup) > 0;
    }

    @Override
    protected void draw(UnitAssemblerBuild build){
        UnitAssembler block = (UnitAssembler)build.block;

        var spawnPos = build.getUnitSpawn().add(0, 14f);

        float size = build.plan().unit.hitSize;
        float x = spawnPos.x, y = spawnPos.y;

        float fraction = build.progress;
        float startX = x - size / 2f, startY = y + size / 2f;
        float endX = x + size / 2f;

        Draw.z(Layer.blockOver + 0.2f + 0.1f);

        MDrawf.drawProgressBar(
            startX, startY, endX, startY, fraction,
            backBarStroke, backBarAlpha, build.team.color,
            proBarStroke, proBarAlpha, build.team.color
        );

        startY += backBarStroke;

        float scl = block.size / 8f / 2f / Scl.scl(1f);
        MDrawf.drawText(Strings.autoFixed((1 - fraction)  * build.plan().time / 60, 1) + "s", scl, startX + size / 2, startY);

        Draw.reset();
    }

}
