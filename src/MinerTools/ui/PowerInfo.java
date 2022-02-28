package MinerTools.ui;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;

public class PowerInfo{
    private static final Seq<Building> buildings = new Seq<>();
    private static final ObjectMap<Team, PowerInfo> teamPowerInfo = new ObjectMap<>();
    private static final Interval timer = new Interval();

    public ObjectSet<PowerGraph> graphs = new ObjectSet<>();
    public ObjectMap<Block, ObjectSet<Building>> consumers;
    public ObjectMap<Block, ObjectSet<Building>> producers;

    public Team team;

    public PowerInfo(Team team){
        this.team = team;

        Events.run(Trigger.update, () -> {
            if(timer.get(5 * 60)){
                clearInfo();
            }
        });

       consumers =  Vars.content.blocks().asMap(block -> block, block -> new ObjectSet<>());
       producers = Vars.content.blocks().asMap(block -> block, block -> new ObjectSet<>());
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

    public void clear(){
        graphs.clear();
    }

    public void update(){
        buildings.clear();
        if(team.data().buildings != null){
            team.data().buildings.getObjects(buildings);

            for(Building building : buildings){
                if(building.block.hasPower && graphs.add(building.power.graph)){
                    // updateCP();
                }
            }
        }
        buildings.clear();
    }

    private void clearCP(){
        consumers.each(((block, buildings) -> buildings.clear()));
        producers.each(((block, buildings) -> buildings.clear()));
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

    public static void load(){
        teamPowerInfo.clear();

        for(TeamData teamData : Vars.state.teams.getActive()){
            teamPowerInfo.put(teamData.team, new PowerInfo(teamData.team));
        }
    }

    public static void updateInfo(Team team){
        if(teamPowerInfo.get(team) != null){
            teamPowerInfo.get(team).update();
        }
    }

    public static void clearInfo(Team team){
        if(teamPowerInfo.get(team) != null){
            teamPowerInfo.get(team).clear();
        }
    }

    public static void updateInfo(){
        teamPowerInfo.each(((team, powerInfo) -> powerInfo.update()));
    }

    public static void clearInfo(){
        teamPowerInfo.each(((team, powerInfo) -> powerInfo.clear()));
    }

    public static PowerInfo getPowerInfo(Team team){
        return teamPowerInfo.get(team);
    }
}
