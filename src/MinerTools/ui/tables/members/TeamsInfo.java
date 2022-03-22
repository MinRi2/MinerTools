package MinerTools.ui.tables.members;

import MinerTools.*;
import MinerTools.core.*;
import MinerTools.ui.Dialogs.*;
import MinerTools.ui.*;
import MinerTools.ui.utils.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
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

import static MinerTools.MinerFuncs.*;
import static MinerTools.MinerVars.*;
import static MinerTools.input.ModBinding.*;
import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.copper;
import static mindustry.content.UnitTypes.*;
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

    private void rebuild(){
        clear();

        table(black3, table -> {
            addInfo(table, coreNucleus.uiIcon, () -> player.team().cores().size + "");
            addInfo(table, mono.uiIcon, () -> countMiner(player.team()) + "");
            addInfo(table, gamma.uiIcon, () -> "[#" + player.team().color + "]" + countPlayer(player.team()) + "[white]" + "/" + "[accent]" + Groups.player.size());
        }).fillX();

        addDivide();

        ScrollPane pane = pane(nonePane, p -> table = p).fillX().left().maxHeight(135).scrollX(false).get();
        MUI.panes.add(pane);

        table.background(black3);
        table.update(() -> {
            if(timer.get(0, 120)){
                teams.set(state.teams.getActive());
                teams.sort(data -> -data.unitCount);
                tableRebuild();
            }
        });

        addDivide();

        /* useful buttons */
        table(black3, buttons -> {
            buttons.defaults().height(35).growX();

            if(mobile){
                buttons.button(Icon.hammer, emptytogglei, () -> control.input.isBuilding = !control.input.isBuilding)
                .name("stopBuilding").checked(b -> control.input.isBuilding);
                buttons.button(Icon.distribution, emptytogglei, () -> enableUpdateConveyor = !enableUpdateConveyor)
                .name("updateConveyor").checked(b -> enableUpdateConveyor);
            }

            ImageButton rebuildButton = buttons.button(new TextureRegionDrawable(poly.uiIcon), clearTransi, 25, MinerFuncs::rebuildBlocks)
            .name("rebuildBlocks").height(35).growX().get();

            /* 结构尚未优化 慎用 */
            ImageButton dropButton = buttons.button(new TextureRegionDrawable(copper.uiIcon), clearTransi, 25, MinerFuncs::dropItems)
            .name("dropItems").get();

            dropButton.changed(() -> {
                if(lastDropItem != null){
                    dropButton.getStyle().imageUp = new TextureRegionDrawable(lastDropItem.uiIcon);
                }
            });

            dropButton.addListener(new ElementGestureListener(){
                @Override
                public boolean longPress(Element actor, float x, float y){
                    dropSetting.show();
                    return true;
                }
            });

            buttons.button(Icon.trashSmall, clearTransi, () ->
            ui.showConfirm(bundle.get("miner-tools.buttons.tooltips.quickVoteGameOver"), () -> {
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

            if(desktop){
                buttons.update(() -> {
                    if(input.keyDown(buildBlocks)){
                        MinerFuncs.rebuildBlocks();
                    }
                    if(timer.get(1, dropHeat) && input.keyDown(dropItem)){
                        dropButton.fireClick();
                    }
                });
            }
        }).minWidth(45f * 4f).fillX();

        addDivide();
    }

    private void tableRebuild(){
        table.clear();

        for(TeamData data : teams){
            if(data.units.size > 0){
                Team team = data.team;

                table.table(teamTable -> {
                    Label label = teamTable.label(() -> (team == player.team() ? "[green]" + Iconc.players : "") + "[#" + team.color + "]" + team.localized() + "(" + countPlayer(team) + ")")
                    .padRight(3).minWidth(16).left().get();
                    label.setFontScale(fontScale + 0.15f);
                    addTeamRuleInfoTooltip(label, team);

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

                        powerBarTable.add(powerBar).width(100).fillY();
                        addPowerBarTooltip(powerBarTable, team);
                    }).pad(-1).right();
                }).pad(4).growX().left();

                table.row();
            }
        }
    }

    private void addDivide(){
        row();
        image().height(3).fillX().update(i -> i.setColor(player.team().color));
        row();
    }

    private static void addInfo(Table table, TextureRegion image, Prov<CharSequence> label){
        table.image(image).size(iconSmall).growX();
        table.label(label).padLeft(3).left().get().setFontScale(0.75f);
    }

    private static void addTeamRuleInfoTooltip(Element e, Team team){
        ElementUtils.addTooltip(e, t -> {
            t.background(black6);

            t.table(base -> {
                base.add();

                base.add("Damage").padLeft(5f);
                base.add("Health").padLeft(5f);
                base.add("BuildSpeed").padLeft(5f);

                base.row();

                base.image(duo.uiIcon).size(iconMed);
                base.add("" + team.rules().blockDamageMultiplier).center();
                base.add("" + team.rules().blockHealthMultiplier).center();
                base.add("" + team.rules().buildSpeedMultiplier).center();

                base.row();

                base.image(flare.uiIcon).size(iconMed);
                base.add("" + team.rules().unitDamageMultiplier).center();
                base.add().center();
                base.add("" + team.rules().unitBuildSpeedMultiplier).center();
            });
        }, mobile);
    }

    private static void addPowerBarTooltip(Element powerBar, Team team){
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
                            consumer.table(tt -> {
                                tt.image(block.uiIcon).size(iconLarge);
                                tt.label(() -> "x" + buildings.size).left();
                            }).left();

                            consumer.add().width(-1f).growX();

                            consumer.table(tt -> {
                                tt.image(Icon.power).padLeft(5f).size(iconSmall).color(Color.red).left();
                                tt.label(() -> String.format("%.1f", info.getConsPower(block))).labelAlign(Align.right).color(Color.red);
                            }).right();
                        }).left().growX();

                        consumers.row();
                    });
                }).fillX();
            }).top().padLeft(5f).minWidth(220f);

            table.table(t -> {
                t.add("Producers").labelAlign(Align.center).growX();

                t.row();

                t.table(producers -> {
                    info.producers.each((block, buildings) -> {
                        if(buildings.isEmpty()) return;

                        producers.table(producer -> {
                            producer.table(tt -> {
                                tt.image(block.uiIcon).size(iconLarge);
                                tt.label(() -> "x" + buildings.size).left();
                            }).left();

                            producer.add().width(-1f).growX();

                            producer.table(tt -> {
                                tt.image(Icon.power).padLeft(5f).size(iconSmall).color(Color.green).left();

                                tt.label(() -> String.format("%.1f", info.getProdPower(block))).labelAlign(Align.right).color(Color.green);
                            }).right();
                        }).left().growX();

                        producers.row();
                    });
                }).fillX();
            }).top().padLeft(5f).minWidth(220f);
        }, mobile);
    }
}
