package MinerTools.modules;

import MinerTools.modules.SpawnerInfo.*;
import arc.struct.*;

public class Modules{
    public static Seq<Module> modules;

    public static SpawnerInfo spawnerInfo;

    public static void init(){
        modules = new Seq<>();
        
        modules.addAll(
        spawnerInfo = new SpawnerInfo()
        );
    }

    public static void load(){
        for(Module module : modules){
            module.load();
        }
    }

}
