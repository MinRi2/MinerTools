package MinerTools.graphics.renderer;

import MinerTools.graphics.draw.*;
import MinerTools.ui.settings.*;
import arc.math.geom.*;
import arc.struct.*;

public abstract class BaseRender<T extends Position>{
    protected final Seq<BaseDrawer<T>> allDrawers = new Seq<>();
    protected final Seq<BaseDrawer<T>> allCameraDrawers = new Seq<>();

    Seq<BaseDrawer<T>> enableDrawers;
    Seq<BaseDrawer<T>> enableCameraDrawers;

    public BaseRender<T> addDrawers(BaseDrawer<T>... drawers){
        for(BaseDrawer<T> drawer : drawers){
            allDrawers.addUnique(drawer);
        }
        return this;
    }

    public BaseRender<T> addCameraDrawers(BaseDrawer<T>... drawers){
        for(BaseDrawer<T> drawer : drawers){
            allCameraDrawers.addUnique(drawer);
        }

        return this;
    }

    public void updateEnable(){
        enableDrawers = allDrawers.select(BaseDrawer::enabled);
        enableCameraDrawers = allCameraDrawers.select(BaseDrawer::enabled);
    }

    public void updateSetting(){
        for(BaseDrawer<?> drawer : allDrawers){
            drawer.readSetting();
        }
        for(BaseDrawer<?> drawer : allCameraDrawers){
            drawer.readSetting();
        }
    }

    public void addSetting(MSettingsTable settings){
    }

    public void render(){
        if(allDrawers.any()){
            var validDrawers = enableDrawers.select(BaseDrawer::isValid);

            if(validDrawers.any()){
                globalRender(validDrawers);
            }
        }

        if(enableCameraDrawers.any()){
            var validCameraDrawers = enableCameraDrawers.select(BaseDrawer::isValid);

            if(validCameraDrawers.any()){
                cameraRender(validCameraDrawers);
            }
        }
    }

    public abstract void globalRender(Seq<BaseDrawer<T>> validDrawers);

    public abstract void cameraRender(Seq<BaseDrawer<T>> validDrawers);
}
