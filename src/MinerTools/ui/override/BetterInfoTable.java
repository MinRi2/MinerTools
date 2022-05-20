package MinerTools.ui.override;

import MinerTools.*;
import MinerTools.interfaces.*;
import MinerTools.ui.utils.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.Displayable;
import mindustry.ui.*;
import mindustry.ui.fragments.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.Reconstructor.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.modules.*;

import java.lang.reflect.*;

public class BetterInfoTable extends Table implements OverrideUI{
    private static final Field topTableField, hoverField, wasHoveredField, menuHoverBlockField, nextFlowBuildField;

    private static final Seq<BuildBuilder<? extends Block, ? extends Building>> buildBuilders = Seq.with(
        new BuildBuilder<>(block -> block.hasItems){
            @Override
            public boolean canBuild(Building build){
                return build.items != null && build.items.any();
            }

            @Override
            protected void build(Table table, Building build){
                ItemModule items = build.items;

                table.table(Tex.pane, t -> {
                    t.table(Tex.whiteui, tt -> tt.add("Items")).color(Color.gray).growX().row();

                    t.table(itemsTable -> {
                        final int[] index = {0};
                        items.each(((item, amount) -> {
                            itemsTable.table(itemTable -> {
                                itemTable.image(item.uiIcon);
                                itemTable.label(() -> UI.formatAmount(items.get(item)) + "").padLeft(3f);
                            }).growX().padLeft(4f);

                            if(++index[0] % 2 == 0) itemsTable.row();
                        }));
                    }).growX();
                }).growX();
            }
        }
    );

    private static final Seq<UnitBuilder> unitBuilders = Seq.with(
        new UnitBuilder(){
            @Override
            public boolean canBuild(Unit unit){
                return unit.type.hasWeapons() && !unit.disarmed;
            }

            @Override
            protected void build(Table table, Unit unit){
                table.table(Tex.pane, t -> {
                    t.table(Tex.whiteui, tt -> tt.add("Weapons")).color(Color.gray).growX().row();

                    float iconSize = Vars.mobile ? Vars.iconMed : Vars.iconXLarge;

                    t.table(weaponsTable -> {
                        int index = 0;
                        for(WeaponMount mount : unit.mounts()){
                            Weapon weapon = mount.weapon;

                            Label label = new Label(() -> String.format("%.1f", mount.reload / weapon.reload / 60 * 100) + "s");

                            label.setAlignment(Align.bottom);

                            weaponsTable.table(Tex.pane, weaponTable -> {
                                weaponTable.stack(new Image(weapon.region), label).minSize(iconSize).maxWidth(100f).row();
                                weaponTable.add(new Bar("", Pal.ammo, () -> mount.reload / weapon.reload)).minSize(45f, 18f);
                            }).bottom().growX();

                            if(++index % 3 == 0) weaponsTable.row();
                        }
                    }).growX();
                }).growX();
            }
        }
    );

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

        update(() -> {
            hover = blockFrag.hover();
            boolean wasHovered = MinerUtils.getValue(wasHoveredField, blockFrag);

            if(wasHovered && hover != lastHover){
                if(hover instanceof Teamc teamc){
                    rebuild(teamc.team() == Vars.player.team());
                }else{
                    clearChildren();
                }
            }

            lastHover = hover;
        });
    }

    private void rebuild(boolean same){
        clearChildren();

        if(hover instanceof Building building){
            if(!same){
                building.displayBars(this);
                building.displayConsumption(this);
            }

            var builders = buildBuilders.select(buildBuilder -> buildBuilder.canBuild(building));
            if(builders.any()){
                for(var builder : builders){
                    builder.tryBuild(row(), building);
                    row();
                }
            }
        }

        if(hover instanceof Unit unit){
            var builders = unitBuilders.select(unitBuilder -> unitBuilder.canBuild(unit));
            if(builders.any()){
                for(var builder : builders){
                    builder.tryBuild(row(), unit);
                }
            }
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

        return Vars.control.input.block != null || MinerUtils.getValue(menuHoverBlockField, blockFrag) != null || hover != null;
    }

    @Nullable
    Displayable hovered(){
        Vec2 v = topTable.stageToLocalCoordinates(Core.input.mouse());

        //if the mouse intersects the table or the UI has the mouse, no hovering can occur
        if(Core.scene.hasMouse() || topTable.hit(v.x, v.y, false) != null) return null;

        //check for a unit
        Unit unit = Units.closestOverlap(null, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 5f, Entityc::isAdded);
        //if cursor has a unit, display it
        if(unit != null) return unit;

        //check tile being hovered over
        Tile hoverTile = Vars.world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);
        if(hoverTile != null){
            //if the tile has a building, display it
            if(hoverTile.build != null && hoverTile.build.displayable() && !hoverTile.build.inFogTo(Vars.player.team())){
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
        for(Block block : Vars.content.blocks()){
            block.addBar("health", e -> new Bar(
                () -> String.format("%.2f", e.health) + "/" + e.maxHealth + "(" + (int)(100 * e.healthf()) + "%" + ")",
                () -> Pal.health, e::healthf).blink(Color.white));

            if(block instanceof UnitFactory factory){
                factory.addBar("progress", (UnitFactoryBuild e) -> new Bar(
                () -> Core.bundle.get("bar.progress") + "(" + 100 * (int)(e.fraction()) + "%" + ")",
                () -> Pal.ammo, e::fraction));
            }

            if(block instanceof Reconstructor reconstructor){
                reconstructor.addBar("progress", (ReconstructorBuild e) -> new Bar(
                () -> Core.bundle.get("bar.progress") + "(" + 100 * (int)(e.fraction()) + "%" + ")",
                () -> Pal.ammo, e::fraction));
            }
        }
    }

    static abstract class BaseBarBuilder<T extends Entityc>{
        public abstract boolean canBuild(T entity);

        public abstract void tryBuild(Table table, T entity);

        protected abstract void build(Table table, T entity);
    }

    static abstract class BuildBuilder<KT extends Block, DT extends Building> extends BaseBarBuilder<DT>{
        private final Class<KT> blockClass;

        private final Seq<Block> blocks;

        public BuildBuilder(){
            var clazz = this.getClass();

            ParameterizedType type = (ParameterizedType)clazz.getGenericSuperclass();
            Type[] types = type.getActualTypeArguments();

            blockClass = (Class<KT>)types[0];

            blocks = MinerVars.visibleBlocks.select(block -> blockClass.isAssignableFrom(block.getClass()));
        }

        public BuildBuilder(Boolf<Block> filter){
            blockClass = null;
            blocks = MinerVars.visibleBlocks.select(filter);
        }

        @Override
        public boolean canBuild(Building build){
            return blocks.contains(build.block);
        }

        @Override
        public void tryBuild(Table table, Building build){
            build(table, (DT)build);
        }
    }

    static abstract class UnitBuilder extends BaseBarBuilder<Unit>{
        @Override
        public boolean canBuild(Unit unit){
            return true;
        }

        @Override
        public void tryBuild(Table table, Unit unit){
            build(table, unit);
        }
    }
}
