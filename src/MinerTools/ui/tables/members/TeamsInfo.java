package MinerTools.ui.tables.members;

import MinerTools.game.*;
import MinerTools.ui.*;
import MinerTools.ui.tables.MembersTable.*;
import MinerTools.ui.tables.floats.*;
import MinerTools.utils.*;
import MinerTools.utils.ui.*;
import arc.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.struct.ObjectFloatMap.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
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
            t.defaults().growX();

            boolean isFirst = true;
            for(Teams.TeamData data : teamData){
                Team team = data.team;

                t.table(teamTable -> {
                    ElementUtils.addTitle(teamTable, team.localized(), team.color);

                    teamTable.table(Tex.pane2, container -> {
                        setupTeamInfoTable(container, data);
                    }).growX().row();
                }).padTop(isFirst ? 0f : 8f);

                t.row();

                isFirst = false;
            }
        }).scrollX(false).grow().with(p -> MUI.panes.add(p));
    }

    private void setupTeamInfoTable(Table table, TeamData data){
        table.table(t -> {
            t.left().top();
            t.defaults().growX();

            t.table(top -> {
                setupInfoTable(top, data);
            });

            t.row().defaults().padTop(8f);

            t.table(powerInfoTable -> {
                setupPowerInfoTable(powerInfoTable, data);
            }).growX();
        }).growX();

        table.row();

        table.defaults().padTop(8f);

        table.table(Tex.whiteui, units -> {
            setupUnitsTable(units, data);
        }).margin(4.0f).color(Pal.darkerGray).grow();
    }

    private void setupInfoTable(Table table, TeamData data){
        Team team = data.team;

        table.left();

        table.label(() -> data.cores.size + "").padLeft(3.0f).style(Styles.outlineLabel).expandX().right();
        table.image(Blocks.coreNucleus.uiIcon).size(24.0f);

        table.label(() -> {
            return GameUtils.colorMark(team) + data.players.size + "[white]/" + Groups.player.size();
        }).padLeft(3.0f).style(Styles.outlineLabel).expandX().right();
        table.image(Icon.playersSmall).size(24.0f).color(team == Vars.player.team() ? Color.green : Color.white);
    }

    private void setupPowerInfoTable(Table table, TeamData data){
        Team team = data.team;

        final PowerInfo powerInfo = PowerInfo.getPowerInfo(team);

        table.clearChildren();

        table.add(new Bar(() -> {
            float powerBalance = powerInfo.getPowerBalance();
            return Iconc.power + UI.formatAmount((long)powerBalance);
        }, () -> team.color, powerInfo::getSatisfaction)).grow();

        table.button(Icon.info, Styles.clearNonei, () -> {
        }).size(32).with(b -> b.clicked(() -> {
            PowerInfoTable t = PowerInfoTable.get(team, powerInfo);
            t.addUI();
            t.alignTo(b, Align.top, Align.bottom);
        }));
    }

    private void setupUnitsTable(Table table, TeamData data){
        table.left().top();

        int[] lastCap = new int[]{-1};

        Runnable rebuildUnits = () -> {
            table.clearChildren();

            int columns = Math.max(1, (int)(table.getWidth() / 44f));

            int i = 0;
            for(UnitType unit : Vars.content.units()){
                if(data.countType(unit) <= 0) continue;

                table.add(new NumImage(unit.uiIcon, data.countType(unit))).size(40f).padLeft(4f);

                if(++i % columns == 0){
                    table.row();
                }
            }
        };

        table.update(() -> {
            int cap = data.unitCount;
            if(lastCap[0] != cap){
                lastCap[0] = cap;
                rebuildUnits.run();
            }
        });
    }

    private static class PowerInfoTable extends TemporaryFloatTable{
        private final PowerInfo info;
        private final Table consumerTable, producerTable;

        private PowerInfoTable(String name, PowerInfo info){
            super(name);

            this.info = info;

            consumerTable = new Table(Styles.black3);
            producerTable = new Table(Styles.black3);

            consumerTable.top();
            producerTable.top();

            update(this::rebuildInfo);
        }

        public static PowerInfoTable get(Team team, PowerInfo info){
            String name = team.localized() + Core.bundle.get("miner-tools.power-info");
            return FloatManager.getOrCreate(name, () -> new PowerInfoTable(name, info));
        }

        @Override
        protected void rebuildBody(Table body){
            super.rebuildBody(body);

            body.defaults().grow();

            body.table(t -> {
                ElementUtils.addTitle(t, "Consumer", Color.red);
                t.add(consumerTable).grow();
            });

            body.table(t -> {
                ElementUtils.addTitle(t, "Producer", Color.green);
                t.add(producerTable).grow();
            }).padLeft(4f);

            rebuildInfo();
        }

        public void rebuildInfo(){
            rebuildConsumer();
            rebuildProducer();
        }

        private void rebuildConsumer(){
            consumerTable.clear();

            int columns = Math.max(1, (int)(consumerTable.getWidth() / 120f));

            int index = 0;
            for(Entry<Block> entry : info.getConsumeMap()){
                Block block = entry.key;
                float product = entry.value;

                consumerTable.table(container -> {
                    container.image(block.uiIcon).size(32f);
                    container.label(() -> Strings.autoFixed(product, 1)).color(Color.red).padLeft(4f).growX();
                }).width(120f).left();

                if(++index % columns == 0){
                    consumerTable.row();
                }
            }
        }

        private void rebuildProducer(){
            producerTable.clear();

            int columns = Math.max(1, (int)(producerTable.getWidth() / 120f));

            int index = 0;
            for(Entry<Block> entry : info.getProductMap()){
                Block block = entry.key;
                float product = entry.value;

                producerTable.table(container -> {
                    container.image(block.uiIcon).size(32f);
                    container.label(() -> Strings.autoFixed(product, 1)).color(Color.green).padLeft(4f).growX();
                }).width(120f).left();

                if(++index % columns == 0){
                    producerTable.row();
                }
            }
        }
    }
}

