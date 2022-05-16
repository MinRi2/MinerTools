package MinerTools.ui.override;

import MinerTools.*;
import MinerTools.interfaces.*;
import MinerTools.ui.utils.*;
import arc.*;
import arc.graphics.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.Displayable;
import mindustry.ui.*;
import mindustry.ui.fragments.*;
import mindustry.world.*;

import java.lang.reflect.*;

import static mindustry.Vars.*;

public class BetterInfoTable extends Table implements OverrideUI{
    private static final Field topTableField, hoverField, wasHoveredField, menuHoverBlockField, nextFlowBuildField;

    static{
        topTableField = MinerUtils.getField(PlacementFragment.class, "topTable");
        hoverField = MinerUtils.getField(PlacementFragment.class, "hover");
        wasHoveredField = MinerUtils.getField(PlacementFragment.class, "wasHovered");
        menuHoverBlockField = MinerUtils.getField(PlacementFragment.class, "menuHoverBlock");
        nextFlowBuildField = MinerUtils.getField(PlacementFragment.class, "nextFlowBuild");

        /* This class always load after content init*/
        addBars();
    }

    private PlacementFragment blockFrag;

    private Table topTable;
    private Displayable hover, lastHover;

    public BetterInfoTable(){
        /* PlacementFragment rebuild event */
        Events.on(WorldLoadEvent.class, event -> Core.app.post(this::tryDoOverride));

        Events.on(UnlockEvent.class, event -> {
            if(event.content instanceof Block){
                tryDoOverride();
            }
        });

        setup();
    }

    private void setup(){
        blockFrag = Vars.ui.hudfrag.blockfrag;

        defaults().minHeight(20f).pad(4);
        margin(5f);

        update(() -> {
            hover = blockFrag.hover();
            boolean wasHovered = MinerUtils.getValue(wasHoveredField, blockFrag);

            if(wasHovered && hover != lastHover && hover instanceof Teamc teamc){
                if(teamc.team() != Vars.player.team()){
                    rebuild();
                }else{
                    clearChildren();
                }
            }

            lastHover = hover;
        });
    }

    private void rebuild(){
        clearChildren();

        if(hover instanceof Building building){
            building.displayBars(this);
            building.displayConsumption(this);
        }
    }

    private void initOverride(){
        topTable = MinerUtils.getValue(topTableField, Vars.ui.hudfrag.blockfrag);
    }

    private void tryDoOverride(){
        initOverride();

        doOverride();
    }

    @Override
    public void doOverride(){
        topTable.row();

        topTable.add(this).growX();

        Cell<?> cell = ElementUtils.getCell(topTable);
        if(cell != null){
            cell.visible(this::hasInfoBox);
        }
    }

    @Override
    public void resetOverride(){
        remove();
    }

    boolean hasInfoBox(){
        Displayable hover = hovered();

        MinerUtils.setValue(hoverField, blockFrag, hover);

        return control.input.block != null || MinerUtils.getValue(menuHoverBlockField, blockFrag) != null || hover != null;
    }

    @Nullable
    Displayable hovered(){
        Vec2 v = topTable.stageToLocalCoordinates(Core.input.mouse());

        //if the mouse intersects the table or the UI has the mouse, no hovering can occur
        if(Core.scene.hasMouse() || topTable.hit(v.x, v.y, false) != null) return null;

        //check for a unit
        Unit unit = Units.closestOverlap(null, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 5f, u -> !u.isLocal());
        //if cursor has a unit, display it
        if(unit != null) return unit;

        //check tile being hovered over
        Tile hoverTile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);
        if(hoverTile != null){
            //if the tile has a building, display it
            if(hoverTile.build != null && hoverTile.build.displayable() && !hoverTile.build.inFogTo(player.team())){
                return MinerUtils.setValue(nextFlowBuildField, blockFrag, hoverTile.build);
            }

            //if the tile has a drop, display the drop
            if((hoverTile.drop() != null && hoverTile.block() == Blocks.air) || hoverTile.wallDrop() != null || hoverTile.floor().liquidDrop != null){
                return hoverTile;
            }
        }

        return null;
    }

    private static void addBars(){
        for(Block block : content.blocks()){
            block.addBar("health", entity -> new Bar(
                () -> String.format("%.2f", entity.health) + "/" + entity.maxHealth + "(" + 100 * (int)entity.healthf() + "%" + ")",
                () -> Pal.health, entity::healthf).blink(Color.white));
        }
    }
}
