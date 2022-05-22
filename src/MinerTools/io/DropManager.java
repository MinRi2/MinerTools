package MinerTools.io;

import arc.files.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.io.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;

import java.io.*;

import static mindustry.Vars.*;

public class DropManager{
    private static Fi root;
    private static final String settingName = "dropSettings";

    public static void init(){
        root = modDirectory.child("MinerTools");
        root.mkdirs();
    }

    public static void loadSetting(ObjectMap<String, Seq<Item>> map){
        Fi settingFi = root.child(settingName);
        Fi backupFi = root.child(settingName + ".backup");
        if(!settingFi.exists()){
            if(backupFi.exists() && backupFi.file().length() > 0L){
                readFi(backupFi, map);
            }else{
                saveDefault();
            }
            return;
        }
        readFi(settingFi, map);
    }

    public static void readFi(Fi fi, ObjectMap<String, Seq<Item>> map){
        try(DataInputStream reads = new DataInputStream(fi.read(Streams.defaultBufferSize))){
            int mapSize = reads.readInt();
            for(int i = 0; i < mapSize; i++){
                String turretName = reads.readUTF();

                int size = reads.readInt();
                Seq<Item> seq = new Seq<>();
                for(int j = 0; j < size; j++){
                    int id = reads.readInt();
                    seq.add(content.item(id));
                }

                map.put(turretName, seq);
            }
        }catch(Exception e){
            fi.delete();
            ui.showException(e);
        }
    }

    public static void save(ObjectMap<String, Seq<Item>> map){
        // backup
        Fi settingFi = root.child(settingName);
        if(settingFi.exists()) {
            settingFi.moveTo(root.child(settingName + ".backup"));
        }
        // save
        Writes writes = settingFi.writes();
        writes.i(map.size);
        for(Entry<String, Seq<Item>> entry : map.entries()){
            writes.str(entry.key);

            writes.i(entry.value.size);
            for(Item item : entry.value){
                writes.i(item.id);
            }
        }
        writes.close();
    }

    public static void saveDefault(){
        ObjectMap<String, Seq<Item>> objectMap = new ObjectMap<>();
        for(Block block : content.blocks()){
            if(block instanceof ItemTurret itemTurret){
                objectMap.put(itemTurret.name, itemTurret.ammoTypes.keys().toSeq());
            }
        }
        save(objectMap);
    }
}
