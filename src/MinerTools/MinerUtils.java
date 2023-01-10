package MinerTools;

import arc.util.*;

import java.lang.reflect.*;

public class MinerUtils{
    public static Field getField(Class<?> clazz, String name){
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

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes){
        try{
            Method method = clazz.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static <T> T setValue(Field field, Object object, T value){
        try{
            field.set(object, value);
            return value;
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

    public static <T> @Nullable T invokeMethod(Object object, Method method, Object... args){
        try{
            Object result = method.invoke(object, args);

            if(method.getReturnType() == void.class){
                return null;
            }

            return (T)result;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
