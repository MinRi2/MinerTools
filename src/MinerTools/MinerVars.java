package MinerTools;

import MinerTools.input.*;
import MinerTools.io.*;
import MinerTools.ui.*;
import arc.KeyBinds.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class MinerVars{
    public static MinerToolsSettings mSettings;
    public static MUI mui;

    public static boolean desktop;

    public static Seq<UnitType> visibleUnits = new Seq<>();
    public static Seq<Block> visibleBlocks = new Seq<>();
    public static Seq<Item> allOres = new Seq<>();

    public static boolean enableUpdateConveyor;

    public static void init(){
        mSettings = new MinerToolsSettings();
        mui = new MUI();

        initBetterUIScaleSetting();

        desktop = control.input instanceof DesktopInput;

        // update controls
        if(desktop){
            initBindings();
        }

        mSettings.init();
        mui.init();
    }

    public static void initContent(){
        visibleBlocks.clear();
        visibleUnits.clear();
        allOres.clear();

        for(Block block : content.blocks()){
            if(block.isVisible()){
                visibleBlocks.add(block);
            }

            if(block.itemDrop != null && !allOres.contains(block.itemDrop)){
                allOres.add(block.itemDrop);
            }

            if(block instanceof ItemBridge){
                block.allowConfigInventory = true;
            }
        }

        for(UnitType type : content.units()){
            if(!type.isHidden()){
                visibleUnits.add(type);
            }
        }

        allOres.sort(item -> item.id);
    }

    public static void initBetterUIScaleSetting(){
        int[] lastUiScale = {settings.getInt("uiscale", 100)};
        int index = ui.settings.graphics.getSettings().indexOf(setting -> setting.name.equals("uiscale"));

        settings.put("uiscale", settings.getInt("_uiscale", 100));

        if(index != -1){
            ui.settings.graphics.getSettings().set(index, new SliderSetting("uiscale", 100, 25, 300, 1, s -> {
                //if the user changed their UI scale, but then put it back, don't consider it 'changed'
                settings.put("uiscalechanged", s != lastUiScale[0]);
                settings.put("_uiscale", s);
                return s + "%";
            }));
        }
        ui.settings.graphics.rebuild();

        Scl.setProduct(settings.getInt("_uiscale", 100) / 100f);
    }

    public static void initBindings(){
        KeyBind[] bindings = Reflect.get(keybinds, "definitions");
        KeyBind[] modBindings = ModBinding.values();

        KeyBind[] newBindings = new KeyBind[bindings.length + modBindings.length];
        System.arraycopy(bindings, 0, newBindings, 0, bindings.length);
        System.arraycopy(modBindings, 0, newBindings, bindings.length, modBindings.length);

        keybinds.setDefaults(newBindings);
        Reflect.invoke(keybinds, "load");
        Reflect.invoke(ui.controls, "setup");
    }
}
