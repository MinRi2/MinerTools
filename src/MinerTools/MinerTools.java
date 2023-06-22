package MinerTools;

import MinerTools.ai.*;
import MinerTools.content.*;
import MinerTools.game.*;
import MinerTools.graphics.*;
import MinerTools.modules.*;
import MinerTools.utils.*;
import arc.*;
import arc.input.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

import static MinerTools.MinerVars.desktop;
import static MinerTools.input.ModBinding.updateConveyor;
import static MinerTools.utils.MinerFunc.*;
import static arc.Core.input;
import static mindustry.Vars.*;

public class MinerTools extends Mod{

    public MinerTools(){
        /* Zoom */
        renderer.minZoom = 0.4f;
        renderer.maxZoom = 12f;

        // mobile = true; // Only for debug

        Events.on(EventType.ContentInitEvent.class, e -> Contents.init());

        Events.on(EventType.ClientLoadEvent.class, e -> {
            Timer.schedule(Updater::checkUpdate, 8);

            MinerVars.init();

            Modules.init();

            Renderer.init();

            Modules.load();
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {

            Timer.schedule(MinerFunc::showBannedInfo, 2f);
        });

        Events.run(Trigger.update, this::update);
    }

    public void update(){
        PowerInfo.update();

        if((desktop && input.keyDown(updateConveyor)) || (mobile && enableUpdateConveyor)){
            tryUpdateConveyor();
        }

        if(desktop && input.alt() && input.keyTap(KeyCode.mouseLeft)){
            tryPanToController();
        }

        if(MinerVars.ui != null){
            MinerVars.ui.update();
        }

        BaseAI.updateController();
    }

}
