package MinerTools.content;

import MinerTools.override.*;
import arc.struct.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitFactory.*;

import static mindustry.Vars.content;

public class Contents{
    public static Seq<UnitType> visibleUnits = new Seq<>();
    public static Seq<Block> visibleBlocks = new Seq<>();
    public static Seq<Item> allOres = new Seq<>();

    public static Seq<Seq<UnitType>> linkedUnits = new Seq<>();

    public static void init(){
        initBlocks();
        initUnits();
    }

    private static void initBlocks(){
        visibleBlocks.clear();
        allOres.clear();

        linkedUnits.clear();

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

            if(block instanceof UnitFactory factory){
                for(UnitPlan plan : factory.plans){
                    linkedUnits.add(Seq.with(plan.unit));
                }
            }

            if(block instanceof Reconstructor reconstructor){
                for(UnitType[] upgrade : reconstructor.upgrades){
                    UnitType from = upgrade[0];
                    UnitType to = upgrade[1];

                    if(from == null || to == null) continue;

                    Seq<UnitType> link = linkedUnits.find(seq -> seq.contains(from));

                    link.add(to);
                }
            }

            Bars.override(block);
        }

        allOres.sort(item -> item.id);
    }

    private static void initUnits(){
        visibleUnits.clear();

        for(UnitType type : content.units()){
            if(!type.isHidden()){
                visibleUnits.add(type);
            }
        }
    }

}
