package MinerTools.ui;

import MinerTools.*;
import MinerTools.ui.Dialogs.*;
import MinerTools.ui.utils.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;

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
    private final DropSettingDialog dropSetting = new DropSettingDialog();

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

            /* A button for wayzer plugin
            * see https://github.com/way-zer/ScriptAgent4MindustryExt
            * */
            buttons.button(playersSmall, clearTransi, () -> Call.sendChatMessage("/list"));

            if(mobile){
                buttons.button(Icon.hammer, emptytogglei, () -> control.input.isBuilding = !control.input.isBuilding)
                .name("stopBuilding").checked(b -> control.input.isBuilding);
            }

            buttons.button(new TextureRegionDrawable(poly.uiIcon), clearTransi, 25, MinerUtils::rebuildBlocks)
            .name("buildBlocks").height(35).growX();

            /* 结构尚未优化 慎用 */
            ImageButton dropButton = buttons.button(new TextureRegionDrawable(copper.uiIcon), clearTransi, 25, () -> dropItems())
            .name("dropItems").get();

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

            buttons.button(Icon.trashSmall, clearTransi, () -> ui.showConfirm(
            bundle.get("miner-tools.buttons.tooltips.quickVoteGameOver"),
            () -> {
                Call.sendChatMessage("/vote gameover");
                Call.sendChatMessage("1");
            })).name("quickVoteGameOver");

            buttons.getChildren().each(child -> {
                ImageButton imageButton = (ImageButton)child;
                imageButton.getStyle().up = none;

                /* add some tooltips */
                if(child.name != null){
                    ElementUtils.addTooltip(child, bundle.get("miner-tools.buttons.tooltips." + child.name), mobile);
                }
            });
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

                    teamTable.table(units -> units.update(() -> {
                        units.clear();

                        int i = 0;
                        for(UnitType unit : content.units()){
                            if(data.countType(unit) > 0){
                                if(i++ % 5 == 0) units.row();
                                units.image(unit.uiIcon).size(imgSize);
                                units.label(() -> data.countType(unit) + "").left().padRight(3).minWidth(16).get().setFontScale(fontScale);
                            }
                        }
                    })).padLeft(3).fill();

                    teamTable.add().growX();

                    teamTable.table(powerBarTable -> {
                        powerBarTable.image(ui.getIcon(Category.power.name())).color(team.color);

                        Bar powerBar = new Bar(
                        () -> (PowerInfo.getPowerInfo(team).getPowerBalance() >= 0 ? "+" : "") + UI.formatAmount(PowerInfo.getPowerInfo(team).getPowerBalance()),
                        () -> team.color,
                        () -> PowerInfo.getPowerInfo(team).getSatisfaction());

                        addPowerBarTooltip(powerBar, team);
                        powerBarTable.add(powerBar).width(100).fillY();
                    }).pad(-1).right();
                }).pad(4).growX().left();

                table.row();
            }
        }
    }

    private void addDivive(){
        row();
        image().height(3).fillX().update(i -> i.setColor(player.team().color));
        row();
    }

    private static void addInfo(Table table, TextureRegion image, Prov<CharSequence> label){
        table.image(image).size(iconSmall).growX();
        table.label(label).padLeft(3).left().get().setFontScale(0.75f);
    }
    private static void addPowerBarTooltip(Bar powerBar, Team team){
        ElementUtils.addTooltip(powerBar, table -> {
            PowerInfo info = PowerInfo.getPowerInfo(team);

            if(info == null) return;

            table.background(black6);

            table.table(t -> {
                t.add("Consumers").labelAlign(Align.center).growX();

                t.row();

                t.table(consumers -> {
                    info.consumers.each((block, buildings) -> {
                        if(buildings.isEmpty()) return;

                        consumers.table(consumer -> {
                            consumers.table(i -> {
                                i.image(block.uiIcon).size(iconLarge);
                                i.label(() -> "X" + buildings.size);
                            }).left();

                            consumers.add().width(-1f).growX();

                            consumers.image(Icon.power).padLeft(5f).size(iconSmall).color(Color.red);
                            consumers.label(() -> {
                                float sum = 0f;
                                for(Building building : buildings){
                                    sum += Mathf.num(building.shouldConsume()) * building.power.status * building.block.consumes.getPower().usage * 60 * building.timeScale();
                                }
                                return "" + sum;
                            }).labelAlign(Align.left).color(Color.red);
                        }).left().growX();


                        consumers.row();
                    });
                }).fillX();
            }).left();

            table.table(t -> {
                t.add("Producers").labelAlign(Align.center).growX();

                t.row();

                t.table(producers -> {
                    info.producers.each((block, buildings) -> {
                        if(buildings.isEmpty()) return;

                        producers.table(producer -> {
                            producers.table(tt -> {
                                tt.image(block.uiIcon).size(iconLarge);
                                tt.label(() -> "X" + buildings.size);
                            }).left();

                            producers.add().width(-1f).growX();

                            producers.image(Icon.power).padLeft(5f).size(iconSmall).color(Color.green);
                            producers.label(() -> {
                                float sum = 0f;
                                for(Building building : buildings){
                                    sum += building.getPowerProduction() * building.timeScale() * 60f;
                                }
                                return "" + sum;
                            }).labelAlign(Align.left).color(Color.green);
                        }).left().growX();

                        producers.row();
                    });
                }).fillX();
            }).padLeft(5f).top();
        }, mobile);
    }
}
