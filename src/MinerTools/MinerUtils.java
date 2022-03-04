package MinerTools;

import arc.util.*;

import java.lang.reflect.*;

public class MinerUtils{
    public static Field getField(Class clazz, String name){
        try{
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Field getField(Object object, String name){
        try{
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
