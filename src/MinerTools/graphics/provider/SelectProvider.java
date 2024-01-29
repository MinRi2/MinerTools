package MinerTools.graphics.provider;

import MinerTools.graphics.draw.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;

import static arc.Core.input;

public class SelectProvider extends DrawerProvider<Drawer<?>>{
    private final Seq<BuildDrawer<?>> allBuildDrawers = new Seq<>();
    private final Seq<UnitDrawer> allUnitDrawers = new Seq<>();

    Seq<BuildDrawer<?>> enableBuildDrawers;
    Seq<UnitDrawer> enableUnitDrawers;

    public SelectProvider addBuildDrawers(BuildDrawer<?>... drawers){
        allBuildDrawers.addAll(drawers);
        addDrawers(drawers);
        return this;
    }

    public SelectProvider addUnitDrawers(UnitDrawer... drawers){
        allUnitDrawers.addAll(drawers);
        addDrawers(drawers);
        return this;
    }

    @Override
    public void updateEnable(){
        enableBuildDrawers = allBuildDrawers.select(Drawer::isEnabled);
        enableUnitDrawers = allUnitDrawers.select(Drawer::isEnabled);

        enableDrawers.clear();
        enableDrawers.addAll(enableBuildDrawers).add(enableUnitDrawers);
    }

    @Override
    public void provide(){
        Vec2 v = input.mouseWorld();

        Building selectBuild = Vars.world.buildWorld(v.x, v.y);
        if(selectBuild != null){
            for(BuildDrawer<?> drawer : enableBuildDrawers){
                drawer.tryDraw(selectBuild);
            }
        }

        Unit selectUnit = Units.closestOverlap(null, v.x, v.y, 5f, Entityc::isAdded);
        if(selectUnit != null){
            for(UnitDrawer drawer : enableUnitDrawers){
                drawer.tryDraw(selectUnit);
            }
        }
    }

}
