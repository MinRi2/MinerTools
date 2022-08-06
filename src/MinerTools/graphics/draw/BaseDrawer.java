package MinerTools.graphics.draw;

import arc.math.geom.*;

public abstract class BaseDrawer<T extends Position>{

    /* Read some settings */
    public void readSetting(){}

    /* Return true if the drawer is enabled */
    public boolean enabled(){
        return true;
    }

    /* Return true if this drawer is valid in game */
    public boolean isValid(){
        return true;
    }

    /* Init before draw */
    public void init(){}

    /* Return true if the type is valid */
    public boolean isValid(T type){
        return true;
    }

    /* Draw Shader */
    public void drawShader(){};

    /* Try to draw */
    public void tryDraw(T type){
        init();
        if(isValid(type)) draw(type);
    }

    /* Draw */
    protected abstract void draw(T type);

}
