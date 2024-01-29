package MinerTools.graphics.provider;

import MinerTools.graphics.draw.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;

public class PlayerProvider extends CameraProvider<PlayerDrawer>{

    /* All player*/
    @Override
    public void globalProvide(Seq<PlayerDrawer> validDrawers){
        Groups.player.each(player -> {
            for(PlayerDrawer drawer : validDrawers){
                drawer.tryDraw(player);
            }
        });
    }

    /* Local player */
    @Override
    public void cameraProvide(Seq<PlayerDrawer> validDrawers){
        for(PlayerDrawer drawer : validDrawers){
            drawer.tryDraw(Vars.player);
        }
    }

}
