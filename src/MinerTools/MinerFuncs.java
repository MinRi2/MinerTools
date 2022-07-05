package MinerTools;

import arc.math.*;
import arc.math.geom.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.core.*;
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
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.distribution.Conveyor.*;
import mindustry.world.blocks.distribution.ItemBridge.*;
import mindustry.world.blocks.distribution.Junction.*;

import static MinerTools.content.Contents.*;
import static arc.Core.*;

public class MinerFuncs{
    public static ObjectSet<Building> updatedBuildings = new ObjectSet<>();

    public static boolean enableUpdateConveyor;

    private static final ObjectMap<Category, Seq<Block>> catBlockMap = new ObjectMap<>();

    public static int countMiner(Team team){
        return team.data().units.count(unit -> unit.controller() instanceof MinerAI);
    }

    public static int countPlayer(Team team){
        return Groups.player.count(player -> player.team() == team);
    }

    public static void tryUpdateConveyor(){
        Vec2 pos = input.mouseWorld(input.mouseX(), input.mouseY());
        Building target = Vars.world.build(World.toTile(pos.x), World.toTile(pos.y));
        if(Vars.control.input.block instanceof Autotiler && target != null){
            updatedBuildings.clear();
            tryUpdateConveyor(target, Vars.control.input.block);
            updatedBuildings.clear();
        }
    }

    public static void tryUpdateConveyor(Building start, Block type){
        /* StackOverflowError */
        if(!updatedBuildings.add(start)) return;

        if(start.block != type){
            Vars.player.unit().addBuild(new BuildPlan(start.tileX(), start.tileY(), start.rotation, type));
        }

        if(start instanceof ChainedBuilding chainedBuild && chainedBuild.next() != null){
            tryUpdateConveyor(chainedBuild.next(), type);
        }else if(start instanceof ConveyorBuild build && build.next != null && build.next.team == build.team){
            if(build.next instanceof JunctionBuild junction){
                Building building = junction.nearby(build.rotation);

                if(building != null) tryUpdateConveyor(building, type);
            }else if(build.next instanceof ItemBridgeBuild bridge){
                Tile otherTile = Vars.world.tile(bridge.link);
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
        Unit unit = Units.closestOverlap(Vars.player.team(), input.mouseWorldX(), input.mouseWorldY(), 5f, u -> !u.isLocal());
        if(unit != null && unit.controller() instanceof LogicAI ai && ai.controller != null){
            ((DesktopInput)Vars.control.input).panning = true;
            camera.position.set(ai.controller);
            Fx.spawn.at(ai.controller);
        }
    }

    public static void showBannedInfo(){
        Table t = new Table(Tex.pane);
        t.touchable = Touchable.disabled;

        var units = Vars.state.rules.bannedUnits.toSeq();
        if(units.any()){
            t.table(unitTable -> {
                var seq = units;
                String fixed = "[red]Banned";

                if(units.size > visibleUnits.size / 2){
                    seq = visibleUnits.select(u -> !units.contains(u));
                    fixed = "[green]UnBanned";
                }

                unitTable.add(fixed + "[accent]Units:[] ").top()
                 .style(Styles.outlineLabel).labelAlign(Align.left);

                unitTable.row();

                var finalSeq = seq;
                unitTable.table(infoTable -> {
                    for(var linkedUnit : linkedUnits){
                        for(UnitType type : linkedUnit){
                            if(finalSeq.contains(type)){
                                infoTable.image(type.uiIcon).size(Vars.iconSmall).left().padLeft(3f);
                            }
                        }

                        infoTable.row();
                    }
                }).left();
            }).left().row();
        }

        var blocks = Vars.state.rules.bannedBlocks.toSeq();
        if(blocks.any()){
            t.table(blockTable -> {
                var seq = blocks;
                String fixed = "[red]Banned";

                if(units.size > visibleUnits.size / 2){
                    seq = visibleBlocks.select(b -> !blocks.contains(b));
                    fixed = "[green]UnBanned";
                }

                blockTable.add(fixed + "[accent]Blocks:[] ").top()
                 .style(Styles.outlineLabel).labelAlign(Align.left);

                blockTable.row();

                catBlockMap.clear();

                for(Block block : seq){
                    catBlockMap.get(block.category, Seq::new).add(block);
                }

                blockTable.table(infoTable -> {
                    for(var entry : catBlockMap){
                        Category cat = entry.key;
                        var catBlocks = entry.value;

                        infoTable.image(Vars.ui.getIcon(cat.name())).left().padLeft(8f);

                        for(Block block : catBlocks){
                            infoTable.image(block.uiIcon).size(Vars.iconSmall).left().padLeft(3f);
                        }

                        infoTable.row();
                    }
                }).left();
            }).left().row();
        }

        t.margin(8f).update(() -> t.setPosition(graphics.getWidth()/2f, graphics.getHeight()/2f, Align.center));
        t.actions(Actions.fadeOut(8.5f, Interp.pow5In), Actions.remove());
        t.pack();
        t.act(0.1f);
        scene.add(t);
    }

    public static void rebuildBlocks(){
        if(!Vars.player.unit().canBuild()) return;

        int i = 0;
        for(BlockPlan block : Vars.player.team().data().plans){
            if(Mathf.len(block.x - Vars.player.tileX(), block.y - Vars.player.tileY()) >= Vars.buildingRange) continue;
            if(++i > 511) break;
            Vars.player.unit().addBuild(new BuildPlan(block.x, block.y, block.rotation, Vars.content.block(block.block), block.config));
        }
    }

}
