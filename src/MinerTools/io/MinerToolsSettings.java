package MinerTools.io;

import arc.files.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;

import java.io.*;

import static mindustry.Vars.modDirectory;

public class MinerToolsSettings{
    protected static final byte start = 15;
    protected static final byte typeBool = 0, typeInt = 1, typeLong = 2, typeFloat = 3, typeString = 4;

    private static final String settingsName = "settings";
    private static final String backupName = settingsName + ".backup";

    public Fi root, settings, backup;

    protected Seq<MinerSetting> minerSettings = new Seq<>();
    private boolean modified = false;

    public void init(){
        root = modDirectory.child("MinerTools");
        root.mkdirs();

        settings = root.child(settingsName);
        backup = root.child(backupName);

        try{
            if(!settings.exists()){
                settings.file().createNewFile();
            }
            if(!backup.exists()){
                backup.file().createNewFile();
            }
        }catch(IOException e){
            Log.err(e);
        }

        load();

        Timer.schedule(() -> {
            if(modified && !Vars.state.isGame()){
                save();
                modified = false;
            }
        }, 600f);
    }

    public MinerSetting findSetting(String name){
        return minerSettings.find(s -> s.name.equals(name));
    }

    public void put(String name, Object b){
        MinerSetting ms = findSetting(name);
        if(ms != null){
            ms.value = b;
        }else{
            minerSettings.add(new MinerSetting(name, b));
        }
    }

    public <T> T get(String name, T def){
        MinerSetting setting = findSetting(name);
        if(setting == null){
            return def;
        }
        return (T)setting.value;
    }

    public boolean getBool(String name){
        return get(name, false);
    }

    public int getInt(String name){
        return get(name, 0);
    }

    public long getLong(String name){
        return get(name, 0L);
    }

    public float getFloat(String name){
        return get(name, 0f);
    }

    public String getString(String name){
        return get(name, "");
    }

    private void load(){
        loadSettings();
    }

    private void loadSettings(){
        try{
            loadSettings(settings);
        }catch(Throwable e){
            try{
                loadSettings(backup);
            }catch(IOException ex){
                Log.info("MinerTools: Filed to load settings");
            }
        }
    }

    private void loadSettings(Fi fi) throws IOException{
        minerSettings.clear();

        var reads = fi.reads();
        if(reads.b() == start){
            int size = reads.i();
            for(int i = 0; i < size - 1; i++){
                String name = reads.str();

                byte type = reads.b();

                Object value;
                switch(type){
                    case typeBool -> value = reads.bool();
                    case typeInt -> value = reads.i();
                    case typeLong -> value = reads.l();
                    case typeFloat -> value = reads.f();
                    case typeString -> value = reads.str();
                    default -> throw new IOException("Field to load type: " + type);
                }

                minerSettings.add(new MinerSetting(name, value));
            }
        }
        throw new IOException("Field to load settings");
    }

    private void save(){
        settings.copyTo(backup);

        var writes = settings.writes();
        writes.b(start);

        writes.i(minerSettings.size);
        for(MinerSetting setting : minerSettings){
            Object value = setting.value;

            writes.str(setting.name);
            if(value instanceof Boolean b){
                writes.b(typeBool);
                writes.bool(b);
            }else if(value instanceof Integer i){
                writes.b(typeInt);
                writes.i(i);
            }else if(value instanceof Long l){
                writes.b(typeLong);
                writes.l(l);
            }else if(value instanceof Float f){
                writes.b(typeFloat);
                writes.f(f);
            }else if(value instanceof String s){
                writes.b(typeString);
                writes.str(s);
            }
        }

        writes.close();
    }

    public static class MinerSetting{
        public String name;
        public Object value;

        public MinerSetting(){}

        public MinerSetting(String name, Object value){
            this.name = name;
            this.value = value;
        }
    }
}
