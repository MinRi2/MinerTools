package MinerTools.graphics.draw;

import MinerTools.content.*;
import arc.func.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;

public abstract class BuildDrawer<T extends Building> extends BaseDrawer<T>{
    /* Ids of block */
    private final IntSeq blocks;

    public BuildDrawer(){
        this((IntSeq)null);
    }

    public BuildDrawer(IntSeq blocks){
        this.blocks = blocks;
    }

    public BuildDrawer(Seq<Block> blocks){
        this(blocks.mapInt(block -> block.id));
    }

    public BuildDrawer(Boolf<Block> predicate){
        this(Contents.visibleBlocks.select(predicate));
    }

    @Override
    public void tryDraw(Building building){
        if(blocks != null && !blocks.contains(building.block.id)) return;

        super.tryDraw((T)building);
    }

    @Override
    protected abstract void draw(T building);

    @Override
    public boolean isValid(T building){
        return building.isValid() && !building.inFogTo(Vars.player.team());
    }

}
