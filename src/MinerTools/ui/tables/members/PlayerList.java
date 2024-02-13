package MinerTools.ui.tables.members;

import MinRi2.ModCore.ui.*;
import MinerTools.ui.*;
import MinerTools.ui.tables.MembersTable.*;
import MinerTools.utils.*;
import arc.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.world.blocks.defense.turrets.Turret.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class PlayerList extends MemberTable{
    public static float buttonSize = 45f;
    private final Seq<Player> players = new Seq<>();
    private final Table playersTable = new Table(black3);
    private int lastSize;
    private Player target;

    public PlayerList(){
        super(Icon.players);

        ScrollPane pane = pane(noBarPane, playersTable).maxHeight(buttonSize * (mobile ? 5 : 8)).get();
        MUI.panes.add(pane);

        update(() -> {
            if(Groups.player.size() != lastSize){
                lastSize = Groups.player.size();
                Groups.player.copy(players.clear());
                rebuild();
            }
        });

        Events.on(WorldLoadEvent.class, e -> target = null);

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
            t.label(() -> GameUtils.colorMark(player.team()) + GameUtils.playerCount(player.team()) + "[white] / " + Groups.player.size()).row();

            for(Player player : players){
                t.table(info -> {
                    info.image(player::icon).size(buttonSize)
                    .get().clicked(() -> panToPlayer(player));

                    info.labelWrap(player.coloredName()).width(200).pad(7)
                    .get().clicked(() -> UIUtils.setClipboardText(player.coloredName()));

                    info.add().width(-1f).grow();

                    info.button(Icon.lockOpen, clearNoneTogglei, () -> setTarget(player))
                    .size(buttonSize).checked(b -> {
                        b.getStyle().imageUp = target == player ? Icon.lock : Icon.lockOpen;
                        return target == player;
                    });

                    info.button(Icon.list, clearNonei, () -> UIUtils.showTableAtMouse(table -> {
                        table.background(Tex.buttonOver);

                        table.button(Icon.eyeSmall, clearNonei, () -> {
                            ui.showConfirm("@confirm", bundle.format("confirmvoteob", player.name), () -> {
                                Call.sendChatMessage("/vote ob " + player.name);
                            });
                        }).grow().size(buttonSize);

                        table.button(Icon.hammerSmall, clearNonei, () -> {
                            ui.showConfirm("@confirm", bundle.format("confirmvotekick", player.name), () -> {
                                Call.sendChatMessage("/votekick " + player.name);
                            });
                        }).grow().size(buttonSize);
                    })).size(buttonSize);
                });

                t.row();
                t.image().height(4).color(player.team().color).fillX();
                t.row();
            }
        });
    }

    private void panToPlayer(Player player){
        CameraUtils.pan(player);
    }

    private void setTarget(Player player){
        if(target == player){
            target = null;
        }else{
            target = player;
            if(control.input instanceof DesktopInput input){
                input.panning = true;
            }
        }
    }
}