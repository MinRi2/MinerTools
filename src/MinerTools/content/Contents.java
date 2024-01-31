package MinerTools.content;

import MinerTools.content.override.*;
import MinerTools.content.override.stats.*;
import arc.struct.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;

import static mindustry.Vars.content;

public class Contents{
    public static Seq<UnitType> visibleUnits = new Seq<>();
    public static Seq<Block> visibleBlocks = new Seq<>();
    public static Seq<Item> allOres = new Seq<>();

    public static void init(){
        initBlocks();
        initUnits();
    }

    private static void initBlocks(){
        visibleBlocks.clear();
        allOres.clear();

        for(Block block : content.blocks()){
            if(block.buildVisibility.visible()){
                visibleBlocks.add(block);
            }

            if(block.itemDrop != null && !allOres.contains(block.itemDrop)){
                allOres.add(block.itemDrop);
            }

            if(block instanceof ItemBridge){
                block.allowConfigInventory = true;
            }

            Bars.override(block);
            MStats.block.override(block);
        }

        allOres.sort(item -> item.id);
    }

    private static void initUnits(){
        visibleUnits.clear();

        for(UnitType type : content.units()){
            if(!type.isHidden()){
                visibleUnits.add(type);
            }

            MStats.unit.override(type);
        }
    }

}
