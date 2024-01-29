package MinerTools.graphics.provider;

import MinerTools.graphics.draw.*;
import arc.struct.*;

public abstract class CameraProvider<T extends Drawer<?>> extends DrawerProvider<T>{
    private final Seq<T> globalDrawers = new Seq<>();
    private final Seq<T> cameraDrawers = new Seq<>();

    @SuppressWarnings("unchecked")
    public final CameraProvider<T> addGlobalDrawers(T... drawers){
        globalDrawers.addAll(drawers);
        cameraDrawers.addAll(drawers);
        addDrawers(drawers);
        return this;
    }

    @SuppressWarnings("unchecked")
    public final CameraProvider<T> addCameraDrawers(T... drawers){
        cameraDrawers.addAll(drawers);
        cameraDrawers.addAll(drawers);
        addDrawers(drawers);
        return this;
    }

    @Override
    public void provide(){
        if(globalDrawers.any()){
            globalProvide(globalDrawers.select(Drawer::isValid));
        }

        if(cameraDrawers.any()){
            cameraProvide(cameraDrawers.select(Drawer::isValid));
        }
    }

    public abstract void globalProvide(Seq<T> validDrawers);

    public abstract void cameraProvide(Seq<T> validDrawers);
}
