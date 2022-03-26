package MinerTools;

import MinerTools.core.*;
import MinerTools.ui.*;
import arc.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

import static MinerTools.MinerFuncs.*;
import static MinerTools.MinerVars.*;
import static MinerTools.Updater.checkUpdate;
import static MinerTools.input.ModBinding.updateConveyor;
import static arc.Core.input;
import static mindustry.Vars.*;

public class MinerTools extends Mod{

    public MinerTools(){
        enableConsole = true;

        Events.on(EventType.ClientLoadEvent.class, e -> {
            checkUpdate();

            MinerVars.init();

            Drawer.setEvents();
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            Timer.schedule(PowerInfo::load, 2f);

            showBannedInfo();
        });

        Events.run(Trigger.update, this::update);

        Timer.schedule(PowerInfo::updateAll, 1f, 3f);
    }

    public void update(){
        if((desktop && input.keyDown(updateConveyor)) || (mobile && enableUpdateConveyor)){
            tryUpdateConveyor();
        }
        mui.update();
    }
}