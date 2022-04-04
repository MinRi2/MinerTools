package MinerTools.ai;

import MinerTools.interfaces.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;

import static mindustry.Vars.player;

public class PlayerAI extends BaseAI implements Displayable{
    /** 用于{@link MinerTools.ui.tables.members.AITable} 标识AI种类, 不用时可为null */
    public Drawable icon;

    public PlayerAI(Drawable icon){
        this.icon = icon;
    }

    /* Needn't update weapons */
    @Override
    public void updateWeapons(){}


    /* Needn't update target */
    @Override
    public void updateTargeting(){}

    protected void update(){
        if(unit == null || unit != player.unit()){
            unit(player.unit());
        }
    }

    @Override
    public void display(Table table){}
}
