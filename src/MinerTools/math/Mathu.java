package MinerTools.math;

import arc.math.geom.*;
import arc.util.*;

public class Mathu{

    public static Vec2 getCentroid(float[] points, Vec2 out){
        int size = (points.length - 1) / 2;
        if(size == 0){
            return out.set(points[0], points[1]);
        }else if(size == 1){
            Vec2 v1 = Tmp.v1.set(points[0], points[1]);
            Vec2 v2 = Tmp.v2.set(points[2], points[3]);

            return out.set(v1).mulAdd(v2.sub(v1), 0.5f);
        }else{
            points[0] += 1f;
            points[1] += 1.5f;
            points[2] += 1.5f;
            points[3] += 1f;
            return Geometry.polygonCentroid(points, 0, points.length, out);
        }
    }

}
