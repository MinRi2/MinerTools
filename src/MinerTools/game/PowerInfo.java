package MinerTools.game;

import arc.math.*;
import arc.struct.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.consumers.*;

public class PowerInfo{
    public static final PowerInfo emptyInfo = new PowerInfo();

    public static final Seq<PowerGraph> activeGraphs = new Seq<>();
    public static final Seq<PowerGraphUpdaterc> lastGraphs = new Seq<>();

    private static final Seq<PowerGraphUpdaterc> tempUpdaters = new Seq<>();
    private static final Seq<Building> tempBuildings = new Seq<>();
    private static final ObjectFloatMap<Block> tempMap = new ObjectFloatMap<>();

    private static final ObjectMap<Team, PowerInfo> powerInfoMap = new ObjectMap<>();

    public ObjectSet<PowerGraph> graphs = new ObjectSet<>();

    private PowerInfo(){
    }

    public static void update(){
        Groups.powerGraph.copy(tempUpdaters);

        if(lastGraphs.size != tempUpdaters.size){
            activeGraphs.clear();

            for(PowerGraphUpdaterc powerGraphUpdaterc : Groups.powerGraph){
                activeGraphs.add(powerGraphUpdaterc.graph());
            }

            updateTeamPowerInfo();
        }
    }

    private static void updateTeamPowerInfo(){
        powerInfoMap.clear();

        if(activeGraphs.isEmpty()){
            return;
        }

        for(PowerGraph graph : activeGraphs){
            if(graph.all.isEmpty()) continue;

            Team team = (graph.all.first()).team;
            PowerInfo info = powerInfoMap.get(team, PowerInfo::new);
            info.addGraph(graph);
        }
    }

    public static PowerInfo getPowerInfo(Team team){
        return powerInfoMap.get(team, emptyInfo);
    }

    public void addGraph(PowerGraph graph){
        graphs.add(graph);
    }

    public int getPowerBalance(){
        if(graphs.isEmpty()){
            return 0;
        }
        
        float total = 0.0f;
        for(PowerGraph graph : graphs){
            total += graph.getPowerBalance();
        }
        return (int)(total * 60.0f);
    }

    public float getOutput(){
        return getPowerProduced() - getPowerNeeded();
    }

    public float getSatisfaction(){
        if(Mathf.zero(getPowerProduced())){
            return 0.0f;
        }
        if(Mathf.zero(getPowerNeeded())){
            return 1.0f;
        }
        return Mathf.clamp(getPowerProduced() / getPowerNeeded());
    }

    public float getPowerProduced(){
        if(graphs.isEmpty()){
            return 0;
        }
        
        float total = 0.0f;
        for(PowerGraph graph : graphs){
            total += graph.getPowerProduced();
        }
        return total;
    }

    public float getPowerNeeded(){
        if(graphs.isEmpty()){
            return 0;
        }

        float total = 0.0f;
        for(PowerGraph graph : graphs){
            total += graph.getPowerNeeded();
        }
        return total;
    }

    public float getBatteryStored(){
        if(graphs.isEmpty()){
            return 0;
        }

        float total = 0.0f;
        for(PowerGraph graph : this.graphs){
            total += graph.getBatteryStored();
        }
        return total;
    }

    public float getBatteryCapacity(){
        if(graphs.isEmpty()){
            return 0;
        }

        float total = 0.0f;
        for(PowerGraph graph : graphs){
            total += graph.getBatteryCapacity();
        }
        return total;
    }

    public ObjectFloatMap<Block> getConsumeMap(){
        tempMap.clear();
        tempBuildings.clear();

        if(graphs.isEmpty()){
            return tempMap;
        }

        for(PowerGraph graph : graphs){
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

        if(graphs.isEmpty()){
            return tempMap;
        }

        for(PowerGraph graph : graphs){
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

