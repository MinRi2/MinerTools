package MinerTools.graphics.draw;

import mindustry.gen.*;

import java.lang.reflect.*;

public abstract class BuildDrawer<T extends Building> extends BaseDrawer<T>{
    /* The type of draw */
    private final Class<? extends Building> clazz;

    public BuildDrawer(){
        var clazz = this.getClass();

        ParameterizedType type = (ParameterizedType)clazz.getGenericSuperclass();
        Type[] types = type.getActualTypeArguments();
        this.clazz = (Class)types[0];
    }

    @Override
    public void tryDraw(Building type){
        if(clazz.isAssignableFrom(type.getClass())){
            super.tryDraw((T)type);
        }
    }
}
