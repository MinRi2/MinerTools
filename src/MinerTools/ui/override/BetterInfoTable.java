package MinerTools.ui.override;

import MinerTools.*;
import MinerTools.interfaces.*;
import MinerTools.utils.*;
import MinerTools.utils.ui.*;
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
import mindustry.ui.fragments.*;
import mindustry.world.*;
import mindustry.world.blocks.logic.LogicBlock.*;
import mindustry.world.modules.*;

import java.lang.reflect.*;

import static mindustry.Vars.*;

public class BetterInfoTable extends Table implements OverrideUI{
    private Seq<InfoTable<?>> infoTables;

    /* For reset override */
    private Table topTable;
    private Stack mainStack;
    private Boolp oldVisible;
    private Cell<?> topTableCell, oldCell;

    private boolean locked;

    public BetterInfoTable(){
        initInfoTable();

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

    private void initInfoTable(){
        infoTables = Seq.with(
        new UnitInfoTable(){{
            addBuilders(
            /* Weapons */ new UnitBuilder(unit -> unit.type.hasWeapons() && !unit.disarmed){
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

                                if(++index % 4 == 0) weaponsTable.row();
                            }
                        }).growX();
                    }).growX();
                }
            },
            /* Status */ new UnitBuilder(unit -> !unit.statusBits().isEmpty()){
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
                                    label = new Label(() -> String.format("%.1f", unit.getDuration(effect)) + "s");
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
            });
        }},
        new BuildInfoTable(){{
            addBuilders(new BuildBuilder(build -> build.items != null && build.items.any()){
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
                                    itemTable.label(() -> UI.formatAmount(items.get(item))).padLeft(3f);
                                }).growX().padLeft(4f);

                                if(++index[0] % 3 == 0) itemsTable.row();
                            }));
                        }).growX();
                    }).growX();
                }
            });
        }},
        new TileInfoTable()
        );
    }

    private void addSetting(){
        MinerVars.ui.settings.ui.addCategory("overrideInfoTable", setting -> {
            setting.checkPref("overrideInfoTable", true, b -> tryToggleOverride());
            setting.checkPref("hover-locked", false, b -> locked = b).change();
        });
    }

    private void setup(){
        for(InfoTable<?> table : infoTables){
            collapser(table, () -> locked || table.visible).margin(6).growX().update(c -> {
                table.update();
            });

            row();
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

    public static abstract class InfoTable<T> extends Table{
        protected Seq<BaseBarBuilder> builders;

        protected T hover, lastHover;

        public InfoTable(){
            builders = new Seq<>();
            visibility = () -> hover != null;

            background(Tex.pane);
        }

        public void update(){
            hover = getHovered();

            if(hover != null && hover != lastHover) rebuild();

            lastHover = hover;
        }

        public void rebuild(){
            clearChildren();

            buildTable();
        }

        public abstract T getHovered();

        protected abstract void buildTable();

        protected void runBuilders(){
            for(BaseBarBuilder builder : builders){
                if(builder.canBuildFor((hover))){
                    builder.build(row(), hover);
                }
            }
        }

        public InfoTable<T> addBuilders(BaseBarBuilder... builders){
            this.builders.addAll(builders);
            return this;
        }

        public abstract class BaseBarBuilder{
            protected final Boolf<T> shouldBuild;

            public BaseBarBuilder(Boolf<T> shouldBuild){
                this.shouldBuild = shouldBuild;
            }

            private boolean canBuildFor(T entity){
                return shouldBuild.get(entity);
            }

            protected abstract void build(Table table, T entity);
        }
    }

    public static class UnitInfoTable extends InfoTable<Unit>{

        private static void unitDisplay(Unit unit, Table table){
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
                    () -> Pal.items, () -> payload.payloadUsed() / type.payloadCapacity));
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

            table.row();

            if(type.logicControllable){
                /* Unit flag always show */
                table.label(() -> Iconc.settings + " " + (long)unit.flag).color(Color.lightGray).growX().wrap().left();
                table.row();
            }

            if(unit.controller() instanceof LogicAI ai){
                table.row();

                table.add(Blocks.microProcessor.emoji() + Core.bundle.get("units.processorcontrol")).growX().wrap().left();

                /* Show the position of controller */
                if(ai.controller instanceof LogicBuild logicBuild){
                    table.row();

                    table.add(Blocks.microProcessor.emoji() + Tmp.v1.set(logicBuild)).growX().wrap().left();
                }

                if(net.active() && ai.controller != null && ai.controller.lastAccessed != null){
                    table.row();

                    table.add(Core.bundle.format("lastaccessed", ai.controller.lastAccessed)).growX().wrap().left();
                }
            }else if(net.active() && unit.lastCommanded != null){
                table.row();

                table.add(Core.bundle.format("lastcommanded", unit.lastCommanded)).growX().wrap().left();
            }
        }

        @Override
        public Unit getHovered(){
            return Units.closestOverlap(null, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 5f, Entityc::isAdded);
        }

        @Override
        protected void buildTable(){
            unitDisplay(hover, this);

            runBuilders();
        }

        public abstract class UnitBuilder extends BaseBarBuilder{
            public UnitBuilder(Boolf<Unit> shouldBuild){
                super(shouldBuild);
            }
        }
    }

    public static class BuildInfoTable extends InfoTable<Building>{
        final Field nextFlowBuildField = MinerUtils.getField(PlacementFragment.class, "nextFlowBuild");

        @Override
        public Building getHovered(){
            Tile tile = Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());

            if(tile == null) return null;

            MinerUtils.setValue(nextFlowBuildField, Vars.ui.hudfrag.blockfrag, tile.build);

            return tile.build;
        }

        @Override
        protected void buildTable(){
            Team team = hover.team;
            if(team != Vars.player.team()){
                hover.team(Vars.player.team());
                hover.display(this);
                hover.team(team);
            }else{
                hover.display(this);
            }

            marginBottom(6);

            runBuilders();
        }

        public abstract class BuildBuilder extends BaseBarBuilder{
            public BuildBuilder(Boolf<Building> shouldBuild){
                super(shouldBuild);
            }
        }
    }

    public static class TileInfoTable extends InfoTable<Tile>{
        private static void displayContent(Table table, UnlockableContent content){
            table.table(t -> {
                t.image(content.uiIcon).size(Vars.iconMed);
                t.add(content.localizedName).pad(5);
            }).growX();
        }

        @Override
        public Tile getHovered(){
            return Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
        }

        @Override
        public void buildTable(){
            displayContent(this, hover.floor());
            if(hover.overlay() != Blocks.air) displayContent(this, hover.overlay());
            if(hover.block().isStatic()){
                displayContent(this, hover.block());
            }
        }
    }
}
