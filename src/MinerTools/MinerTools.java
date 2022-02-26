package MinerTools;

import MinerTools.ui.*;
import arc.*;
import arc.scene.ui.layout.*;
import io.mnemotechnician.autoupdater.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

import static mindustry.Vars.*;

public class MinerTools extends Mod{

    public MinerTools(){
        enableConsole = true;

        Events.on(EventType.WorldLoadEvent.class, e -> {
            Core.app.post(() -> Core.app.post(() -> PowerInfo.load()));
        });

        Events.on(EventType.ClientLoadEvent.class, e -> {
            Updater.checkUpdates(this);

            ui.hudGroup.fill(t -> {
                t.top().right().name = "miner-tools";
                t.visible(() -> ui.hudfrag.shown && !ui.minimapfrag.shown());

                t.add(new MinerToolsTable());

                Table minimap = ui.hudGroup.find("minimap/position");
                t.update(() -> t.translation.x = -minimap.getPrefWidth());
            });
        });
    }

}
