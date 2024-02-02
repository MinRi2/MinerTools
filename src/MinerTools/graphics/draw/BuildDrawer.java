package MinerTools.graphics.draw;

import arc.func.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;

public abstract class BuildDrawer<T extends Building> extends Drawer<T>{
    private final Boolf<Block> shouldDrawBlock;

    public BuildDrawer(){
        this(block -> true);
    }

    public BuildDrawer(Boolf<Block> shouldDrawBlock){
        this.shouldDrawBlock = shouldDrawBlock;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void tryDraw(Building type){
        if(!shouldDrawBlock.get(type.block)) return;

        super.tryDraw((T)type);
    }

    @Override
    protected abstract void draw(T building);

    @Override
    public boolean shouldDraw(T building){
        return building.isValid() && !building.inFogTo(Vars.player.team());
    }

}
