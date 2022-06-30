package MinerTools;

import MinerTools.input.*;
import MinerTools.io.*;
import MinerTools.override.*;
import MinerTools.override.stats.*;
import MinerTools.ui.*;
import arc.*;
import arc.KeyBinds.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.type.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;

import static arc.Core.*;
import static mindustry.Vars.content;

public class MinerVars{
    public static MSettings settings;
    public static MUI ui;

    public static boolean desktop;

    public static Seq<UnitType> visibleUnits = new Seq<>();
    public static Seq<Block> visibleBlocks = new Seq<>();
    public static Seq<Item> allOres = new Seq<>();

    public static void init(){
        settings = new MSettings();
        ui = new MUI();

        desktop = app.isDesktop();

        betterUIScaleSetting();

        // update controls
        if(desktop){
            initBindings();
        }

        settings.init();
        ui.init();
    }

    public static void initContent(){
        visibleBlocks.clear();
        visibleUnits.clear();
        allOres.clear();

        for(Block block : content.blocks()){
            if(block.buildVisibility.visible()){
                visibleBlocks.add(block);
            }

            if(block.itemDrop != null && !allOres.contains(block.itemDrop)){
                allOres.add(block.itemDrop);
            }

            if(block instanceof ItemBridge){
                block.allowConfigInventory = true;
            }

            Bars.override(block);
            MStats.block.override(block);
        }

        for(UnitType type : content.units()){
            if(!type.isHidden()){
                visibleUnits.add(type);
            }

            MStats.unit.override(type);
        }

        allOres.sort(item -> item.id);
    }

    public static void betterUIScaleSetting(){
        int[] lastUiScale = {Core.settings.getInt("uiscale", 100)};
        int index = Vars.ui.settings.graphics.getSettings().indexOf(setting -> setting.name.equals("uiscale"));

        final boolean[] shouldChange = {false};

        Core.settings.put("uiscale", MinerVars.settings.getInt("_uiscale", 100));
        Core.settings.put("uiscalechanged", false);

        if(index != -1){
            Vars.ui.settings.graphics.getSettings().add(new Setting("rebuildListener"){
                @Override
                public void add(SettingsTable table){
                    shouldChange[0] = false;
                }
            });

            Vars.ui.settings.graphics.getSettings().set(index, new SliderSetting("uiscale", 100, 25, 300, 1, s -> {
                //if the user changed their UI scale, but then put it back, don't consider it 'changed'
                if(shouldChange[0]){
                    Core.settings.put("uiscalechanged", s != lastUiScale[0]);
                }else{
                    shouldChange[0] = true;
                }

                MinerVars.settings.put("_uiscale", s);

                return s + "%";
            }));
        }
        Vars.ui.settings.graphics.rebuild();

        Scl.setProduct(MinerVars.settings.getInt("_uiscale", 100) / 100f);
    }

    public static void initBindings(){
        KeyBind[] bindings = Reflect.get(keybinds, "definitions");
        KeyBind[] modBindings = ModBinding.values();

        KeyBind[] newBindings = new KeyBind[bindings.length + modBindings.length];
        System.arraycopy(bindings, 0, newBindings, 0, bindings.length);
        System.arraycopy(modBindings, 0, newBindings, bindings.length, modBindings.length);

        keybinds.setDefaults(newBindings);
        Reflect.invoke(keybinds, "load");
        Reflect.invoke(Vars.ui.controls, "setup");
    }
}
