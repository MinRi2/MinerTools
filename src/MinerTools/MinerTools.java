package MinerTools;

import MinerTools.ui.*;
import arc.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import io.mnemotechnician.autoupdater.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.mod.*;
import mindustry.ui.*;

import static mindustry.Vars.*;

public class MinerTools extends Mod{

    public MinerTools(){
        enableConsole = true;

        Events.on(EventType.WorldLoadEvent.class, e -> {
            Core.app.post(() -> Core.app.post(PowerInfo::load));

            Table t = new Table(Styles.black3);
            t.touchable = Touchable.disabled;
            if(!state.rules.bannedUnits.isEmpty()){
                t.add("[accent]BannedUnits:[] ").style(Styles.outlineLabel).labelAlign(Align.left);
                for(UnlockableContent c : state.rules.bannedUnits){
                    t.image(c.uiIcon).size(iconSmall).left().padLeft(3f);
                }
                t.row();
            }
            if(!state.rules.bannedBlocks.isEmpty()){
                t.add("[accent]BannedBlocks:[] ").style(Styles.outlineLabel).labelAlign(Align.left);
                for(UnlockableContent c : state.rules.bannedBlocks){
                    t.image(c.uiIcon).size(iconSmall).left().padLeft(3f);
                }
                t.row();
            }

            t.margin(8f).update(() -> t.setPosition(Core.graphics.getWidth()/2f, Core.graphics.getHeight()/2f, Align.center));
            t.actions(Actions.fadeOut(6.5f, Interp.pow4In), Actions.remove());
            t.pack();
            t.act(0.1f);
            Core.scene.add(t);
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
