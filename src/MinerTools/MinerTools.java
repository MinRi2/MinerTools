package MinerTools;

import MinerTools.core.*;
import MinerTools.ui.*;
import arc.*;
import arc.scene.ui.layout.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;

import static MinerTools.MinerFuncs.*;
import static MinerTools.MinerVars.*;
import static arc.Core.*;
import static mindustry.Vars.*;

public class MinerTools extends Mod{

    public MinerTools(){
        enableConsole = true;

        Events.on(EventType.ClientLoadEvent.class, e -> {
            MinerVars.init();
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            Core.app.post(() -> Core.app.post(() -> Core.app.post(() -> Core.app.post(() -> Core.app.post(PowerInfo::init)))));

            showBannedInfo();
        });

        Events.run(Trigger.update, () -> update());

        Drawer.setEvents();
    }

    public static void update(){
        if((desktop && input.alt()) || (mobile && enableUpdateConveyor)){
            tryUpdateConveyor();
        }
    }
}