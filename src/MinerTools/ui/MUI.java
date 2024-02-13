package MinerTools.ui;

import MinRi2.ModCore.ui.*;
import MinerTools.interfaces.*;
import MinerTools.ui.override.*;
import MinerTools.ui.settings.*;
import MinerTools.ui.tables.floats.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.struct.*;

import static arc.Core.*;

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
    public MSettingsMenu settings;
    public MSettingsDialog settingsDialog;

    // DraggableTable

    // FloatTable
    public ChatTable chat;
    public ToolsFloatTable toolsTable;
    public ScriptButtons scriptButtons;
    public MainTable main;

    public MUI(){
    }

    public void init(){
        MStyles.load();

        settings = new MSettingsMenu();
        settingsDialog = new MSettingsDialog();

        floats.addAll(
        main = new MainTable(),
        chat = new ChatTable(),
        toolsTable = new ToolsFloatTable(),
        scriptButtons = new ScriptButtons()
        );

        overrides.addAll(
        coreItemsDisplay = new CoreItemsDisplay(),
        betterHover = new BetterInfoTable()
        );

        addableTables.addAll(floats);

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
                Element result = scene.hit(input.mouseX(), input.mouseY(), true);
                if(result == null || !result.isDescendantOf(pane)){
                    scene.setScrollFocus(null);
                }
            }
        }
    }
}
