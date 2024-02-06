package MinerTools.game;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.consumers.*;

/**
 * 调用{@link #getPowerInfo(Team)}获取{@link PowerInfo}对象
 * <p>
 * {@link PowerInfo}对象一直会保留
 * <p>
 * 在电网更新或游戏重置会自动更新数据
 * @author minri2
 */
public class PowerInfo{
    public static final Seq<PowerGraph> activeGraphs = new Seq<>();
    public static final ObjectMap<Team, PowerInfo> powerInfoMap = new ObjectMap<>();
    private static final Seq<Building> tempBuildings = new Seq<>();
    private static final ObjectFloatMap<Block> tempMap = new ObjectFloatMap<>();
    private static int lastSize = -1;

    static{
        Events.run(Trigger.update, PowerInfo::update);
        Events.on(ResetEvent.class, e -> {
            lastSize = -1;
            activeGraphs.clear();

            for(Entry<Team, PowerInfo> entry : powerInfoMap){
                entry.value.clearGraph();
            }
        });
    }

    public ObjectSet<PowerGraph> graphs = new ObjectSet<>();

    private PowerInfo(){
    }

    public static void update(){
        int size = Groups.powerGraph.size();

        if(size != lastSize){
            lastSize = size;

            activeGraphs.clear();

            if(size != 0){
                for(PowerGraphUpdaterc powerGraphUpdaterc : Groups.powerGraph){
                    activeGraphs.add(powerGraphUpdaterc.graph());
                }
            }

            updateTeamPowerInfo();
        }
    }

    private static void updateTeamPowerInfo(){
        for(Entry<Team, PowerInfo> entry : powerInfoMap){
            PowerInfo info = entry.value;
            info.clearGraph();
        }

        for(PowerGraph graph : activeGraphs){
            if(graph.all.isEmpty()) continue;

            Team team = (graph.all.first()).team;
            PowerInfo info = powerInfoMap.get(team, PowerInfo::new);
            info.addGraph(graph);
        }
    }

    /**
     * {@link PowerInfo} 在需要时才会更新数据!
     * @param team 获取的队伍
     * @return {@link PowerInfo}
     */
    public static PowerInfo getPowerInfo(Team team){
        return powerInfoMap.get(team, PowerInfo::new);
    }

    public void addGraph(PowerGraph graph){
        graphs.add(graph);
    }

    public void clearGraph(){
        graphs.clear();
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

