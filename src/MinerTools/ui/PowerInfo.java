package MinerTools.ui;

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

import static mindustry.Vars.*;

public class PowerInfo{
    private static final Seq<Building> buildings = new Seq<>();
    private static final ObjectMap<Team, PowerInfo> teamPowerInfo = new ObjectMap<>();

    public Team team;

    public ObjectSet<PowerGraph> graphs = new ObjectSet<>();
    public ObjectMap<Block, ObjectSet<Building>> consumers = new ObjectMap<>();
    public ObjectMap<Block, ObjectSet<Building>> producers = new ObjectMap<>();

    private final Interval timer = new Interval();

    public PowerInfo(Team team){
        this.team = team;

        Events.run(Trigger.update, () -> {
            if(timer.get(2 * 60f)){
                updateCP();
            }
        });

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
        }

        for(Block block : content.blocks()){
            if(block.hasPower){
                consumers.put(block, new ObjectSet<>());
                producers.put(block, new ObjectSet<>());
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
        if(building.block.hasPower && building.power.graph.all.size <= 1){
            Log.info("Remove building: " + building);
            graphs.remove(building.power.graph);
        }
    }

    private void addBuild(Building building){
        if(building.block.hasPower){
            Log.info("Add building: " + building);
            graphs.add(building.power.graph);
        }
    }
    
    private void updateCP(){
        clearCP();

        for(PowerGraph graph : graphs){
            for(Building building : graph.consumers){
                consumers.get(building.block).add(building);
            }
            for(Building building : graph.producers){
                producers.get(building.block).add(building);
            }
        }
    }

    private void clearCP(){
        for(ObjectSet<Building> buildings : consumers.values()){
            buildings.clear();
        }
        for(ObjectSet<Building> buildings : producers.values()){
            buildings.clear();
        }
    }

    public int getPowerBalance(){
        float total = 0f;
        for(PowerGraph graph : graphs){
            total += graph.getPowerBalance();
        }
        return (int)(total * 60);
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

    public static void load(){
        teamPowerInfo.clear();

        for(TeamData teamData : Vars.state.teams.getActive()){
            teamPowerInfo.put(teamData.team, new PowerInfo(teamData.team));
        }
    }

    public static PowerInfo getPowerInfo(Team team){
        return teamPowerInfo.get(team);
    }
}
