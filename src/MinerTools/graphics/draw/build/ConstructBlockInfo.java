package MinerTools.graphics.draw.build;

import MinerTools.*;
import MinerTools.graphics.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.ConstructBlock.*;
import mindustry.world.blocks.storage.CoreBlock.*;

public class ConstructBlockInfo extends BuildDrawer<ConstructBuild>{

    public ConstructBlockInfo(){
        super(getConsBlock());
    }

    @Override
    public boolean isEnabled(){
        return MinerVars.settings.getBool("constructBuildInfo");
    }

    @Override
    public boolean shouldDraw(ConstructBuild building){
        return super.shouldDraw(building) && building.core() != null;
    }

    @Override
    protected void draw(ConstructBuild c){
        CoreBuild core = c.core();

        // Above building
        Draw.z(Layer.blockBuilding + 0.1f);

        ConstructBlock block = (ConstructBlock)c.block;
        float buildCostMultiplier = Vars.state.rules.buildCostMultiplier;
        float scl = block.size / 8f / 2f / Scl.scl(1f);

        MDrawf.drawText(Strings.autoFixed(c.progress * 100, 2) + "%", scl, c.x, c.y + block.size * Vars.tilesize / 2f, Pal.accent, Align.center);

        float nextPad = 0f;
        for(int i = 0; i < c.current.requirements.length; i++){
            ItemStack stack = c.current.requirements[i];

            float dx = c.x - (block.size * Vars.tilesize) / 2f, dy = c.y - (block.size * Vars.tilesize) / 2f + nextPad;
            boolean hasItem = (1.0f - c.progress) * buildCostMultiplier * stack.amount <= core.items.get(stack.item);

            int needAmount = (int)(c.progress * buildCostMultiplier * stack.amount);
            int invertAmount = (int)(buildCostMultiplier * stack.amount);
            int coreAmount = core.items.get(stack.item);

            nextPad += MDrawf.drawText(
            stack.item.emoji() + needAmount + "/" + invertAmount + "/" + UI.formatAmount(coreAmount),
            scl, dx, dy, hasItem ? Pal.accent : Pal.remove, Align.left).height;
            nextPad++;
        }

        Draw.reset();
    }

    public static IntSeq getConsBlock(){
        ConstructBlock[] cons = Reflect.get(ConstructBlock.class, "consBlocks");
        return new Seq<Block>().addAll(cons).mapInt(block -> block.id);
    }

}
