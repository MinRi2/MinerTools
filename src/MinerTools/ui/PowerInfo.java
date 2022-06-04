package MinerTools.ui;

import MinerTools.*;
import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
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
    private static final Field lastFrameUpdatedField;

    private static final Seq<Building> tmp = new Seq<>();
    private static final Seq<PowerInfo> teamsInfo = new Seq<>();

    static{
        lastFrameUpdatedField = MinerUtils.getField(PowerGraph.class, "lastFrameUpdated");
    }

    public Team team;

    public ObjectSet<PowerGraph> graphs = new ObjectSet<>();

    public ObjectSet<Building> all = new ObjectSet<>();
    public ObjectMap<Block, ObjectSet<Building>> consumers = new ObjectMap<>();
    public ObjectMap<Block, ObjectSet<Building>> producers = new ObjectMap<>();

    private boolean requireInitGraphs;
    private final Interval timer = new Interval();

    public PowerInfo(Team team){
        this.team = team;

        for(Block block : content.blocks()){
            if(block.hasPower){
                if(block.consumesPower){
                    consumers.put(block, new ObjectSet<>());
                }
                if(block.outputsPower){
                    producers.put(block, new ObjectSet<>());
                }
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

        initBuildings();
    }

    private void initBuildings(){
        var buildings = team.data().buildings;
        if(buildings != null){
            buildings.getObjects(tmp);

            for(Building building : tmp){
                addBuild(building);
            }

            tmp.clear();
        }
    }

    private void initGraphs(){
        graphs.clear();

        if(!all.isEmpty()){
            for(Building building : all){
                if(building.power != null) graphs.add(building.power.graph);
            }
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

        all.remove(building);

        requireInitGraphs = true;

        var consSet = consumers.get(building.block);
        if(consSet != null){
            consSet.remove(building);
        }

        var proSet = producers.get(building.block);
        if(proSet != null){
            proSet.remove(building);
        }
    }

    private void addBuild(Building building){
        if(!building.block.hasPower || building.power == null){
            return;
        }

        all.add(building);

        requireInitGraphs = true;

        var consSet = consumers.get(building.block);
        var proSet = producers.get(building.block);

        if(consSet != null && proSet != null && !building.block.consumes.getPower().buffered){
            consSet.add(building);
            proSet.add(building);
        }else if(consSet != null){
            consSet.add(building);
        }else if(proSet != null){
            proSet.add(building);
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

    public void update(){
        if(timer.get(0, 10f) && requireInitGraphs){
            initGraphs();
            requireInitGraphs = false;
        }

        updateActive();
    }

    public void updateActive(){
        for(PowerGraph graph : graphs){
            if(graph == null){
                continue;
            }

            if(state.isPaused()) MinerUtils.setValue(lastFrameUpdatedField, graph, graphics.getFrameId());
            if(!(graphics.getFrameId() - MinerUtils.<Long>getValue(lastFrameUpdatedField, graph) < 2L)){
                graphs.remove(graph);
            }
        }
    }

    public static void load(){
        teamsInfo.clear();

        for(TeamData teamData : Vars.state.teams.getActive()){
            teamsInfo.add(new PowerInfo(teamData.team));
        }
    }

    public static PowerInfo getPowerInfo(Team team){
        return teamsInfo.find(info -> info.team == team);
    }

    public static void updateAll(){
        for(PowerInfo powerInfo : teamsInfo){
            powerInfo.update();
        }
    }
}