package MinerTools;

import MinerTools.ai.*;
import MinerTools.graphics.*;
import MinerTools.ui.*;
import arc.*;
import arc.input.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

import static MinerTools.MinerFuncs.*;
import static MinerTools.MinerVars.*;
import static MinerTools.input.ModBinding.updateConveyor;
import static arc.Core.input;
import static mindustry.Vars.*;

public class MinerTools extends Mod{

    public MinerTools(){
        enableConsole = true;

        Events.on(EventType.ContentInitEvent.class, e -> {
            MinerVars.initContent();
        });

        Events.on(EventType.ClientLoadEvent.class, e -> {
            Timer.schedule(Updater::checkUpdate, 3);

            MinerVars.init();

            Drawer.init();
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            Timer.schedule(PowerInfo::load, 1f);

            Timer.schedule(MinerFuncs::showBannedInfo, 2f);
        });

        Events.run(Trigger.update, this::update);

        Timer.schedule(PowerInfo::updateAll, 1f, 3f);
    }

    public void update(){
        if((desktop && input.keyDown(updateConveyor)) || (mobile && enableUpdateConveyor)){
            tryUpdateConveyor();
        }

        if(desktop && input.alt() && input.keyTap(KeyCode.mouseLeft)){
            tryPanToController();
        }

        mui.update();

        BaseAI.updateController();
    }

}