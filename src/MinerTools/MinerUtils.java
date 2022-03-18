package MinerTools;

import arc.util.*;
import org.jetbrains.annotations.*;

import java.lang.reflect.*;

public class MinerUtils{
    public static @NotNull Field getField(Class clazz, String name){
        try{
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static @NotNull Field getField(Object object, String name){
        try{
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Object setValue(Field field, Object object, Object value){
        try{
            field.set(object, value);
            return object;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static <T> T getValue(Field field, Object object){
        try{
            return (T)field.get(object);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
