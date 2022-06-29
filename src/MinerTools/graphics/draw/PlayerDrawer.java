package MinerTools.graphics.draw;

import mindustry.gen.*;

public abstract class PlayerDrawer extends BaseDrawer<Player>{

    @Override
    public boolean isValid(Player player){
        return !player.dead();
    }

    @Override
    protected abstract void draw(Player player);

}
