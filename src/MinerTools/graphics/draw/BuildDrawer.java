package MinerTools.graphics.draw;

import MinerTools.*;
import arc.func.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;

public abstract class BuildDrawer<T extends Building> extends BaseDrawer<T>{
    private final Seq<Block> blocks;

    public BuildDrawer(){
        this(MinerVars.visibleBlocks.copy());
    }

    public BuildDrawer(Seq<Block> blocks){
        this.blocks = blocks;
    }

    public BuildDrawer(Boolf<Block> predicate){
        this(MinerVars.visibleBlocks.select(predicate));
    }

    @Override
    public void tryDraw(Building building){
        if(!blocks.contains(building.block)) return;

        super.tryDraw((T)building);
    }

    @Override
    public boolean isValid(T building){
        return building.isValid() && !building.inFogTo(Vars.player.team());
    }

}
