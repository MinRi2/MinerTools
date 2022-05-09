package MinerTools.ui.override;

import MinerTools.*;
import MinerTools.interfaces.*;
import MinerTools.ui.settings.*;
import arc.*;
import arc.math.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.struct.ObjectIntMap.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.storage.CoreBlock.*;

import static MinerTools.MinerVars.desktop;
import static mindustry.Vars.*;

public class CoreItemsDisplay extends Table implements OverrideUI{
    public static int windowSize = 6;
    public static float iconSize = (desktop ? iconMed : iconSmall), fontScale = 0.95f, labelMinWidth = 50f;
    private static final Interval timer = new Interval();

    /* For override */
    private Table override;

    private final Table itemsInfoTable = new Table();

    private final ObjectSet<Item> usedItems = new ObjectSet<>();
    private final ObjectSet<UnitType> usedUnits = new ObjectSet<>();

    private final int[] lastUpdateItems = new int[content.items().size];
    private final WindowedMean[] means = new WindowedMean[content.items().size];

    private final Table planInfoTable = new Table();

    private int lastTotal;
    private final ItemSeq planItems = new ItemSeq();
    private final ObjectIntMap<Block> planBlockCounter = new ObjectIntMap<>(){
        @Override
        public void put(Block key, int value){
            super.put(key, get(key) + value);
        }
    };

    private CoreBuild core;

    public CoreItemsDisplay(){
        init();
        addSettings();

        Events.on(ResetEvent.class, e -> resetUsed());

        add(itemsInfoTable).row();
        add(planInfoTable).fillX().padTop(2f);
    }

    private void addSettings(){
        addSettings(MinerVars.ui.minerSettings.ui.addCategorySetting("overrideCoreItemsDisplay"));
    }

    private void addSettings(MSettingTable setting){
        setting.checkPref("overrideCoreItemsDisplay", true, b -> {
            if(b){
                doOverride();
            }else{
                resetOverride();
            }
        }).change();

        /* Add coreItems setting for mobile */
        if(mobile){
            var settings = ui.settings.graphics.getSettings();
            settings.insert(settings.indexOf(s -> s.name.equals("minimap")), new CheckSetting("coreitems", true, null));
            ui.settings.graphics.rebuild();
        }
    }

    private void init(){
        override = ui.hudGroup.find(c -> c instanceof mindustry.ui.CoreItemsDisplay);
        override.parent.touchable = Touchable.disabled;
        
        for(int i = 0; i < means.length; i++){
            means[i] = new WindowedMean(windowSize);
        }

        // Setup ui
        itemsInfoTable.update(() -> {
            CoreBuild core = Vars.player.team().core();

            if(timer.get(60f)) updateItems();

            if(core != null){
                this.core = core;

                boolean hasNewItem = content.items().contains(item -> core.items.get(item) > 0 && usedItems.add(item));
                boolean hasNewUnit = content.units().contains(unit -> Vars.player.team().data().countType(unit) > 0 && usedUnits.add(unit));

                if(hasNewItem || hasNewUnit){
                    rebuildItems();
                }
            }
        });

        planInfoTable.update(() -> {
            updatePlanItems();

            if(lastTotal != planItems.total){
                lastTotal = planItems.total;
                rebuildPlanItems();
            }
        });
    }

    public void resetUsed(){
        usedItems.clear();
        usedUnits.clear();

        for(WindowedMean mean : means){
            mean.clear();
        }
    }

    public void updateItems(){
        if(core == null) return;

        for(Item item : Vars.content.items()){
            int update = core.items.get(item) - lastUpdateItems[item.id];

            means[item.id].add(update);

            lastUpdateItems[item.id] = core.items.get(item);
        }
    }

    private void updatePlanItems(){
        planItems.clear();
        planBlockCounter.clear();

        float buildCostMultiplier = state.rules.buildCostMultiplier;
        float breakMultiplier = -1 * buildCostMultiplier * state.rules.deconstructRefundMultiplier;

        control.input.allPlans().each(plan -> {
            if(plan.block instanceof CoreBlock || plan.block.requirements.length == 0) return;

            planBlockCounter.put(plan.block, 1);

            float mul = plan.breaking ? (breakMultiplier * (1 - plan.progress)) : (buildCostMultiplier * (1 - plan.progress));

            for(ItemStack stack : plan.block.requirements){
                int planAmount = (int)(mul * stack.amount);
                planItems.add(stack.item, planAmount);
            }
        });
    }

    private void rebuildItems(){
        itemsInfoTable.clear();

        if(usedItems.size > 0 || usedUnits.size > 0){
            itemsInfoTable.background(Styles.black3);
            itemsInfoTable.margin(4);
        }

        int i = 0;
        for(Item item : content.items()){
            if(usedItems.contains(item)){
                Label label = new Label(() -> {
                    float update = means[item.id].mean();
                    if(update == 0) return "";
                    return (update < 0 ? "[red]" : "[green]+") + String.format("%.1f", update);
                });

                label.setFontScale(fontScale);
                label.setAlignment(Align.topRight);

                itemsInfoTable.stack(new Table(t -> t.image(item.uiIcon).size(iconSize)), label);

                itemsInfoTable.label(() -> "" + UI.formatAmount(core.items.get(item))).padRight(3f).minWidth(labelMinWidth).left();

                if(++i % 5 == 0){
                    itemsInfoTable.row();
                }
            }
        }

        for(UnitType unit : content.units()){
            if(usedUnits.contains(unit)){
                itemsInfoTable.image(unit.uiIcon).size(iconSize).padRight(3);

                //TODO leaks garbage
                itemsInfoTable.label(() -> "" + Vars.player.team().data().countType(unit)).padRight(3f).minWidth(labelMinWidth).left();

                if(++i % 5 == 0){
                    itemsInfoTable.row();
                }
            }
        }
    }

    private void rebuildPlanItems(){
        planInfoTable.clear();

        if(planItems.total == 0){
            planInfoTable.background(null);
            return;
        }else{
            planInfoTable.background(Styles.black3);
        }

        int i = 0;
        for(ItemStack stack : planItems){
            Item item = stack.item;
            int planAmount = stack.amount;
            int coreAmount = core.items.get(item);

            if(planAmount == 0){
                continue;
            }

            planInfoTable.image(item.uiIcon).size(iconSize).padRight(3f);

            String planColor = (planAmount > 0 ? "[scarlet]" : "[green]");
            String amountColor = (coreAmount < planAmount / 2 ? "[scarlet]" : coreAmount < planAmount ? "[stat]" : "[green]");

            planInfoTable.add(amountColor + UI.formatAmount(coreAmount) + "[white]/" + planColor + UI.formatAmount(Math.abs(planAmount)), fontScale)
            .padRight(3f).minWidth(labelMinWidth).growX();

            if(++i % 5 == 0){
                planInfoTable.row();
            }
        }

        planInfoTable.row();

        i = 0;
        for(Entry<Block> entry : planBlockCounter.entries()){
            Block block = entry.key;
            int count = entry.value;

            planInfoTable.image(block.uiIcon).size(iconSize).padRight(3f);
            planInfoTable.add("" + count, fontScale).padRight(3f).minWidth(labelMinWidth).growX();

            if(++i % 5 == 0){
                planInfoTable.row();
            }
        }
    }

    @Override
    public void doOverride(){
        if(override.parent instanceof Collapser c){
            c.setTable(this);
            c.setCollapsed(() -> !(Core.settings.getBool("coreitems") && ui.hudfrag.shown));
        }
    }

    @Override
    public void resetOverride(){
        if(parent instanceof Collapser c){
            c.setTable(override);
        }
    }
}