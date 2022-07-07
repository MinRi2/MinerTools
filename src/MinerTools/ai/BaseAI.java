package MinerTools.ai;

import mindustry.*;
import mindustry.entities.units.*;
import mindustry.input.*;

public class BaseAI extends AIController{
    public static BaseAI controller;

    public void requireUpdate(){
        controller = this;
    }

    protected void update(){
        if(Vars.control.input instanceof MobileInput input){
            input.movement.setZero();
        }
    }

    public static void resetController(){
        controller = null;
    }

    public static void updateController(){
        if(controller != null){
            controller.update();

            if(controller.unit() != null){
                controller.updateUnit();
            }
        }
    }
}
