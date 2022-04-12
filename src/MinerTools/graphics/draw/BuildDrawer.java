package MinerTools.graphics.draw;

import MinerTools.interfaces.*;
import mindustry.gen.*;

import java.lang.reflect.*;

public abstract class BuildDrawer<T extends Building> implements Drawable<T>{
    /* 泛型 */
    private Class clazz;

    public BuildDrawer(){
        var clazz = this.getClass();

        ParameterizedType type = (ParameterizedType)clazz.getGenericSuperclass();
        Type[] types = type.getActualTypeArguments();
        this.clazz = (Class)types[0];
    }

    @Override
    public void tryDraw(Building type){
        if(clazz.isAssignableFrom(type.getClass())){
            Drawable.super.tryDraw((T)type);
        }
    }
}
