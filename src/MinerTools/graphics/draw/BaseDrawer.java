package MinerTools.graphics.draw;

import arc.*;
import arc.math.geom.*;
import arc.util.*;

public abstract class BaseDrawer<T extends Position>{
    /* Draw in camera only */
    public boolean drawInCamera = true;

    /* Read some settings */
    public void readSetting(){};

    /* Return true if the drawer is enabled */
    public boolean enabled(){
        return true;
    };

    /* Return true if this drawer is valid in game */
    public boolean isValid(){
        return true;
    };

    /* Init before draw */
    public void init(){}

    /* Return true if the type is valid */
    public boolean isValid(T type){
        return true;
    };

    /* Return ture if the type is in camera */
    protected boolean inCamera(T type){
        return Core.camera.bounds(Tmp.r1).contains(Tmp.v1.set(type));
    }

    /* Try to draw */
    public void tryDraw(T type){
        if(isValid(type) && (!drawInCamera || inCamera(type))) draw(type);
    }

    /* Draw */
    public void draw(T type){};
}
