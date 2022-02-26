package MinerTools.ui;

import MinerTools.*;
import MinerTools.ui.Dialogs.*;
import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.type.*;

import static MinerTools.MinerUtils.*;
import static MinerTools.ui.MinerToolsTable.panes;
import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.coreNucleus;
import static mindustry.content.Items.copper;
import static mindustry.content.UnitTypes.*;
import static mindustry.gen.Icon.playersSmall;
import static mindustry.ui.Styles.*;

public class TeamsInfo extends Table{
    public static int dropHeat = 35;
    private DropSettingDialog dropSetting = new DropSettingDialog();

    private Table table;

    private Seq<TeamData> teams = new Seq<>();
    private final Interval timer = new Interval(2);

    public TeamsInfo(){
        rebuild();
    }

    public void rebuild(){
        clear();

        table(black3, table -> {
            addInfo(table, coreNucleus.uiIcon, () -> player.team().cores().size + "");
            addInfo(table, mono.uiIcon, () -> countMiner(player.team()) + "");
            addInfo(table, gamma.uiIcon, () -> "[#" + player.team().color + "]" + countPlayer(player.team()) + "[white]" + "/" + "[accent]" + Groups.player.size());
        }).fillX();

        addDivive();

        ScrollPane pane = pane(nonePane, p -> table = p).fillX().left().maxHeight(135).scrollX(false).get();
        panes.add(pane);

        table.background(black3);
        tableRebuild();
        table.update(() -> {
            if(timer.get(0, 120)){
                teams = state.teams.getActive();
                tableRebuild();
            }
        });

        addDivive();

        /* useful buttons */
        table(black3, buttons -> {
            buttons.defaults().height(35).growX();

            buttons.button(playersSmall, clearTransi, () -> {
                Call.sendChatMessage("/list");
            });

            if(mobile){
                buttons.button(Icon.play, emptytogglei, () -> {
                    boolean view = settings.getBool("viewmode");
                    if(view) camera.position.set(player);
                    settings.put("viewmode", !view);
                }).checked(b -> {
                    boolean view = settings.getBool("viewmode");
                    b.getStyle().imageUp = !view ? Icon.play : Icon.pause;
                    return view;
                });

                buttons.button(Icon.hammer, emptytogglei, () -> control.input.isBuilding = !control.input.isBuilding)
                .checked(b -> control.input.isBuilding);
            }

            buttons.button(new TextureRegionDrawable(poly.uiIcon), clearTransi, 25, MinerUtils::rebuildBlocks).height(35).growX();

            ImageButton dropButton = buttons.button(new TextureRegionDrawable(copper.uiIcon), clearTransi, 25, () -> {
                dropItems();
            }).get();

            dropButton.changed(() -> {
                if(lastDropItem != null) dropButton.getStyle().imageUp = new TextureRegionDrawable(lastDropItem.uiIcon);
            });

            dropButton.update(() -> {
                if(timer.get(1, dropHeat) && input.keyDown(KeyCode.h)){
                    dropButton.fireClick();
                }
            });

            dropButton.addListener(new ElementGestureListener(){
                @Override
                public boolean longPress(Element actor, float x, float y){
                    dropSetting.show();
                    return true;
                }
            });

            buttons.button(Icon.trashSmall, clearTransi, () -> {
                ui.showConfirm(
                "?",
                () -> {
                    Call.sendChatMessage("/vote gameover");
                    Call.sendChatMessage("1");
                });
            });

            buttons.getCells().each(cell -> ((ImageButton)cell.get()).getStyle().up = none);
        }).minWidth(45f * 4f).fillX();

        addDivive();
    }

    private void tableRebuild(){
        table.clear();

        for(TeamData data : teams){
            if(data.units.size > 0){
                Team team = data.team;

                table.table(teamTable -> {
                    teamTable.label(() -> "[#" + team.color + "]" + team.localized() + "(" + countPlayer(team) + ")")
                    .padRight(3).minWidth(16).left().get().setFontScale(fontScale + 0.15f);

                    teamTable.table(units -> {
                        units.update(() -> {
                            units.clear();

                            int i = 0;
                            for(UnitType unit : content.units()){
                                if(data.countType(unit) > 0){
                                    if(i++ % 5 == 0) units.row();
                                    units.image(unit.uiIcon).size(imgSize);
                                    units.label(() -> data.countType(unit) + "").left().padRight(3).minWidth(16).get().setFontScale(fontScale);
                                }
                            }
                        });
                    }).padLeft(3).fill();
                }).pad(4).fillX().left();

                table.row();
            }
        }
    }

    private void addDivive(){
        row();
        image().height(3).fillX().update(i -> i.setColor(player.team().color));
        row();
    }

    private void addInfo(Table table, TextureRegion image, Prov<CharSequence> label){
        table.image(image).size(iconSmall).growX();
        table.label(label).padLeft(3).left().get().setFontScale(0.75f);
    }
}
