package MinerTools.ai.types;

import MinerTools.ai.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ai.types.*;
import mindustry.type.*;

import static mindustry.content.UnitTypes.*;

public class PlayerMinerAI extends PlayerAI{
    public static Seq<Item> allOres = Item.getAllOres();

    public PlayerMinerAI(){
        super(new MinerAIX(), new TextureRegionDrawable(mono.uiIcon));
    }

    @Override
    public void display(Table table){
    }

    public static class MinerAIX extends MinerAI{

    }
}
