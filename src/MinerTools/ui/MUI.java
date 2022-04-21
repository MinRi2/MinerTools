package MinerTools.ui;

import MinerTools.*;
import MinerTools.ui.logic.*;
import MinerTools.ui.override.CoreItemsDisplay;
import MinerTools.ui.settings.*;
import MinerTools.ui.tables.*;
import MinerTools.ui.tables.floats.*;
import MinerTools.ui.utils.*;
import arc.*;
import arc.func.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ui.*;

import static arc.Core.*;
import static arc.util.Align.center;
import static mindustry.Vars.state;
import static mindustry.ui.Styles.black6;

public class MUI{
    /* 集中处理鼠标未指向ScrollPane但又占用滑动的情况 */
    public static Seq<ScrollPane> panes = new Seq<>();

    private Seq<Addable> addableTables = new Seq<>();

    // Override
    public CoreItemsDisplay coreItemsDisplay;

    // Settings
    public MSettingsTable minerSettings;

    // MinerToolsTable
    public MinerToolsTable minerToolsTable;

    // DraggableTable
    public LogicVars logicVars;

    // FloatTable
    public ChatTable chat;

    public MUI(){
    }

    public void init(){
        MStyles.load();

        addableTables.addAll(
        coreItemsDisplay = new CoreItemsDisplay(),
        minerSettings = new MSettingsTable(),
        minerToolsTable = new MinerToolsTable(),
        chat = new ChatTable(),
        logicVars = new LogicVars()
        );

        addUI();

        if(MinerVars.desktop){
            scene.root.clicked(() -> {
                if(input.ctrl()){
                    Element hit = ElementUtils.hitUnTouchable(scene.root, input.mouseX(), input.mouseY());

                    if(hit instanceof Label label){
                        setClipboardText(label.getText().toString());
                    }
                }
            });
        }
    }

    public void addUI(){
        for(Addable addableTable : addableTables){
            addableTable.addUI();
        }
    }

    public void update(){
        for(ScrollPane pane : panes){
            if(pane.hasScroll()){
                Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                if(result == null || !result.isDescendantOf(pane)){
                    Core.scene.setScrollFocus(null);
                }
            }
        }
    }

    public static void showTableAt(Cons<Table> cons){
        showTableAt(input.mouseX(), input.mouseY(), center, cons);
    }

    public static void showTableAt(float x, float y, int align, Cons<Table> cons){
        showTableAt(x, y, align, cons, table -> !table.hasMouse());
    }

    public static void showTableAt(float x, float y, int align, Cons<Table> cons, Boolf<Table> hideBoolp){
        Table table = new Table(black6);
        scene.add(table);

        cons.get(table);
        table.pack();

        table.setPosition(x, y, align);
        table.keepInStage();

        table.update(() -> {
            if(hideBoolp.get(table)) table.remove();
        });
    }

    public static void showInfoToast(String info, float duration, int align){
        Table table = new Table();
        table.setFillParent(true);
        table.touchable = Touchable.disabled;
        table.update(() -> {
            if(state.isMenu()) table.remove();
        });
        table.actions(Actions.delay(duration * 0.9f), Actions.fadeOut(duration * 0.3f, Interp.fade), Actions.remove());
        table.align(align).table(Styles.black3, t -> t.margin(4).add(info).style(Styles.outlineLabel)).padTop(10);
        Core.scene.add(table);
    }

    public static void setClipboardText(String text){
        /* Do not copy the empty text */
        if(!text.equals("")){
            app.setClipboardText(text);
            showInfoToast("Copy: " + text, 3f, Align.bottom);
        }
    }
}
