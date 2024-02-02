package MinerTools.graphics.provider;

import MinerTools.graphics.draw.*;
import arc.struct.*;

public abstract class DrawerProvider<T extends Drawer<?>>{
    protected Seq<T> drawers = new Seq<>();
    protected Seq<T> enableDrawers = new Seq<>();

    @SuppressWarnings("unchecked")
    protected DrawerProvider<T> addDrawers(T... drawers){
        this.drawers.addAll(drawers);
        return this;
    }

    protected DrawerProvider<T> addDrawers(Seq<T> drawers){
        this.drawers.addAll(drawers);
        return this;
    }

    public void updateEnable(){
        enableDrawers.selectFrom(drawers, Drawer::isEnabled);
    }

    public void updateSetting(){
        for(T drawer : drawers){
            drawer.readSetting();
        }
    }

    public final void drawShader(){
        for(T drawer : drawers){
            drawer.drawShader();
        }
    }

    public abstract void provide();
}
