package MinerTools.ui;

import MinerTools.ui.logic.*;
import MinerTools.ui.settings.*;
import MinerTools.ui.tables.*;
import MinerTools.ui.tables.floats.*;
import arc.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ui.*;

import static arc.Core.app;
import static mindustry.Vars.*;

public class MUI{
    /* 集中处理鼠标未指向ScrollPane但又占用滑动的情况 */
    public static Seq<ScrollPane> panes = new Seq<>();

    private Seq<Addable> addableTables = new Seq<>();

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
            minerSettings = new MSettingsTable(),
            minerToolsTable = new MinerToolsTable(),
            chat = new ChatTable(),
            logicVars = new LogicVars()
        );

        addUI();
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