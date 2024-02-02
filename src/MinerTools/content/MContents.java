package MinerTools.content;

import MinerTools.content.override.*;
import arc.struct.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;

import static mindustry.Vars.content;

public class MContents{
    public static Seq<Item> allOres = new Seq<>();

    public static void init(){
        initBlocks();
        initUnits();
    }

    private static void initBlocks(){
        allOres.clear();

        for(Block block : content.blocks()){
            if(block.itemDrop != null && !allOres.contains(block.itemDrop)){
                allOres.add(block.itemDrop);
            }

            if(block instanceof ItemBridge){
                block.allowConfigInventory = true;
            }

            MBars.override(block);
        }

        allOres.sort(item -> item.id);
    }

    private static void initUnits(){
//        for(UnitType type : content.units()){
//        }
    }

}
