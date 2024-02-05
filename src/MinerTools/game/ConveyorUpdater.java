package MinerTools.game;

import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.distribution.Conveyor.*;
import mindustry.world.blocks.distribution.Duct.*;
import mindustry.world.blocks.distribution.DuctBridge.*;
import mindustry.world.blocks.distribution.DuctRouter.*;
import mindustry.world.blocks.distribution.ItemBridge.*;
import mindustry.world.blocks.distribution.Junction.*;
import mindustry.world.blocks.distribution.OverflowDuct.*;
import mindustry.world.blocks.distribution.OverflowGate.*;
import mindustry.world.blocks.distribution.Router.*;
import mindustry.world.blocks.distribution.Sorter.*;
import mindustry.world.blocks.distribution.StackConveyor.*;
import mindustry.world.blocks.distribution.StackRouter.*;

import static arc.Core.input;

public class ConveyorUpdater{
    public static ObjectSet<Building> updatedBuildings = new ObjectSet<>();
    public static boolean enable;
    public static Vec2 lastPos = new Vec2();

    public static boolean toggle(){
        return enable = !enable;
    }

    public static void tryUpdateConveyor(){
        Vec2 pos = input.mouseWorld(input.mouseX(), input.mouseY());

        if(pos.equals(lastPos)) return;

        lastPos.set(pos);

        Building target = Vars.world.build(World.toTile(pos.x), World.toTile(pos.y));

        if(target == null) return;

        Block type = Vars.control.input.block;
        if(type != null && (type instanceof Conveyor || type instanceof StackConveyor || type instanceof Duct || type instanceof Junction) && target.block.size == type.size){
            tryUpdateConveyor(target, type, target.rotation);
            updatedBuildings.clear();
        }
    }

    public static void tryUpdateConveyor(Building start, Block type, int rotation){
        /* StackOverflowError */
        if(!updatedBuildings.add(start)) return;

        Building build = null;
        if(start instanceof ConveyorBuild || start instanceof DuctBuild || start instanceof StackConveyorBuild){
            rotation = start.rotation;
            build = start.nearby(rotation);
            addPlan(start, type, rotation);
        }else if(start instanceof JunctionBuild junction){
            if(type instanceof StackConveyor){
                addPlan(start, type, rotation);
            }

            build = junction.nearby(rotation);
        }else if(start instanceof RouterBuild || start instanceof DuctRouterBuild || start instanceof SorterBuild || start instanceof OverflowDuctBuild || start instanceof OverflowGateBuild){
            if(type instanceof StackConveyor && !(start instanceof StackRouterBuild)){
                addPlan(start, type, rotation);
            }

            for(Building building : start.proximity){
                tryUpdateConveyor(building, type, start.relativeTo(building));
            }

            return;
        }else if(start instanceof DuctBridgeBuild duct){
            DuctBridgeBuild other = (DuctBridgeBuild)duct.findLink();

            if(other != null){
                tryUpdateConveyor(other, type, start.rotation);
            }else{
                for(Building building : start.proximity){
                    tryUpdateConveyor(building, type, start.relativeTo(building));
                }
            }

            return;
        }else if(start instanceof ItemBridgeBuild bridge){
            Tile otherTile = Vars.world.tile(bridge.link);
            ItemBridge block = (ItemBridge)bridge.block;

            if(block.linkValid(bridge.tile, otherTile)){
                ItemBridgeBuild other = (ItemBridgeBuild)otherTile.build;
                tryUpdateConveyor(other, type, start.rotation);
            }else{
                for(Building building : start.proximity){
                    tryUpdateConveyor(building, type, start.relativeTo(building));
                }
            }

            return;
        }

        if(build == null || build.team != start.team){
            return;
        }

        tryUpdateConveyor(build, type, rotation);
    }

    private static void addPlan(Building build, Block type, int rotation){
        Vars.player.unit().addBuild(new BuildPlan(build.tileX(), build.tileY(), rotation, type));
    }
}
