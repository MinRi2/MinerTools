package MinerTools.ai.types;

import MinerTools.ai.*;
import arc.*;
import arc.graphics.*;
import arc.scene.style.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

import static MinerTools.MinerVars.allOres;
import static mindustry.Vars.*;

public class PlayerMinerAI extends PlayerAI{
    private Seq<Item> mineOres = Seq.with(Items.copper, Items.lead);

    public Item targetItem;
    public Tile ore;
    public boolean mining;

    public PlayerMinerAI(){
        super(new TextureRegionDrawable(UnitTypes.mono.uiIcon));

        /* 适配ContentLoader */
        Events.on(EventType.ContentInitEvent.class, e -> {
            for(int i = 0; i < mineOres.size; i++){
                mineOres.set(i, content.item(i));
            }
        });
    }

    @Override
    public void updateMovement(){
        Building core = unit.closestCore();

        if(!(unit.canMine()) || core == null){
            return;
        }

        if(unit.mineTile != null && !unit.mineTile.within(unit, unit.type.miningRange)){
            unit.mineTile(null);
        }

        if(mining){
            if(timer.get(timerTarget2, 60 * 4) || targetItem == null){
                targetItem = mineOres.min(i -> indexer.hasOre(i) && unit.canMine(i), i -> core.items.get(i));
            }

            //core full of the target item, do nothing
            if(targetItem != null && core.acceptStack(targetItem, 1, unit) == 0){
                Call.dropItem(player.angleTo(player.x, player.y));
                unit.mineTile = null;
                return;
            }

            //if inventory is full, drop it off.
            if(unit.stack.amount >= unit.type.itemCapacity || (targetItem != null && !unit.acceptsItem(targetItem))){
                mining = false;
            }else{
                if(timer.get(timerTarget3, 60) && targetItem != null){
                    ore = indexer.findClosestOre(unit, targetItem);
                }

                if(ore != null){
                    moveTo(ore, unit.type.miningRange / 2f, 20f);

                    if(ore.block() == Blocks.air && unit.within(ore, unit.type.miningRange)){
                        unit.mineTile = ore;
                    }

                    if(ore.block() != Blocks.air){
                        mining = false;
                    }
                }
            }
        }else{
            unit.mineTile = null;

            if(unit.stack.amount == 0){
                mining = true;
                return;
            }

            if(unit.within(core, unit.type.range)){
                if(core.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0){
                    Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, core);
                }

                Call.dropItem(player.angleTo(player.x, player.y));
                mining = true;
            }

            circle(core, unit.type.range / 1.8f);
        }
    }

    @Override
    public void display(Table table){
        ButtonStyle none = new ButtonStyle();

        table.table(oreSetting -> {
            oreSetting.add("Ore Setting: ");
            for(Item ore : allOres){
                oreSetting.button(b -> {
                    b.image(ore.uiIcon).update(i -> i.setColor(mineOres.contains(ore) ? Color.white : Color.gray));
                }, none, () -> {
                    if(!mineOres.contains(ore)){
                        mineOres.add(ore);
                        mineOres.sort(o -> o.id);
                    }else{
                        mineOres.remove(ore);
                    }
                }).fill().get();
            }
        }).right();
    }
}
