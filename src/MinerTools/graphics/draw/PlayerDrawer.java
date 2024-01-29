package MinerTools.graphics.draw;

import mindustry.gen.*;

public abstract class PlayerDrawer extends Drawer<Player>{

    @Override
    public boolean shouldDraw(Player player){
        return !player.dead();
    }

    @Override
    protected abstract void draw(Player player);

}
