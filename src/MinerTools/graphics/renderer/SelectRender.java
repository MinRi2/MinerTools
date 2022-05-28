package MinerTools.graphics.renderer;

import MinerTools.graphics.draw.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;

import static arc.Core.input;

public class SelectRender extends BaseRender<BaseDrawer<?>>{
    private final Seq<BuildDrawer<?>> allBuildDrawers = new Seq<>();
    private final Seq<UnitDrawer> allUnitDrawers = new Seq<>();

    Seq<BuildDrawer<?>> enableBuildDrawers;
    Seq<UnitDrawer> enableUnitDrawers;

    public SelectRender addBuildDrawers(BuildDrawer<?>... drawers){
        allBuildDrawers.addAll(drawers);
        return this;
    }

    public SelectRender addUnitDrawers(UnitDrawer... drawers){
        allUnitDrawers.addAll(drawers);
        return this;
    }

    @Override
    public void updateEnable(){
        enableBuildDrawers = allBuildDrawers.select(BaseDrawer::enabled);
        enableUnitDrawers = allUnitDrawers.select(BaseDrawer::enabled);
    }

    @Override
    public void updateSetting(){
        for(var drawer : allBuildDrawers){
            drawer.readSetting();
        }
        for(var drawer : allUnitDrawers){
            drawer.readSetting();
        }
    }

    @Override
    public void render(){
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

    @Override
    public void globalRender(Seq<BaseDrawer<?>> validDrawers){}

    @Override
    public void cameraRender(Seq<BaseDrawer<?>> validDrawers){}

}
