package MinerTools.graphics.provider;

import MinerTools.graphics.draw.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;

public class PlayerProvider extends DrawerProvider<PlayerDrawer>{
    private final Seq<PlayerDrawer> globalDrawers = new Seq<>();
    private final Seq<PlayerDrawer> localDrawers = new Seq<>();

    private Seq<PlayerDrawer> enableGlobalDrawers;
    private Seq<PlayerDrawer> enableLocalDrawers;

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
    public void updateEnable(){
        enableGlobalDrawers = globalDrawers.select(Drawer::isEnabled);
        enableLocalDrawers = localDrawers.select(Drawer::isEnabled);

        enableDrawers.clear();
        enableDrawers.addAll(enableGlobalDrawers).addAll(enableLocalDrawers);
    }

    @Override
    public void provide(){
        if(globalDrawers.any()){
            globalProvide(enableGlobalDrawers.select(Drawer::isValid));
        }

        if(localDrawers.any()){
            playerProvide(enableLocalDrawers.select(Drawer::isValid));
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
