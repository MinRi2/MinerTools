package MinerTools.override;

import arc.math.geom.*;
import mindustry.*;
import mindustry.input.*;

public class ObserverMode{
    private static final Vec2 movement = new Vec2(){
        @Override
        public Vec2 setAngle(float degrees){
            return super.setZero();
        }
    };
    private static boolean observing = false;
    private static Vec2 oldMovement;

    public static void toggle(){

        if(Vars.mobile){
            mobileObserver();
        }

    }

    public static void mobileObserver(){
        if(Vars.control.input instanceof MobileInput input){
            if(oldMovement == null){
                oldMovement = input.movement;
            }

            if(!observing){
                input.movement = movement;
            }else{
                input.movement = oldMovement;
            }

            observing = !observing;
        }
    }

    public static boolean isObserving(){
        return observing;
    }
}
