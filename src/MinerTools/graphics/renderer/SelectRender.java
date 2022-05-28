package MinerTools.graphics.renderer;

import MinerTools.graphics.draw.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;

import static arc.Core.input;

public class SelectRender extends BaseRender<BaseDrawer<?>>{
    private final Seq<BuildDrawer<?>> buildDrawers = new Seq<>();
    private final Seq<UnitDrawer> unitDrawers = new Seq<>();

    public SelectRender addBuildDrawers(BuildDrawer<?>... drawers){
        buildDrawers.addAll(drawers);
        return this;
    }

    public SelectRender addUnitDrawers(UnitDrawer... drawers){
        unitDrawers.addAll(drawers);
        return this;
    }

    @Override
    public void render(){
        Vec2 v = input.mouseWorld();

        Building selectBuild = Vars.world.buildWorld(v.x, v.y);
        if(selectBuild != null){
            for(BuildDrawer<?> drawer : buildDrawers){
                drawer.tryDraw(selectBuild);
            }
        }

        Unit selectUnit = Units.closestOverlap(null, v.x, v.y, 5f, Entityc::isAdded);
        if(selectUnit != null){
            for(UnitDrawer drawer : unitDrawers){
                drawer.tryDraw(selectUnit);
            }
        }
    }

    @Override
    public void globalRender(Seq<BaseDrawer<?>> validDrawers){}

    @Override
    public void cameraRender(Seq<BaseDrawer<?>> validDrawers){}

}
