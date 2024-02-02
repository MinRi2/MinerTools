package MinerTools.ui.tables.members;

import MinerTools.game.*;
import MinerTools.ui.*;
import MinerTools.ui.tables.MembersTable.*;
import MinerTools.utils.*;
import MinerTools.utils.ui.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.struct.ObjectFloatMap.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;

public class TeamsInfo extends MemberTable{
    private final Seq<Teams.TeamData> teamData = new Seq<>();
    private int lastTeamSize = -1;

    public TeamsInfo(){
        super(Icon.info);
    }

    private static void setupPowerInfoDetailsTable(Table table, PowerInfo info){
        table.background(Styles.black3);

        table.table(t -> {
            t.add("Consumers").growX();
            t.row();
            t.table().growX().update(consumer -> {
                consumer.clear();

                for(Entry<Block> entry : info.getConsumeMap()){
                    Block block = entry.key;
                    float product = entry.value;

                    consumer.table(container -> {
                        container.image(block.uiIcon).size(32.0f);
                        container.label(() -> Strings.autoFixed(product, 1)).right().color(Color.red).growX();
                    }).left().growX();

                    consumer.row();
                }
            });
        }).top().padLeft(4.0f);

        table.table(t -> {
            t.add("Producers").growX();
            t.row();
            t.table().growX().update(producer -> {
                producer.clear();

                for(Entry<Block> entry : info.getProductMap()){
                    Block block = entry.key;
                    float product = entry.value;

                    producer.table(container -> {
                        container.image(block.uiIcon).size(32.0f);
                        container.label(() -> Strings.autoFixed(product, 1)).right().color(Color.green).growX();
                    }).left().growX();

                    producer.row();
                }
            });
        }).top().padLeft(5.0f);
    }

    @Override
    public void memberRebuild(){
        setup();
    }

    private void setup(){
        defaults().growX();

        background(Styles.black3);

        update(() -> {
            int teamSize = Vars.state.teams.present.size;

            if(lastTeamSize != teamSize){
                teamData.set(Vars.state.teams.present);
                teamData.sort(data -> -data.unitCount);
                lastTeamSize = teamSize;

                rebuild();
            }
        });
    }

    private void rebuild(){
        clearChildren();

        pane(Styles.noBarPane, t -> {
            for(Teams.TeamData data : teamData){
                t.table(container -> {
                    addTeamInfoTable(container, data);
                }).padTop(8f).growX().row();
            }
        }).scrollX(false).grow();
    }

    private void addTeamInfoTable(Table table, Teams.TeamData data){
        Team team = data.team;

        // some info
        table.table(t -> {
            t.left().top();
            t.defaults().growX();

            t.table(info -> {
                info.left();
                info.defaults().growX();

                info.add(GameUtils.coloredName(team));

                info.table(cont -> {
                    cont.left();

                    cont.label(() -> data.cores.size + "").padLeft(3.0f).left().style(Styles.outlineLabel);
                    cont.image(Blocks.coreNucleus.uiIcon).size(24.0f);
                });

                info.table(cont -> {
                    cont.left();

                    cont.label(() -> {
                        return GameUtils.colorMark(team) + data.players.size + "[white]/" + Groups.player.size();
                    }).padLeft(3.0f).left().style(Styles.outlineLabel);
                    cont.image(Icon.playersSmall).size(24.0f).color(team == Vars.player.team() ? Color.green : Color.white);
                }).row();
            });

            t.table(powerInfoTable -> {
                final PowerInfo[] powerInfo = {PowerInfo.getPowerInfo(team)};

                Runnable powerInfoBuilder = () -> {
                    powerInfoTable.clearChildren();

                    powerInfoTable.add(new Bar(() -> {
                        float powerBalance = powerInfo[0].getPowerBalance();
                        return Iconc.power + UI.formatAmount((long)powerBalance);
                    }, () -> team.color, powerInfo[0]::getSatisfaction)).grow();

                    powerInfoTable.button(Icon.info, Styles.clearNonei, () -> {
                    }).size(32).with(b -> {
                        ElementUtils.addTooltip(b, tooltip -> {
                            setupPowerInfoDetailsTable(tooltip, powerInfo[0]);
                        }, true);
                    });
                };

                powerInfoBuilder.run();

                powerInfoTable.update(() -> {
                    PowerInfo currentInfo = PowerInfo.getPowerInfo(team);

                    if(powerInfo[0] != currentInfo){
                        powerInfo[0] = currentInfo;
                        powerInfoBuilder.run();
                    }
                });
            }).growX();
        }).growX();

        table.row();

        table.table(Tex.whiteui, units -> {
            units.left().top();

            int[] lastCap = new int[]{-1};

            Runnable rebuildUnits = () -> {
                units.clear();

                int i = 0;
                for(UnitType unit : Vars.content.units()){
                    if(data.countType(unit) <= 0) continue;

                    units.add(new NumImage(unit.uiIcon, data.countType(unit))).size(40.0f).padLeft(4f);

                    if(++i % 5 == 0){
                        units.row();
                    }
                }
            };

            units.update(() -> {
                int cap = data.unitCount;
                if(lastCap[0] != cap){
                    lastCap[0] = cap;
                    rebuildUnits.run();
                }
            });
        }).margin(4.0f).color(Pal.darkerGray).grow();
    }
}

