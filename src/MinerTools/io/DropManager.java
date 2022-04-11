package MinerTools.io;

import arc.files.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;

import static mindustry.Vars.*;

public class DropManager{
    private static Fi root;
    private static String settingName = "dropSettings";

    public static void init(){
        root = modDirectory.child("MinerTools");
        root.mkdirs();
    }

    public static void loadSetting(ObjectMap<ItemTurret, Seq<Item>> map){
        Fi settingf = root.child(settingName);
        Fi backupf = root.child(settingName + ".backup");
        if(!settingf.exists()){
            if(backupf.exists() && backupf.file().length() > 0L){
                readFi(backupf, map);
            }else{
                saveDefault();
            }
            return;
        }
        readFi(settingf, map);
    }

    public static void readFi(Fi fi, ObjectMap<ItemTurret, Seq<Item>> map){
        Reads reads = null;
        try{
            reads = fi.reads();
            int mapSize = reads.i();
            for(int i = 0; i < mapSize; i++){
                int turretID = reads.i();
                ItemTurret turret = (ItemTurret)content.block(turretID);

                int size = reads.i();
                Seq<Item> seq = new Seq();
                for(int j = 0; j < size; j++){
                    int id = reads.i();
                    seq.add(content.item(id));
                }

                map.put(turret, seq);
            }
        }catch(Exception e){
            ui.showException(e);
        }finally{
            if(reads != null) reads.close();
        }
    }

    public static void save(ObjectMap<ItemTurret, Seq<Item>> map){
        // backup
        Fi settingf = root.child(settingName);
        if(settingf.exists()) {
            settingf.moveTo(root.child(settingName + ".backup"));
        }
        // save
        Writes writes = settingf.writes();
        writes.i(map.size);
        for(Entry<ItemTurret, Seq<Item>> entry : map.entries()){
            writes.i(entry.key.id);

            writes.i(entry.value.size);
            for(Item item : entry.value){
                writes.i(item.id);
            }
        }
        writes.close();
    }

    public static void saveDefault(){
        ObjectMap<ItemTurret, Seq<Item>> objectMap = new ObjectMap<>();
        for(Block block : content.blocks()){
            if(block instanceof ItemTurret itemTurret){
                objectMap.put(itemTurret, itemTurret.ammoTypes.keys().toSeq());
            }
        }
        save(objectMap);
    }
}
