package MinerTools.ui;

import MinerTools.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.type.*;

import static MinerTools.MineUtils.*;
import static arc.Core.*;
import static arc.graphics.Color.*;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.UnitTypes.*;
import static mindustry.ui.Styles.*;

public class MinerToolsTable extends Table{
    private boolean shown;

    private Table teamsTable;
    private Seq<TeamData> teams;
    private Interval timer = new Interval();

    public MinerToolsTable(){
        background(black3);

        teams = new Seq<>();
        rebuild();
    }

    public void rebuild(){
        clear();

        table(table -> {
            addInfo(table, coreNucleus.uiIcon, () -> player.team().cores().size + "");
            addInfo(table, mono.uiIcon, () -> countMiner(player.team()) + "");
            addInfo(table, gamma.uiIcon, () -> "[#" + player.team().color + "]" + countPlayer(player.team()) + "[white]" + "/" + "[accent]" + Groups.player.size());
        }).fillX();

        addDivive();

        pane(nonePane, p -> teamsTable = p).fillX().left().maxHeight(135).scrollX(false);

        teamsTableRebuild();
        teamsTable.update(() -> {
            if(timer.get(120)){
                teams = state.teams.getActive();
                teamsTableRebuild();
            }
        });

        addDivive();

        /* useful buttons */
        table(buttons -> {
            buttons.button(Icon.playersSmall, clearTransi, () -> {
                Call.sendChatMessage("/list");
            }).height(35).growX();

            buttons.button(Icon.play, emptytogglei, () -> {
                boolean view = settings.getBool("viewmode");
                if(view) camera.position.set(player);
                settings.put("viewmode", !view);
            }).checked(b -> {
                boolean view = settings.getBool("viewmode");
                b.getStyle().imageUp = !view ? Icon.play : Icon.pause;
                return view;
            }).height(35).growX();

            buttons.button(new TextureRegionDrawable(poly.uiIcon), clearTransi, 25, MineUtils::rebuildBlocks).height(35).growX();

            ImageButton dropButton = buttons.button(new TextureRegionDrawable(copper.uiIcon), clearTransi, 25, () -> {
                dropItems();
            }).height(35).growX().get();

            buttons.button(Icon.trashSmall, clearTransi, () -> {
                ui.showConfirm(
                "?",
                () -> {
                    Call.sendChatMessage("/vote gameover");
                    Call.sendChatMessage("1");
                });
            }).height(35).growX();

            buttons.getCells().each(cell -> ((ImageButton)cell.get()).getStyle().up = none);
        }).minWidth(45f * 4f).fillX();
    }

    private void teamsTableRebuild(){
        teamsTable.clear();

        for(TeamData data : teams){
            if(data.units.size > 0){
                Team team = data.team;

                teamsTable.table(teamTable -> {
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

                    teamTable.add().width(-1).growX();
                }).pad(4).left().fillX();

                teamsTable.row();
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
