package MinerTools.ui.override;

import MinerTools.*;
import MinerTools.interfaces.*;
import MinerTools.ui.utils.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.logic.LogicBlock.*;
import mindustry.world.modules.*;

import static mindustry.Vars.*;

public class BetterInfoTable extends Table implements OverrideUI{
    private final BaseInfoTable<?> unitInfo, buildInfo, tileInfo;

    private final Seq<BaseInfoTable<?>> infoTables = Seq.with(
    unitInfo = new BaseInfoTable<Unit>(){
        @Override
        public Unit hovered(){
            return Units.closestOverlap(null, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 5f, Entityc::isAdded);
        }

        @Override
        protected void build(){
            unitDisplay(hover, this);

            var builders = unitBuilders.select(unitBuilder -> unitBuilder.canBuild(hover));
            if(builders.any()){
                for(var builder : builders){
                    builder.tryBuild(row(), hover);
                }
            }
        }
    },
    buildInfo = new BaseInfoTable<Building>(){
        @Override
        public Building hovered(){
            Tile tile = Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());

            if(tile == null) return null;

            return tile.build;
        }

        @Override
        protected void build(){
            Team team = hover.team;
            if(team != Vars.player.team()){
                hover.team(Vars.player.team());
                hover.display(this);
                hover.team(team);
            }else{
                hover.display(this);
            }

            var builders = buildBuilders.select(buildBuilder -> buildBuilder.canBuild(hover));
            if(builders.any()){
                for(var builder : builders){
                    builder.tryBuild(row(), hover);
                }
            }
        }
    },
    tileInfo = new BaseInfoTable<Tile>(){
        @Override
        public Tile hovered(){
            return Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
        }

        @Override
        public void build(){
            displayContent(this, hover.floor());
            if(hover.overlay() != Blocks.air) displayContent(this, hover.overlay());
            if(hover.block().isStatic()){
                displayContent(this, hover.block());
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

                        if(++index[0] % 3 == 0) itemsTable.row();
                    }));
                }).growX();
            }).growX();
        }
    }
    );

    private static final Seq<UnitBuilder> unitBuilders = Seq.with(
    /* Weapons */ new UnitBuilder(){
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
                            weaponTable.stack(new Image(weapon.region), label).minSize(iconSize).maxSize(80f, 120f).row();
                            weaponTable.add(new Bar("", Pal.ammo, () -> mount.reload / weapon.reload)).minSize(45f, 18f);
                        }).bottom().growX();

                        if(++index % 3 == 0) weaponsTable.row();
                    }
                }).growX();
            }).growX();
        }
    },
    /* Status */ new UnitBuilder(){
        @Override
        public boolean canBuild(Unit unit){
            return !unit.statusBits().isEmpty();
        }

        @Override
        protected void build(Table table, Unit unit){
            Bits status = unit.statusBits();

            table.table(Tex.pane, t -> {
                t.table(Tex.whiteui, tt -> tt.add("Status")).color(Color.gray).growX().row();

                float iconSize = Vars.mobile ? Vars.iconSmall : Vars.iconXLarge;

                t.table(statusTable -> {
                    int index = 0;
                    for(StatusEffect effect : Vars.content.statusEffects()){
                        if(!status.get(effect.id)) continue;

                        Label label;
                        if(Float.isInfinite(unit.getDuration(effect))){
                            label = new Label(() -> "[red]Inf");
                        }else{
                            label = new Label(() -> UI.formatTime(unit.getDuration(effect)));
                        }

                        label.setAlignment(Align.bottom);

                        statusTable.table(Tex.pane2, stateTable -> {
                            stateTable.stack(new Image(effect.uiIcon), label).size(iconSize).row();
                        }).bottom().growX();

                        if(++index % 4 == 0) statusTable.row();
                    }
                }).growX();
            }).growX();
        }
    }
    );

    /* For reset override */
    private Table topTable;
    private Boolp oldVisible;
    private Cell<?> topTableCell;

    private boolean locked;

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
            setting.checkPref("hover-locked", false, b -> locked = !locked).change();
        });
    }

    private void setup(){
        update(this::rebuild);
    }

    private void rebuild(){
        clearChildren();

        for(BaseInfoTable<?> table : infoTables){
            table.update();

            if(locked || table.shouldAdd()){
                add(table).margin(6).growX().row();
            }
        }
    }

    public void initOverride(){
        topTable = Reflect.get(Vars.ui.hudfrag.blockfrag, "topTable");
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
    }

    @Override
    public void resetOverride(){
        if(oldVisible != null){
            topTable.visible(oldVisible);
        }

        if(topTableCell != null){
            topTableCell.setElement(topTable);
        }
    }

    public static void unitDisplay(Unit unit, Table table){
        UnitType type = unit.type;

        table.table(t -> {
            t.left();
            t.add(new Image(type.uiIcon)).size(iconMed).scaling(Scaling.fit);
            t.labelWrap(type.localizedName).left().width(190f).padLeft(5);
        }).growX().left();
        table.row();

        table.table(bars -> {
            bars.defaults().growX().height(20f).pad(4);

            //TODO overlay shields
            bars.add(new Bar(
            () -> unit.health + "/" + type.health + "(" + (int)(unit.healthf() * 100) + "%" + ")",
            () -> Pal.health, unit::healthf).blink(Color.white));
            bars.row();

            if(state.rules.unitAmmo){
                bars.add(new Bar(type.ammoType.icon() + " " + Core.bundle.get("stat.ammo"), type.ammoType.barColor(), () -> unit.ammo / type.ammoCapacity));
                bars.row();
            }

            for(Ability ability : unit.abilities){
                ability.displayBars(unit, bars);
            }

            if(type.payloadCapacity > 0 && unit instanceof Payloadc payload){
                bars.add(new Bar(
                () -> Core.bundle.get("stat.payloadcapacity") + ": " + payload.payloadUsed() + "/" + type.payloadCapacity,
                () -> Pal.items, () -> payload.payloadUsed() /type.payloadCapacity));
                bars.row();

                var count = new float[]{-1};
                bars.table().update(t -> {
                    if(count[0] != payload.payloadUsed()){
                        payload.contentInfo(t, 8 * 2, 270);
                        count[0] = payload.payloadUsed();
                    }
                }).growX().left().height(0f).pad(0f);
            }
        }).growX();

        if(unit.controller() instanceof LogicAI ai){
            table.row();

            table.add(Blocks.microProcessor.emoji() + Core.bundle.get("units.processorcontrol")).growX().wrap().left();

            /* Show the position of controller */
            if(ai.controller instanceof LogicBuild logicBuild){
                table.row();

                table.add(Blocks.microProcessor.emoji() + Tmp.v1.set(logicBuild)).growX().wrap().left();
            }
        }

        table.row();

        /* Unit flag always show */
        table.label(() -> Iconc.settings + " " + (long)unit.flag + "").color(Color.lightGray).growX().wrap().left();
        table.row();
    }

    static abstract class BaseInfoTable<T> extends Table{
        T hover, lastHover;

        public BaseInfoTable(){
            background(Tex.pane);
        }

        public void update(){
            hover = hovered();

            if(shouldRebuild()) rebuild();

            lastHover = hover;
        }

        public void rebuild(){
            clearChildren();

            build();
        }

        public boolean shouldAdd(){
            return hover != null;
        }

        public boolean shouldRebuild(){
            return shouldAdd() && hover != lastHover;
        }

        public abstract T hovered();

        protected abstract void build();
    }

    static abstract class BaseBarBuilder<T extends Entityc>{
        public abstract boolean canBuild(T entity);

        public abstract void tryBuild(Table table, T entity);

        protected abstract void build(Table table, T entity);
    }

    static abstract class BuildBuilder<KT extends Block, DT extends Building> extends BaseBarBuilder<DT>{
        private final Seq<Block> blocks;

        public BuildBuilder(Boolf<Block> predicate){
            blocks = MinerVars.visibleBlocks.select(predicate);
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
