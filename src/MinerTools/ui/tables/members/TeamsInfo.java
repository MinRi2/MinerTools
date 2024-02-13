package MinerTools.ui.tables.members;

import MinRi2.ModCore.ui.*;
import MinerTools.game.*;
import MinerTools.ui.*;
import MinerTools.ui.tables.MembersTable.*;
import MinerTools.ui.tables.floats.*;
import MinerTools.utils.*;
import arc.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
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
            int teamSize = Vars.state.teams.active.size;

            if(lastTeamSize != teamSize){
                teamData.set(Vars.state.teams.active);
                teamData.sort(data -> -data.unitCount);
                lastTeamSize = teamSize;

                rebuild();
            }
        });
    }

    private void rebuild(){
        clearChildren();

        pane(Styles.noBarPane, t -> {
            t.top();
            t.defaults().growX();

            for(Teams.TeamData data : teamData){
                Team team = data.team;

                t.table(teamTable -> {
                    ElementUtils.addTitle(teamTable, team.localized(), team.color);

                    teamTable.table(Tex.pane2, container -> {
                        setupTeamInfoTable(container, data);
                    }).growX().row();
                }).padTop(8f);

                t.row();
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
        }).margin(4f).color(Pal.darkerGray).grow();
    }

    private void setupInfoTable(Table table, TeamData data){
        Team team = data.team;

        table.left();

        table.label(() -> data.cores.size + "").padLeft(3f).style(Styles.outlineLabel).expandX().right();
        table.image(Blocks.coreNucleus.uiIcon).size(24f);

        table.label(() -> {
            return GameUtils.colorMark(team) + data.players.size + "[white]/" + Groups.player.size();
        }).padLeft(3f).style(Styles.outlineLabel).expandX().right();
        table.image(Icon.playersSmall).size(24f).color(team == Vars.player.team() ? Color.green : Color.white);
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
            t.keepInStage();
        }));
    }

    private void setupUnitsTable(Table table, TeamData data){
        table.left().top();

        int[] lastCap = new int[]{-1};

        Runnable rebuildUnits = () -> {
            table.clearChildren();

            int columns = Math.max(1, (int)(table.getWidth() / Scl.scl(44f)));

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
                Core.app.post(rebuildUnits);
            }
        });
    }

    private static class PowerInfoTable extends TemporaryFloatTable{
        private final Team team;
        private final PowerInfo info;
        private final Table consumerTable, producerTable;

        private PowerInfoTable(String name, Team team, PowerInfo info){
            super(name);

            this.team = team;
            this.info = info;

            consumerTable = new Table(Styles.black3);
            producerTable = new Table(Styles.black3);

            consumerTable.top();
            producerTable.top();

            update(this::rebuildInfo);
        }

        public static PowerInfoTable get(Team team, PowerInfo info){
            String name = Core.bundle.format("miner-tools.power-info", team.coloredName());
            return FloatManager.getOrCreate(name, () -> new PowerInfoTable(name, team, info));
        }

        @Override
        protected void setupTitle(){
            super.setupTitle();

            title.background(MinTex.getColoredRegion(team.color, 0.6f));
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

            int columns = Math.max(1, (int)(consumerTable.getWidth() / Scl.scl(120f)));

            ObjectFloatMap<Block> map = info.getConsumeMap();
            int index = 0;
            for(Block block : Vars.content.blocks()){
                if(!map.containsKey(block)) continue;

                float consume = map.get(block, 0f);
                consumerTable.table(container -> {
                    container.image(block.uiIcon).size(32f);
                    container.label(() -> Strings.autoFixed(consume, 1)).color(Color.red).padLeft(4f).growX();
                }).width(120f).left();

                if(++index % columns == 0){
                    consumerTable.row();
                }
            }
        }

        private void rebuildProducer(){
            producerTable.clear();

            int columns = Math.max(1, (int)(producerTable.getWidth() / Scl.scl(120f)));

            ObjectFloatMap<Block> map = info.getProductMap();
            int index = 0;
            for(Block block : Vars.content.blocks()){
                if(!map.containsKey(block)) continue;

                float product = map.get(block, 0f);
                producerTable.table(container -> {
                    container.image(block.uiIcon).size(32f);
                    container.label(() -> Strings.autoFixed(product, 1)).color(Color.green).padLeft(4f).growX();
                }).width(120f).pad(4f).left();

                if(++index % columns == 0){
                    producerTable.row();
                }
            }
        }
    }
}

