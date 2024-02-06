package MinerTools.utils.ui;

import MinerTools.utils.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.util.*;

public class BorderSnapper{
    // 目标元素
    private final Element targetElem;

    // 吸附的元素
    public Element snapElem;

    public boolean pause;

    // 目标元素相对于吸附元素的原点坐标
    private float relativeX, relativeY;
    // 记录吸附元素的坐标，用于判断坐标是否改变
    private float lastX, lastY;

    public BorderSnapper(Element targetElem){
        this.targetElem = targetElem;

        // 植入Action
        targetElem.addAction(Actions.forever(Actions.run(this::updateSnap)));
    }

    public void cancelSnapping(){
        snapElem = null;
    }

    public boolean setSnap(Element snapElem, int snapAlign){
        if(canSnap(snapElem, snapAlign)){
            this.snapElem = snapElem;

            Vec2 pos = Tmp.v1.setZero();
            ElementUtils.localToTargetCoordinate(targetElem, snapElem.parent, pos);

            relativeX = snapElem.x - pos.x;
            relativeY = snapElem.y - pos.y;

            return true;
        }

        return false;
    }

    public void resume(){
        pause = false;
    }

    public void pause(){
        pause = true;
    }

    private void updateSnap(){
        if(pause || snapElem == null) return;

        if(!snapElem.hasParent()) return;

        if(updateSnapChanged()){
            float sx = snapElem.x;
            float sy = snapElem.y;

            targetElem.setPosition(sx - relativeX, sy - relativeY);
            targetElem.keepInStage();
        }
    }

    private boolean updateSnapChanged(){
        float sx = snapElem.x;
        float sy = snapElem.y;

        if(sx != lastX || sy != lastY){
            lastX = sx;
            lastY = sy;

            return true;
        }

        lastX = sx;
        lastY = sy;

        return false;
    }

    private boolean canSnap(Element snapElem, int snapAlign){
        if(snapElem == null || snapAlign == 0){
            return false;
        }

        if(!snapElem.hasParent()){
            return false;
        }

        Rect targetBounds = ElementUtils.getBoundsOnScene(targetElem, Tmp.r1);
        Rect snapBounds = ElementUtils.getBoundsOnScene(snapElem, Tmp.r2);
        return GeometryUtils.hasCommonEdge(targetBounds, snapBounds, snapAlign);
    }

}
