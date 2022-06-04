package MinerTools;

import MinerTools.ui.dialogs.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.distribution.Conveyor.*;
import mindustry.world.blocks.distribution.ItemBridge.*;
import mindustry.world.blocks.distribution.Junction.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;

import static MinerTools.MinerVars.*;
import static arc.Core.*;
import static mindustry.Vars.*;

public class MinerFuncs{
    public static ObjectSet<Building> updatedBuildings = new ObjectSet<>();

    public static Item lastDropItem;
    private static final Seq<Class<?>> blackDropBuild = Seq.with(StorageBlock.class, ItemBridge.class, Autotiler.class, MassDriver.class, NuclearReactor.class);
    private static final Seq<DropBuilding> dropBuildings = new Seq<>();

    public static int countMiner(Team team){
        return team.data().units.count(unit -> unit.controller() instanceof MinerAI);
    }

    public static int countPlayer(Team team){
        return Groups.player.count(player -> player.team() == team);
    }

    public static void tryUpdateConveyor(){
        Vec2 pos = input.mouseWorld(input.mouseX(), input.mouseY());
        Building target = world.build(World.toTile(pos.x), World.toTile(pos.y));
        if(control.input.block instanceof Autotiler && target != null){
            updatedBuildings.clear();
            tryUpdateConveyor(target, control.input.block);
            updatedBuildings.clear();
        }
    }

    public static void tryUpdateConveyor(Building start, Block type){
        /* StackOverflowError */
        if(!updatedBuildings.add(start)) return;

        if(start.block != type){
            player.unit().addBuild(new BuildPlan(start.tileX(), start.tileY(), start.rotation, type));
        }

        if(start instanceof ChainedBuilding chainedBuild && chainedBuild.next() != null){
            tryUpdateConveyor(chainedBuild.next(), type);
        }else if(start instanceof ConveyorBuild build && build.next != null && build.next.team == build.team){
            if(build.next instanceof JunctionBuild junction){
                Building building = junction.nearby(build.rotation);

                if(building != null) tryUpdateConveyor(building, type);
            }else if(build.next instanceof ItemBridgeBuild bridge){
                Tile otherTile = world.tile(bridge.link);
                ItemBridge block = (ItemBridge)bridge.block;

                if(block.linkValid(bridge.tile, otherTile)){
                    ItemBridgeBuild other = (ItemBridgeBuild)otherTile.build;

                    for(Building building : other.proximity){
                        tryUpdateConveyor(building, type);
                    }
                }
            }
        }
    }

    public static void tryPanToController(){
        Unit unit = Units.closestOverlap(player.team(), input.mouseWorldX(), input.mouseWorldY(), 5f, u -> !u.isLocal());
        if(unit != null && unit.controller() instanceof LogicAI ai && ai.controller != null){
            ((DesktopInput)control.input).panning = true;
            camera.position.set(ai.controller);
            Fx.spawn.at(ai.controller);
        }
    }

    public static void showBannedInfo(){
        Table t = new Table(Styles.black3);
        t.touchable = Touchable.disabled;

        Seq<UnitType> units = state.rules.bannedUnits.asArray();
        if(!units.isEmpty()){
            Seq<UnitType> seq;

            if(units.size < visibleUnits.size / 2){
                t.add("[red]Banned [accent]Units:[] ").style(Styles.outlineLabel).labelAlign(Align.left);
                seq = units;
            }else{
                t.add("[green]UnBanned [accent]Units:[] ").style(Styles.outlineLabel).labelAlign(Align.left);
                seq = visibleUnits.select(u -> !units.contains(u));
            }

            for(UnlockableContent c : seq){
                t.image(c.uiIcon).size(iconSmall).left().padLeft(3f);
            }
            t.row();
        }

        Seq<Block> blocks = state.rules.bannedBlocks.asArray();
        if(!blocks.isEmpty()){
            Seq<Block> seq;

            if(blocks.size < visibleBlocks.size / 2){
                t.add("[red]Banned [accent]Blocks:[] ").style(Styles.outlineLabel).labelAlign(Align.left);
                seq = blocks;
            }else{
                t.add("[green]UnBanned [accent]Blocks:[] ").style(Styles.outlineLabel).labelAlign(Align.left);
                seq = visibleBlocks.select(b -> !blocks.contains(b));
            }

            for(UnlockableContent c : seq){
                t.image(c.uiIcon).size(iconSmall).left().padLeft(3f);
            }
            t.row();
        }

        t.margin(8f).update(() -> t.setPosition(graphics.getWidth()/2f, graphics.getHeight()/2f, Align.center));
        t.actions(Actions.fadeOut(8.5f, Interp.pow4In), Actions.remove());
        t.pack();
        t.act(0.1f);
        scene.add(t);
    }

    public static void rebuildBlocks(){
        if(!player.unit().canBuild()) return;

        int i = 0;
        for(BlockPlan block : player.team().data().blocks){
            if(Mathf.len(block.x - player.tileX(), block.y - player.tileY()) >= buildingRange) continue;
            if(++i > 511) break;
            player.unit().addBuild(new BuildPlan(block.x, block.y, block.rotation, content.block(block.block), block.config));
        }
    }

    public static void dropItems(){
        dropBuildings.clear();

        CoreBuild core = player.closestCore();
        if(core == null && !player.unit().hasItem()) return;

        boolean autoDrop = player.dst(core) <= itemTransferRange && core != null && core.items != null;

        indexer.eachBlock(player.team(), player.x, player.y, itemTransferRange,
        building -> !blackDropBuild.contains(clazz -> clazz.isAssignableFrom(building.block.getClass())), building -> {
            DropBuilding db = new DropBuilding(building, autoDrop);
            if(db.any()){
                dropBuildings.add(db);
            }
        });

        if(dropBuildings.isEmpty()) return;

        dropBuildings.sort(Structs.comps(Structs.comparing(db -> db.status.ordinal()), Structs.comparingBool(db -> db.building.block instanceof GenericCrafter)));

        for(DropBuilding db : dropBuildings){
            if(DropBuilding.drop(db)) break;
        }
    }

    private static void tryDropItem(Item item, int amount){
        for(DropBuilding db : dropBuildings){
            Building build = db.building;
            if(build.acceptStack(item, amount, player.unit()) > 0){
                Call.transferInventory(player, build);

                if(!player.unit().hasItem()) break;
                dropBuildings.remove(db);
            }
        }

        lastDropItem = item;
    }

    private static void requestItem(Item item){
        CoreBuild core = player.closestCore();

        /* 玩家有物品, 需要扔回核心 */
        if(player.unit().hasItem() && player.unit().item() != item){
            // 核心是否接受
            if(core.acceptStack(player.unit().item(), player.unit().stack.amount, player.unit()) > 0){
                Call.transferInventory(player, core);
            }else{
                Call.dropItem(0);
            }
        }

        /* 拿物品 */
        if(!player.unit().hasItem()){
            int coreAmount = core.items.get(item);
            if(coreAmount > 0){
                int getAmount = Math.min(coreAmount, player.unit().maxAccepted(item));
                Call.requestItem(player, core, item, getAmount);
            }
        }
    }

    public static class DropBuilding{
        public DropStatus status;

        public Building building;
        public ItemStack[] consumeItems;

        public DropBuilding(Building building, boolean autoDrop){
            this.building = building;
            consumeItems = getConsItemStack(building);

            if(player.unit().hasItem() && building.acceptStack(player.unit().item(), player.unit().stack.amount, player.unit()) > 0){
                status = DropStatus.PLAYER;
            }else if(lastDropItem != null && !player.unit().hasItem() && building.acceptItem(building, lastDropItem)){
                status = DropStatus.LAST;
            }else if(autoDrop && consumeItems != null){
                status = DropStatus.AUTO;
            }
        }

        public boolean any(){
            return status != null;
        }

        public Item getConsItem(){
            if(consumeItems == null){
                return null;
            }

            CoreBuild core = player.closestCore();

            if(building.block instanceof ItemTurret block){
                Seq<Item> items = DropSettingDialog.get(block.name);

                if(items == null) return null;

                for(Item oldItem : items){
                    Item item = content.item(oldItem.id);
                    if(building.acceptItem(building, item) && core.items.has(item)){
                        return item;
                    }
                }
            }

            for(ItemStack stack : consumeItems){
                Item consItem = stack.item;
                int maxAmount = building.getMaximumAccepted(consItem);
                boolean buildHasItem = building.items.has(consItem), chasItem = core.items.has(consItem);

                /* 必要前提 核心有物品*/
                /* 可选的情况:
                 1.建筑没有物品
                 2.建筑有物品但是不够
                 */
                if(chasItem && (!buildHasItem || building.items.get(consItem) < maxAmount)){
                    return consItem;
                }
            }

            return null;
        }

        public static ItemStack[] getConsItemStack(Building building){
            if(building.block instanceof ItemTurret) return ItemStack.empty; // Not null

            if(building.block instanceof UnitFactory block){
                UnitFactoryBuild factoryBuild = (UnitFactoryBuild)building;
                int currentPlan = factoryBuild.currentPlan;
                if(currentPlan == -1) return null;
                return block.plans.get(currentPlan).requirements;
            }else{
                Consume consume = building.block.consumes.getItem();

                if(consume instanceof ConsumeItems consumeItems){
                    return consumeItems.items;
                }
            }

            return null;
        }

        public static boolean drop(DropBuilding dropBuilding){
            switch(dropBuilding.status){
                case PLAYER -> {
                    tryDropItem(player.unit().item(), player.unit().stack.amount);
                    return true;
                }
                case LAST -> {
                    requestItem(lastDropItem);
                    tryDropItem(lastDropItem, player.unit().stack.amount);
                    return true;
                }
                case AUTO -> {
                    Item dropItem = dropBuilding.getConsItem();
                    if(dropItem == null) return false;

                    requestItem(dropItem);
                    tryDropItem(dropItem, player.unit().stack.amount);
                    return true;
                }
            }
            return false;
        }

        public enum DropStatus{
            PLAYER, LAST, AUTO
        }
    }
}
