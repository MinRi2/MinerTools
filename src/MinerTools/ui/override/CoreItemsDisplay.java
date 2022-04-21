package MinerTools.ui.override;

import MinerTools.ui.tables.*;
import arc.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.storage.CoreBlock.*;

import static MinerTools.MinerVars.desktop;
import static mindustry.Vars.*;

public class CoreItemsDisplay extends Table implements Addable{
    public static float iconSize = (desktop ? iconMed : iconSmall), fontScale = 0.95f;
    private static final Interval timer = new Interval();

    private Table itemsInfoTable = new Table();

    private final ObjectSet<Item> usedItems = new ObjectSet<>();
    private final ObjectSet<UnitType> usedUnits = new ObjectSet<>();

    private final int[] lastUpdateItems = new int[content.items().size];
    private final WindowedMean[] means = new WindowedMean[content.items().size];

    private Table planInfoTable = new Table(Styles.black6);
    private ItemSeq planItems = new ItemSeq();
    private int lastTotal;

    private CoreBuild core;

    public CoreItemsDisplay(){
        init();

        Events.on(ResetEvent.class, e -> resetUsed());

        add(itemsInfoTable).row();
        add(planInfoTable).fillX().padTop(2f);

        touchable = Touchable.disabled;
    }

    @Override
    public void addUI(){
        Element e = ui.hudGroup.find(c -> c instanceof mindustry.ui.CoreItemsDisplay);

        if(e.parent instanceof Collapser collapser){
            collapser.setTable(this);

            collapser.setCollapsed(() -> !(Core.settings.getBool("coreitems") && ui.hudfrag.shown));
        }
    }

    private void init(){
        for(int i = 0; i < means.length; i++){
            means[i] = new WindowedMean(6);
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

        float buildCostMultiplier = state.rules.buildCostMultiplier;
        float breakMultiplier = -1 * buildCostMultiplier * state.rules.deconstructRefundMultiplier;

        control.input.allRequests().each(plan -> {
            if(plan.block instanceof CoreBlock) return;

            float mul = plan.breaking ? (breakMultiplier * plan.progress) : (buildCostMultiplier * (1 - plan.progress));

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

                itemsInfoTable.label(() -> "" + UI.formatAmount(core.items.get(item))).padRight(3).minWidth(52f).left();

                if(++i % 5 == 0){
                    itemsInfoTable.row();
                }
            }
        }

        for(UnitType unit : content.units()){
            if(usedUnits.contains(unit)){
                itemsInfoTable.image(unit.uiIcon).size(iconSize).padRight(3);

                //TODO leaks garbage
                itemsInfoTable.label(() -> "" + Vars.player.team().data().countType(unit)).padRight(3).minWidth(52f).left();

                if(++i % 5 == 0){
                    itemsInfoTable.row();
                }
            }
        }
    }

    private void rebuildPlanItems(){
        planInfoTable.clear();

        if(lastTotal == 0){
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

            planInfoTable.image(item.uiIcon).size(iconSize);

            String planColor = (planAmount > 0 ? "[scarlet]" : "[green]");
            String amountColor = (coreAmount < planAmount / 2 ? "[scarlet]" : coreAmount < planAmount ? "[stat]" : "[green]");

            planInfoTable.add(amountColor + UI.formatAmount(coreAmount) + "[white]/" + planColor + UI.formatAmount(Math.abs(planAmount)), fontScale).growX();

            if(++i % 5 == 0){
                planInfoTable.row();
            }
        }
    }
}