package MinerTools.graphics.draw;

import MinerTools.content.*;
import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;

public abstract class BuildDrawer<T extends Building> extends BaseDrawer<T>{
    private final Seq<Block> blocks;

    public BuildDrawer(){
        this((Seq<Block>)null);
    }

    public BuildDrawer(Boolf<Block> predicate){
        this(Contents.visibleBlocks.select(predicate));
    }

    public BuildDrawer(Seq<Block> blocks){
        this.blocks = blocks;
    }

    @Override
    public void tryDraw(Building building){
        if(blocks != null && !blocks.contains(building.block)) return;

        super.tryDraw((T)building);
    }

    @Override
    public boolean isValid(T building){
        return building.isValid();
    }

}
