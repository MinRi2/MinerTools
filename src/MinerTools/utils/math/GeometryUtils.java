package MinerTools.utils.math;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

public class GeometryUtils{
    /**
     * 返回矩形(rect)在给定方位是否与矩形(other)有公共边
     * @param align 判断方位
     * @return 有的话返回true
     */
    public static boolean hasCommonEdge(Rect rect, Rect other, int align){
        if(rect.overlaps(other)){
            return false;
        }

        float left = rect.x, right = left + rect.width;
        float bottom = rect.y, top = bottom + rect.height;

        float otherLeft = other.x, otherRight = otherLeft + other.width;
        float otherBottom = other.y, otherTop = otherBottom + other.height;

        if(Align.isRight(align) || Align.isLeft(align)){
            float ex = right;
            float oex = otherLeft;

            if(Align.isLeft(align)){
                ex = left;
                oex = otherRight;
            }

            return ((otherBottom >= bottom && otherBottom <= top)
            || otherTop >= bottom && otherTop <= top)
            && Mathf.equal(ex, oex);
        }

        if(Align.isTop(align) || Align.isBottom(align)){
            float ey = top;
            float oey = otherBottom;

            if(Align.isBottom(align)){
                ey = bottom;
                oey = otherTop;
            }

            return ((otherLeft >= left && otherLeft <= right)
            || otherRight >= left && otherRight <= right)
            && Mathf.equal(ey, oey);
        }

        return false;
    }
}
