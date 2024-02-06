package MinerTools.ui.tables.floats;

import arc.func.*;
import arc.struct.*;

public class FloatManager{
    private static final ObjectMap<String, FloatTable> map = new ObjectMap<>();

    public static void add(FloatTable table){
        map.put(table.name, table);
    }

    public static void remove(FloatTable table){
        map.remove(table.name);
    }

    @SuppressWarnings("unchecked")
    public static <T extends FloatTable> T get(String name){
        return (T)map.get(name);
    }

    @SuppressWarnings("unchecked")
    public static <T extends FloatTable> T getOrCreate(String name, Prov<FloatTable> prov){
        return (T)map.get(name, prov);
    }
}
