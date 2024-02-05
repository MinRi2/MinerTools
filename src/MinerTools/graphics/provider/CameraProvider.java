package MinerTools.graphics.provider;

import MinerTools.graphics.draw.*;
import arc.struct.*;

public abstract class CameraProvider<T extends Drawer<?>> extends DrawerProvider<T>{
    private final Seq<T> globalDrawers = new Seq<>();
    private final Seq<T> cameraDrawers = new Seq<>();

    private Seq<T> enableGlobalDrawers;
    private Seq<T> enableCameraDrawers;

    @SuppressWarnings("unchecked")
    public final CameraProvider<T> addGlobalDrawers(T... drawers){
        globalDrawers.addAll(drawers);
        addDrawers(drawers);
        return this;
    }

    @SuppressWarnings("unchecked")
    public final CameraProvider<T> addCameraDrawers(T... drawers){
        cameraDrawers.addAll(drawers);
        addDrawers(drawers);
        return this;
    }

    @Override
    public void updateEnable(){
        enableGlobalDrawers = globalDrawers.select(Drawer::isEnabled);
        enableCameraDrawers = cameraDrawers.select(Drawer::isEnabled);

        enableDrawers.clear();
        enableDrawers.addAll(enableGlobalDrawers).addAll(enableCameraDrawers);
    }

    @Override
    public void provide(){
        if(globalDrawers.any()){
            globalProvide(enableGlobalDrawers.select(Drawer::isValid));
        }

        if(cameraDrawers.any()){
            cameraProvide(enableCameraDrawers.select(Drawer::isValid));
        }
    }

    protected abstract void globalProvide(Seq<T> validDrawers);

    protected abstract void cameraProvide(Seq<T> validDrawers);
}
