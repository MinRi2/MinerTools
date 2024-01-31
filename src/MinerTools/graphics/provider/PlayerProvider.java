package MinerTools.graphics.provider;

import MinerTools.graphics.draw.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;

public class PlayerProvider extends DrawerProvider<PlayerDrawer>{
    private final Seq<PlayerDrawer> globalDrawers = new Seq<>();
    private final Seq<PlayerDrawer> localDrawers = new Seq<>();

    public final PlayerProvider addGlobalDrawers(PlayerDrawer... drawers){
        globalDrawers.addAll(drawers);
        addDrawers(drawers);
        return this;
    }

    public final PlayerProvider addLocalDrawers(PlayerDrawer... drawers){
        localDrawers.addAll(drawers);
        addDrawers(drawers);
        return this;
    }

    @Override
    public void provide(){
        if(globalDrawers.any()){
            globalProvide(globalDrawers.select(Drawer::isValid));
        }

        if(localDrawers.any()){
            playerProvide(localDrawers.select(Drawer::isValid));
        }
    }

    private void globalProvide(Seq<PlayerDrawer> validDrawers){
        Groups.player.each(player -> {
            for(PlayerDrawer drawer : validDrawers){
                drawer.tryDraw(player);
            }
        });
    }

    /* Local player */
    private void playerProvide(Seq<PlayerDrawer> validDrawers){
        for(PlayerDrawer drawer : validDrawers){
            drawer.tryDraw(Vars.player);
        }
    }

}
