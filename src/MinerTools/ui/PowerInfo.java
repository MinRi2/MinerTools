package MinerTools.ui;

import MinerTools.*;
import arc.*;
import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;

import java.lang.reflect.*;

import static arc.Core.graphics;
import static mindustry.Vars.*;

public class PowerInfo{
    private static Field lastFrameUpdatedField;

    private static final Seq<Building> buildings = new Seq<>();
    private static final ObjectMap<Team, PowerInfo> teamPowerInfo = new ObjectMap<>();

    {
        lastFrameUpdatedField = MinerUtils.getField(PowerGraph.class, "lastFrameUpdated");
    }

    public Team team;

    public ObjectSet<PowerGraph> graphs = new ObjectSet<>();
    public ObjectMap<Block, ObjectSet<Building>> consumers = new ObjectMap<>();
    public ObjectMap<Block, ObjectSet<Building>> producers = new ObjectMap<>();

    public PowerInfo(Team team){
        this.team = team;

        for(Block block : content.blocks()){
            if(block.consumesPower){
                consumers.put(block, new ObjectSet<>());
            }
            if(block.outputsPower){
                producers.put(block, new ObjectSet<>());
            }
        }

        Events.on(TilePreChangeEvent.class, event -> {
            if(state.isEditor()) return;
            removeTile(event.tile);
        });

        Events.on(TileChangeEvent.class, event -> {
            if(state.isEditor()) return;
            addTile(event.tile);
        });

        if(team.data().buildings != null){
            buildings.clear();

            team.data().buildings.getObjects(buildings);
            for(Building building : buildings){
                addBuild(building);
            }

            buildings.clear();
        }
    }

    private void removeTile(Tile tile){
        if(tile.build != null && tile.build.team == team && tile.isCenter()){
            removeBuild(tile.build);
        }
    }

    private void addTile(Tile tile){
        if(tile.build != null && tile.build.team == team && tile.isCenter()){
            addBuild(tile.build);
        }
    }

    private void removeBuild(Building building){
        if(!building.block.hasPower || building.power == null){
            return;
        }

        if(building.block.consumesPower){
            consumers.get(building.block).remove(building);
        }
        if(building.block.outputsPower){
            producers.get(building.block).remove(building);
        }
    }

    private void addBuild(Building building){
        if(!building.block.hasPower || building.power == null){
            return;
        }

        graphs.add(building.power.graph);

        if(building.block.outputsPower && building.block.consumesPower && !building.block.consumes.getPower().buffered){
            producers.get(building.block).add(building);
            consumers.get(building.block).add(building);
        }else if(building.block.outputsPower){
            producers.get(building.block).add(building);
        }else if(building.block.consumesPower){
            consumers.get(building.block).add(building);
        }
    }

    public int getPowerBalance(){
        float total = 0f;
        for(PowerGraph graph : graphs){
            total += graph.getPowerBalance();
        }
        return (int)(total * 60);
    }

    public float getOutput(){
        return getPowerProduced() - getPowerNeeded();
    }

    public float getSatisfaction(){
        if(Mathf.zero(getPowerProduced())){
            return 0f;
        }else if(Mathf.zero(getPowerNeeded())){
            return 1f;
        }
        return Mathf.clamp(getPowerProduced() / getPowerNeeded());
    }

    public float getPowerProduced(){
        float total = 0f;
        for(PowerGraph graph : graphs){
            total += graph.getPowerProduced();
        }
        return total;
    }

    public float getPowerNeeded(){
        float total = 0f;
        for(PowerGraph graph : graphs){
            total += graph.getPowerNeeded();
        }
        return total;
    }

    public float getBatteryStored(){
        float total = 0f;
        for(PowerGraph graph : graphs){
            total += graph.getBatteryStored();
        }
        return total;
    }

    public float getBatteryCapacity(){
        float total = 0f;
        for(PowerGraph graph : graphs){
            total += graph.getBatteryCapacity();
        }
        return total;
    }

    public float getConsPower(Block block){
        float sum = 0f;
        for(Building building : consumers.get(block)){
            sum += Mathf.num(building.shouldConsume()) * building.power.status * building.block.consumes.getPower().usage * 60 * building.timeScale();
        }
        return sum;
    }

    public float getProdPower(Block block){
        float sum = 0f;
        for(Building building : producers.get(block)){
            sum += building.getPowerProduction() * building.timeScale() * 60f;
        }
        return sum;
    }

    private void updateGraph(){
        graphs.clear();

        consumers.each((block, buildings) -> {
            for(Building building : buildings){
                graphs.add(building.power.graph);
            }
        });

        producers.each((block, buildings) -> {
            for(Building building : buildings){
                graphs.add(building.power.graph);
            }
        });
    }

    public void updateActive(){
        for(PowerGraph graph : graphs){
            if(graph == null){
                continue;
            }
            if(state.isPaused()) MinerUtils.setValue(lastFrameUpdatedField, graph, graphics.getFrameId());
            if(!(graphics.getFrameId() - MinerUtils.<Long>getValue(lastFrameUpdatedField, graph) < 2L)){
                updateGraph();
            }
        }
    }

    public static void load(){
        teamPowerInfo.clear();

        for(TeamData teamData : Vars.state.teams.getActive()){
            teamPowerInfo.put(teamData.team, new PowerInfo(teamData.team));
        }
    }

    public static PowerInfo getPowerInfo(Team team){
        return teamPowerInfo.get(team);
    }

    public static void updateAll(){
        teamPowerInfo.each((team, powerInfo) -> powerInfo.updateActive());
    }
}
