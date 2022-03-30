package MinerTools.ui.tables.members;

import MinerTools.*;
import MinerTools.ui.*;
import arc.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.world.blocks.defense.turrets.Turret.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class PlayersList extends MemberTable{
    private int lastSize;
    private final Seq<Player> players = new Seq<>();

    private Table playersTable = new Table(black3);

    private Player target;

    public PlayersList(){
        icon = Icon.players;

        ScrollPane pane = pane(nonePane, playersTable).maxHeight(235).get();
        MUI.panes.add(pane);

        update(() -> {
            if(Groups.player.size() != lastSize){
                lastSize = Groups.player.size();
                Groups.player.copy(players.clear());
                rebuild();
            }
        });

        Events.on(EventType.WorldLoadEvent.class, e -> target = null);

        Events.run(Trigger.update, () -> {
            if(control.input instanceof DesktopInput input && !input.panning){
                target = null;
            }

            if(target != null){
                Position pos = target;
                if(target.unit() instanceof BlockUnitc block && block.tile() instanceof TurretBuild build){
                    pos = build.targetPos;
                }
                camera.position.lerpDelta(pos, 0.08f);
            }
        });
    }

    private void rebuild(){
        playersTable.clear();
        players.sort(Structs.comps(Structs.comparing(Player::team), Structs.comparingBool(p -> !p.admin)));

        playersTable.table(t -> {
            /* players */
            t.label(() -> "[#" + player.team().color + "]" + MinerFuncs.countPlayer(player.team()) + "[] / " + Groups.player.size()).row();

            for(Player player : players){
                t.table(info -> {
                    info.table(image -> {
                        image.add(new Image(player.icon()).setScaling(Scaling.bounded)).grow();
                    }).size(35);

                    info.labelWrap("[#" + player.color.toString().toUpperCase() + "]" + player.name).width(140).pad(10);
                    info.add().width(-1f).grow();

                    info.button(Icon.copy, clearPartiali, () -> {
                        app.setClipboardText(player.name);
                    }).size(35);

                    info.button(Icon.lockOpen, clearTogglePartiali, () -> {
                        if(target == player){
                            target = null;
                        }else{
                            target = player;
                            if(control.input instanceof DesktopInput input){
                                input.panning = true;
                            }
                        }
                    }).size(35).checked(b -> {
                        b.getStyle().imageUp = target == player ? Icon.lock : Icon.lockOpen;
                        return target == player;
                    });

                    info.button(Icon.units, clearPartiali, () -> {
                        if(control.input instanceof DesktopInput input){
                            input.panning = true;
                        }
                        camera.position.set(player);
                    }).size(35);

                    info.button(Icon.list, clearPartiali, () -> MUI.showTableAt(table -> {
                        table.background(Tex.buttonOver);

                        table.button(Icon.eyeSmall, clearPartiali, () -> {
                            ui.showConfirm("@confirm", bundle.format("confirmvoteob", player.name), () -> {
                                Call.sendChatMessage("/vote ob " + player.name);
                            });
                        }).grow().size(35f);

                        table.button(Icon.hammerSmall, clearPartiali, () -> {
                            ui.showConfirm("@confirm", bundle.format("confirmvotekick", player.name), () -> {
                                Call.sendChatMessage("/votekick " + player.name);
                            });
                        }).grow().size(35f);
                    })).size(35);
                });

                t.row();
                t.image().height(4).color(player.team().color).fillX();
                t.row();
            }
        });
    }
}