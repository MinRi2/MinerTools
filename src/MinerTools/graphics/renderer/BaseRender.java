package MinerTools.graphics.renderer;

import MinerTools.graphics.draw.*;
import arc.struct.*;

public abstract class BaseRender<T extends BaseDrawer<?>>{
    protected final Seq<T> allGlobalDrawers = new Seq<>();
    protected final Seq<T> allCameraDrawers = new Seq<>();

    Seq<T> enableDrawers;
    Seq<T> enableCameraDrawers;

    @SuppressWarnings("unchecked")
    public final BaseRender<T> addDrawers(T... drawers){
        allGlobalDrawers.addAll(drawers);
        return this;
    }

    @SuppressWarnings("unchecked")
    public final BaseRender<T> addCameraDrawers(T... drawers){
        allCameraDrawers.addAll(drawers);
        return this;
    }

    public void updateEnable(){
        enableDrawers = allGlobalDrawers.select(BaseDrawer::enabled);
        enableCameraDrawers = allCameraDrawers.select(BaseDrawer::enabled);
    }

    public void updateSetting(){
        for(BaseDrawer<?> drawer : allGlobalDrawers){
            drawer.readSetting();
        }
        for(BaseDrawer<?> drawer : allCameraDrawers){
            drawer.readSetting();
        }
    }

    public void render(){
        if(allGlobalDrawers.any()){
            var validDrawers = enableDrawers.select(BaseDrawer::isValid);

            if(validDrawers.any()){
                drawShader(validDrawers);

                globalRender(validDrawers);
            }
        }

        if(enableCameraDrawers.any()){
            var validCameraDrawers = enableCameraDrawers.select(BaseDrawer::isValid);

            if(validCameraDrawers.any()){
                drawShader(validCameraDrawers);

                cameraRender(validCameraDrawers);
            }
        }
    }

    public void drawShader(Seq<T> validDrawers){
        for(T drawer : validDrawers){
            drawer.drawShader();
        }
    }

    public abstract void globalRender(Seq<T> validDrawers);

    public abstract void cameraRender(Seq<T> validDrawers);

}
