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
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.modules.*;

import java.lang.reflect.*;

public class BetterInfoTable extends Table implements OverrideUI{
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

                    float iconSize = Vars.mobile ? Vars.iconSmall : Vars.iconXLarge;

                    t.table(weaponsTable -> {
                        int index = 0;
                        for(WeaponMount mount : unit.mounts()){
                            Weapon weapon = mount.weapon;

                            Label label = new Label(() -> String.format("%.1f", mount.reload / weapon.reload / 60 * 100) + "s");

                            label.setAlignment(Align.bottom);

                            weaponsTable.table(Tex.pane, weaponTable -> {
                                weaponTable.stack(new Image(weapon.region), label).minSize(iconSize).maxWidth(80f).row();
                                weaponTable.add(new Bar("", Pal.ammo, () -> mount.reload / weapon.reload)).minSize(45f, 18f);
                            }).bottom().growX();

                            if(++index % 4 == 0) weaponsTable.row();
                        }
                    }).growX();
                }).growX();
            }
        }
    );

    private final BaseInfoTable unitInfo, buildInfo, tileInfo;

    private final Seq<BaseInfoTable> infoTables = Seq.with(
        unitInfo = new BaseInfoTable(){
            private Unit unit;

            @Override
            public boolean shouldRebuild(){
                return unit != null && !unit.inFogTo(Vars.player.team());
            }

            @Override
            public void hovered(){
                unit = Units.closestOverlap(null, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 5f, Entityc::isAdded);
            }

            @Override
            protected void build(){
                unit.display(this);

                var builders = unitBuilders.select(unitBuilder -> unitBuilder.canBuild(unit));
                if(builders.any()){
                    for(var builder : builders){
                        builder.tryBuild(row(), unit);
                    }
                }
            }
        },
        buildInfo = new BaseInfoTable(){
            private Building build;

            @Override
            public boolean shouldRebuild(){
                return build != null && build.displayable() && !build.inFogTo(Vars.player.team());
            }

            @Override
            public void hovered(){
                Tile hoverTile = Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
                if(hoverTile != null){
                    build = hoverTile.build;
                }
            }

            @Override
            protected void build(){
                Team team = build.team;
                if(team != Vars.player.team()){
                    build.team(Vars.player.team());
                    build.display(this);
                    build.team(team);
                }else{
                    build.display(this);
                }

                var builders = buildBuilders.select(buildBuilder -> buildBuilder.canBuild(build));
                if(builders.any()){
                    for(var builder : builders){
                        builder.tryBuild(row(), build);
                    }
                }
            }
        },
        tileInfo = new BaseInfoTable(){
            private Tile tile;

            @Override
            public boolean shouldRebuild(){
                return tile != null;
            }

            @Override
            public void hovered(){
                tile = Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
            }

            @Override
            public void build(){
                displayContent(this, tile.floor());
                if(tile.overlay() != Blocks.air) displayContent(this, tile.overlay());
                if(tile.block().isStatic()){
                    displayContent(this, tile.block());
                }
            }

            private static void displayContent(Table table, UnlockableContent content){
                table.table(t -> {
                    t.image(content.uiIcon).size(Vars.iconMed);
                    t.add(content.localizedName).pad(5);
                }).growX();
            }
        }
    );

    /* For reset override */
    private Table topTable;
    private Stack mainStack;
    private Boolp oldVisible;
    private Cell<?> topTableCell, oldCell;

    public BetterInfoTable(){
        /* PlacementFragment rebuild event */
        Events.on(WorldLoadEvent.class, event -> Core.app.post(this::tryOverride));

        Events.on(UnlockEvent.class, event -> {
            if(event.content instanceof Block){
                tryOverride();
            }
        });

        addSetting();

        setup();
    }

    private void addSetting(){
        MinerVars.ui.settings.ui.addCategory("overrideInfoTable", setting -> {
            setting.checkPref("overrideInfoTable", true, b -> tryToggleOverride());
        });
    }

    private void setup(){
        update(() -> {
            hovered();
            rebuild();
        });
    }

    private void rebuild(){
        clearChildren();

        for(BaseInfoTable table : infoTables){
            if(table.shouldRebuild()){
                add(table.rebuild()).margin(6).growX().row();
            }
        }
    }

    private void hovered(){
        Vec2 v = stageToLocalCoordinates(Core.input.mouse());

        if(Core.scene.hasMouse() || hit(v.x, v.y, false) != null) return;

        for(BaseInfoTable table : infoTables){
            table.hovered();
        }
    }

    public void initOverride(){
        topTable = Reflect.get(Vars.ui.hudfrag.blockfrag, "topTable");
        mainStack = Reflect.get(Vars.ui.hudfrag.blockfrag, "mainStack");
    }

    public void tryToggleOverride(){
        initOverride();
        if(MinerVars.settings.getBool("overrideInfoTable")){
            doOverride();
        }else{
            resetOverride();
        }
    }

    public void tryOverride(){
        if(MinerVars.settings.getBool("overrideInfoTable")){
            initOverride();
            doOverride();
        }
    }

    @Override
    public void doOverride(){
        oldVisible = topTable.visibility;
        topTable.visible(() -> Vars.control.input.block != null);

        Cell<?> cell = ElementUtils.getCell(topTable);
        if(cell != null){
            topTableCell = cell;

            cell.setElement(new Table(t -> {
                t.add(topTable).growX().row();
                t.add(this).growX();
            }));
        }

        Cell<?> mainStackCell = ElementUtils.getCell(mainStack);
        if(mainStackCell != null){
            oldCell = new Cell<>().set(mainStackCell);
            mainStackCell.set(new Cell<>().colspan(3).right());
        }
    }

    @Override
    public void resetOverride(){
        if(oldVisible != null){
            topTable.visible(oldVisible);
        }

        if(topTableCell != null){
            topTableCell.setElement(topTable);
        }

        if(mainStack != null){
            Cell<?> mainStackCell = ElementUtils.getCell(mainStack);
            if(mainStackCell != null){
                mainStackCell.set(oldCell);
            }
        }
    }

    static abstract class BaseInfoTable extends Table{

        public BaseInfoTable(){
            background(Tex.pane);
        }

        public BaseInfoTable rebuild(){
            clearChildren();

            if(!shouldRebuild()) return this;

            build();

            return this;
        }

        public abstract boolean shouldRebuild();

        public abstract void hovered();

        protected abstract void build();
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
