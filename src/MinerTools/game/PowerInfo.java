package MinerTools.game;

import arc.math.*;
import arc.struct.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.consumers.*;

public class PowerInfo{
    public static final Seq<PowerGraph> activeGraphs = new Seq<>();
    public static final Seq<Building> tempBuildings = new Seq<>();
    private static final ObjectMap<Team, PowerInfo> powerInfoMap = new ObjectMap<>();
    private static final ObjectFloatMap<Block> tempMap = new ObjectFloatMap<>();
    public ObjectSet<PowerGraph> graphs = new ObjectSet<>();

    private PowerInfo(){
    }

    public static void update(){
        int lastSize = PowerInfo.activeGraphs.size;
        int size = Groups.powerGraph.size();
        if(lastSize != size){
            activeGraphs.clear();
            for(PowerGraphUpdaterc powerGraphUpdaterc : Groups.powerGraph){
                activeGraphs.add(powerGraphUpdaterc.graph());
            }
            PowerInfo.updateTeamPowerInfo();
        }
    }

    private static void updateTeamPowerInfo(){
        powerInfoMap.clear();
        for(PowerGraph graph : activeGraphs){
            if(!graph.all.any()) continue;
            Team team = (graph.all.first()).team;
            PowerInfo info =

            powerInfoMap.get(team, PowerInfo::new);
            info.addGraph(graph);
        }
    }

    public static PowerInfo getPowerInfo(Team team){
        return (PowerInfo)powerInfoMap.get(team);
    }

    public void addGraph(PowerGraph graph){
        this.graphs.add(graph);
    }

    public int getPowerBalance(){
        float total = 0.0f;
        for(PowerGraph graph : this.graphs){
            total += graph.getPowerBalance();
        }
        return (int)(total * 60.0f);
    }

    public float getOutput(){
        return this.getPowerProduced() - this.getPowerNeeded();
    }

    public float getSatisfaction(){
        if(Mathf.zero(this.getPowerProduced())){
            return 0.0f;
        }
        if(Mathf.zero(this.getPowerNeeded())){
            return 1.0f;
        }
        return Mathf.clamp(this.getPowerProduced() / this.getPowerNeeded());
    }

    public float getPowerProduced(){
        float total = 0.0f;
        for(PowerGraph graph : this.graphs){
            total += graph.getPowerProduced();
        }
        return total;
    }

    public float getPowerNeeded(){
        float total = 0.0f;
        for(PowerGraph graph : this.graphs){
            total += graph.getPowerNeeded();
        }
        return total;
    }

    public float getBatteryStored(){
        float total = 0.0f;
        for(PowerGraph graph : this.graphs){
            total += graph.getBatteryStored();
        }
        return total;
    }

    public float getBatteryCapacity(){
        float total = 0.0f;
        for(PowerGraph graph : this.graphs){
            total += graph.getBatteryCapacity();
        }
        return total;
    }

    public ObjectFloatMap<Block> getConsumeMap(){
        tempMap.clear();
        tempBuildings.clear();
        for(PowerGraph graph : this.graphs){
            tempBuildings.addAll(graph.consumers);
        }
        for(Building building : tempBuildings){
            Block block = building.block;
            ConsumePower consumePower = building.block.consPower;
            float power = consumePower.requestedPower(building) * building.timeScale() * 60.0f;
            tempMap.increment(block, 0.0f, power);
        }
        return tempMap;
    }

    public ObjectFloatMap<Block> getProductMap(){
        tempMap.clear();
        tempBuildings.clear();
        for(PowerGraph graph : this.graphs){
            tempBuildings.addAll(graph.producers);
        }
        for(Building building : tempBuildings){
            Block block = building.block;
            float power = building.getPowerProduction() * building.timeScale() * 60.0f;
            tempMap.increment(block, 0.0f, power);
        }
        return tempMap;


    }
}

