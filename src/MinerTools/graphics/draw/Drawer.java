package MinerTools.graphics.draw;

import arc.math.geom.*;

public abstract class Drawer<T extends Position>{

    /* Read some settings */
    public void readSetting(){
    }

    /* Return true if the drawer is enabled */
    public boolean isEnabled(){
        return true;
    }

    /* Return true if the drawer is valid in game */
    public boolean isValid(){
        return true;
    }

    /* Return true if the drawer should draw this type */
    public boolean shouldDraw(T type){
        return true;
    }

    /* Draw Shader */
    public void drawShader(){
    }

    /* Try to draw */
    public void tryDraw(T type){
        if(shouldDraw(type)) draw(type);
    }

    /* Draw */
    protected abstract void draw(T type);

}
