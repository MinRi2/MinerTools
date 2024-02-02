package MinerTools.ui;

import MinerTools.*;
import MinerTools.interfaces.*;
import MinerTools.ui.override.CoreItemsDisplay;
import MinerTools.ui.override.*;
import MinerTools.ui.settings.*;
import MinerTools.ui.tables.*;
import MinerTools.ui.tables.floats.*;
import MinerTools.utils.ui.*;
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

    private final Seq<Addable> addableTables = new Seq<>();
    private final Seq<FloatTable> floats = new Seq<>();
    private final Seq<OverrideUI> overrides = new Seq<>();

    // Override
    public CoreItemsDisplay coreItemsDisplay;
    public BetterInfoTable betterHover;

    // Settings
    public MSettingsTable settings;

    // DraggableTable

    // FloatTable
    public ChatTable chat;
    public ToolsFloatTable toolsTable;
    public ScriptButtons scriptButtons;

    public MUI(){
    }

    public static void showTableAtMouse(Cons<Table> cons){
        showTableAt(input.mouseX(), input.mouseY(), center, cons);
    }

    public static void showTableAt(float x, float y, int align, Cons<Table> cons){
        showTableAt(x, y, align, cons, table -> !table.hasMouse());
    }

    public static void showTableAt(float x, float y, int align, Cons<Table> cons, Boolf<Table> hideBoolp){
        Table table = new Table(black6);
        scene.add(table);
        table.actions(Actions.fadeIn(0.5f, Interp.smooth), Actions.remove());

        cons.get(table);
        table.pack();

        table.setPosition(x, y, align);
        table.keepInStage();

        table.update(() -> {
            if(hideBoolp.get(table)){
                table.actions(Actions.fadeOut(0.5f, Interp.smooth), Actions.remove());
            }
        });
    }

    public static void showInfoToast(String info, float duration, int align){
        showInfoToastAt(scene.root.getX(align), scene.root.getY(align), info, duration, center);
    }

    public static void showInfoToastAt(float x, float y, String info, float duration, int align){
        Table table = new Table(Styles.black3);
        table.touchable = Touchable.disabled;

        table.update(() -> {
            if(state.isMenu()) table.remove();
        });

        table.actions(Actions.fadeIn(0.5f, Interp.smooth), Actions.delay(duration), Actions.fadeOut(0.5f, Interp.smooth), Actions.remove());
        table.add(info).style(Styles.outlineLabel);

        table.pack();

        table.setPosition(x, y, align);
        table.keepInStage();
        scene.add(table);
    }

    public static void setClipboardText(String text){
        /* Do not copy the empty text */
        if(!text.equals("")){
            app.setClipboardText(text);
            showInfoToast("Copy: " + text, 3f, Align.bottom);
        }
    }

    public void init(){
        MStyles.load();

        addableTables.addAll(
        settings = new MSettingsTable()
        );

        floats.addAll(
        chat = new ChatTable(),
        toolsTable = new ToolsFloatTable(),
        scriptButtons = new ScriptButtons()
        );

        overrides.addAll(
        coreItemsDisplay = new CoreItemsDisplay(),
        betterHover = new BetterInfoTable()
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
                Element result = scene.hit(input.mouseX(), input.mouseY(), true);
                if(result == null || !result.isDescendantOf(pane)){
                    scene.setScrollFocus(null);
                }
            }
        }
    }
}
