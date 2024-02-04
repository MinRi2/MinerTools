package MinerTools.utils;

import arc.util.*;

public class AlignUtils{

    /**
     * 反转方向 例如: bottomLeft -> topRight
     */
    public static int flip(int align){
        return flipX(flipY(align));
    }

    /**
     * 反转横向 例如: bottomLeft -> bottomRight
     */
    public static int flipX(int align){
        if(Align.isLeft(align)){
            align &= ~Align.left;
            align |= Align.right;
        }else if(Align.isRight(align)){
            align &= ~Align.right;
            align |= Align.left;
        }
        return align;
    }

    /**
     * 反转纵向 例如: bottomLeft -> topLeft
     */
    public static int flipY(int align){
        if(Align.isTop(align)){
            align &= ~Align.top;
            align |= Align.bottom;
        }else if(Align.isBottom(align)){
            align &= ~Align.bottom;
            align |= Align.top;
        }

        return align;
    }
}
