package MinerTools.graphics.renderer;

import MinerTools.graphics.draw.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;

public class PlayerRender extends BaseRender<PlayerDrawer>{

    /* All player*/
    @Override
    public void globalRender(Seq<PlayerDrawer> validDrawers){
        Groups.player.each(player -> {
            for(PlayerDrawer drawer : validDrawers){
                drawer.tryDraw(player);
            }
        });
    }

    /* Local player */
    @Override
    public void cameraRender(Seq<PlayerDrawer> validDrawers){
        for(PlayerDrawer drawer : validDrawers){
            drawer.tryDraw(Vars.player);
        }
    }

}
