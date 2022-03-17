package MinerTools;

import MinerTools.core.*;
import MinerTools.input.*;
import MinerTools.ui.*;
import arc.*;
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

        Events.on(EventType.ClientLoadEvent.class, e -> {
            MinerVars.init();
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
//            Core.app.post(() -> Core.app.post(() -> Core.app.post(() -> Core.app.post(() -> Core.app.post(PowerInfo::init)))));

            showBannedInfo();
        });

        Events.run(Trigger.update, () -> update());

        Drawer.setEvents();
    }

    public static void update(){
        if((desktop && input.keyDown(updateConveyor)) || (mobile && enableUpdateConveyor)){
            tryUpdateConveyor();
        }
    }
}