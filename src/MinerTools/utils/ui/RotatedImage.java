package MinerTools.utils.ui;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.util.*;

public class RotatedImage extends Image{
    public int rotateStep;
    public int rotateCount, maxRotateCount;

    public RotatedImage(TransformDrawable drawable, int rotateStep){
        super(drawable, Scaling.stretch, Align.center);

        if(rotateStep == 0){
            throw new RuntimeException("Rotate step cannot be 0");
        }

        setRotateStep(rotateStep);
    }

    public RotatedImage(TextureRegion region, int rotateStep){
        this(new TextureRegionDrawable(region), rotateStep);
    }

    public void setRotateStep(int rotateStep){
        setRotateStep(rotateStep, true);
    }

    public void setRotateStep(int rotateStep, boolean resetRotation){
        this.rotateStep = rotateStep;
        maxRotateCount = 360 / rotateStep + 1;

        if(resetRotation){
            rotation = 0;
        }
    }

    public void rotate(){
        rotate(0f, Interp.linear);
    }

    public void rotate(float duration, Interp interp){
        rotate(++rotateCount, duration, interp);
    }

    public void rotate(int rotateCount, float duration, Interp interp){
        this.rotateCount = rotateCount % maxRotateCount;

        actions(Actions.rotateTo(this.rotateCount * rotateStep, duration, interp));
    }
}
