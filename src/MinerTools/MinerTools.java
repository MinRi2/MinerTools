package MinerTools;

import MinerTools.ai.*;
import MinerTools.content.*;
import MinerTools.content.override.*;
import MinerTools.game.*;
import MinerTools.graphics.*;
import MinerTools.modules.*;
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
import static mindustry.Vars.mobile;

public class MinerTools extends Mod{

    public MinerTools(){
        // mobile = true; // Only for debug
        Events.on(EventType.ContentInitEvent.class, e -> MContents.init());

        Events.on(EventType.ClientLoadEvent.class, e -> {
            Timer.schedule(Updater::checkUpdate, 3);

            MStats.init();
            MinerVars.init();

            Modules.init();

            MRenderer.init();

            Modules.load();
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
