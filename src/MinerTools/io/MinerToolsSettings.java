package MinerTools.io;

import arc.files.*;
import arc.struct.*;
import arc.util.*;

import java.io.*;

import static mindustry.Vars.modDirectory;

public class MinerToolsSettings{
    protected static final byte typeBool = 0, typeInt = 1, typeLong = 2, typeFloat = 3, typeString = 4;

    private static final String settingsName = "settings";
    private static final String backupName = settingsName + ".backup";

    private Fi root, settings, backup;

    protected Seq<MinerSetting> mSettings = new Seq<>();
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
            Log.err("MinerToolsSettings: Failed to create file: ", e);
        }

        load();
    }

    public MinerSetting findSetting(String name){
        return mSettings.find(s -> s.name.equals(name));
    }

    public void put(String name, Object obj){
        put(name, obj, false);
    }

    public void put(String name, Object obj, boolean unique){
        MinerSetting ms = findSetting(name);

        if(ms != null){
            if(unique) return;
            ms.value = obj;
        }else{
            mSettings.add(new MinerSetting(name, obj));
        }
        modified = true;

        save();
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
        if(settings.file().length() != 0L || backup.file().length() != 0L){
            loadSettings();
        }
        Log.info(mSettings);
    }

    private void loadSettings(){
        try{
            Log.info("MinerToolsSettings: Trying to load settings");
            loadSettings(settings);
        }catch(Throwable e){
            Log.err("MinerToolsSettings: Filed to load settings: ", e);
            Log.err("MinerToolsSettings: Trying to load backup: ", e);
            try{
                loadSettings(backup);
            }catch(IOException ex){
                Log.err("MinerToolsSettings: Filed to load backup: ", ex);
            }
        }
    }

    private void loadSettings(Fi fi) throws IOException{
        mSettings.clear();

        var reads = fi.reads();
        int size = reads.i();
        for(int i = 0; i < size; i++){
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

            Log.info("MinerToolsSettings: Read:" + name + "/ Value:" + value + "(" + type + ")");
            mSettings.add(new MinerSetting(name, value));
        }
    }

    private void save(){
        Log.info("MinerToolsSettings: Saving");
        settings.copyTo(backup);

        var writes = settings.writes();

        writes.i(mSettings.size);
        for(MinerSetting setting : mSettings){
            Log.info("MinerToolsSettings: Saving " + setting.name + ": " + setting.value);

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
        modified = false;
    }

    public static class MinerSetting{
        public String name;
        public Object value;

        public MinerSetting(){
        }

        public MinerSetting(String name, Object value){
            this.name = name;
            this.value = value;
        }
    }
}
