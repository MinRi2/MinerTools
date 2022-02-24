package MinerTools.ui;

import MinerTools.*;
import arc.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class PlayersList extends Table{
    private static final Seq<Player> tmpSeq = new Seq<>();
    private static final Interval timer = new Interval();

    private final Seq<Player> lastPlayers = new Seq<>();

    private Table players = new Table(black3);

    public PlayersList(){
        ScrollPane pane = pane(players).maxHeight(235).get();

        pane.update(() -> {
            if(pane.hasScroll()){
                Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                if(result == null || !result.isDescendantOf(pane)){
                    Core.scene.setScrollFocus(null);
                }
            }
        });

        update(() -> {
            if(timer.get(60) && !lastPlayers.equals(Groups.player.copy(tmpSeq))){
                Groups.player.copy(lastPlayers.clear());
                rebuild();
            }
        });
    }

    private void rebuild(){
        players.clear();
        lastPlayers.sort(Structs.comps(Structs.comparing(p -> p.team()), Structs.comparingBool(p -> !p.admin)));

        players.table(t -> {
            /* players */
            t.label(() -> "[#" + player.team().color + "]" + MinerUtils.countPlayer(player.team()) + "[] / " + Groups.player.size()).row();

            for(Player player : lastPlayers){
                t.table(info -> {
                    info.table(image -> {
                        image.add(new Image(player.icon()).setScaling(Scaling.bounded)).grow();
                    }).size(35);

                    info.labelWrap("[#" + player.color.toString().toUpperCase() + "]" + player.name).width(140).pad(10);
                    info.add().width(-1f).grow();

                    info.button(Icon.copy, clearPartiali, () -> {
                        app.setClipboardText(player.name);
                    }).size(35);

                    info.button(Icon.lock, clearToggleTransi, () -> {
                    }).size(35).update(b -> {
                    });

                    info.button(Icon.units, clearPartiali, () -> {
                    }).size(35);

                    info.button(Icon.hammer, clearPartiali, () -> {
                        ui.showConfirm("@confirm", bundle.format("confirmvotekick", player.name), () -> {
                            Call.sendChatMessage("/votekick " + player.name);
                        });
                    }).size(35);
                });

                t.row();
                t.image().height(4).color(player.team().color).fillX();
                t.row();
            }
        });
    }
}