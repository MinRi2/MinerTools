package MinerTools.ai;

import mindustry.entities.units.*;

public class BaseAI extends AIController{
    public static BaseAI controller;

    public void requireUpdate(){
        controller = this;
    }

    protected void update(){}

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
