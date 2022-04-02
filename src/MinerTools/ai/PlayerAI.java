package MinerTools.ai;

import MinerTools.interfaces.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.entities.units.*;

public class PlayerAI extends BaseAI implements Displayable{
    /** 用于{@link MinerTools.ui.tables.members.AITable} 标识AI种类, 不用时可为null */
    public Drawable icon;

    public PlayerAI(AIController fallback, Drawable icon){
        this.fallback = fallback;
        this.icon = icon;
    }

    protected void update(){
        if(unit == null || unit != Vars.player.unit()){
            unit(Vars.player.unit());
        }
    }

    @Override
    public boolean useFallback(){
        return true;
    }

    @Override
    public void display(Table table){}
}
