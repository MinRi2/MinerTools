package MinerTools;

import MinerTools.input.ModBinding;
import MinerTools.io.MSettings;
import MinerTools.ui.MUI;
import arc.Core;
import arc.KeyBinds.KeyBind;
import arc.scene.ui.layout.Scl;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.Setting;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.SliderSetting;

import static arc.Core.app;
import static arc.Core.keybinds;
import static mindustry.Vars.maxSchematicSize;

public class MinerVars{
    public static MSettings settings;
    public static MUI ui;

    public static boolean desktop;

    public static void init(){
        settings = new MSettings();
        ui = new MUI();

        desktop = app.isDesktop();

        // update controls
        if(desktop){
            initBindings();
        }

        settings.init();
        ui.init();

        betterUIScaleSetting();
        betterSchemeSize();
    }

    public static void betterUIScaleSetting(){
        if(Core.settings.has("_uiscale")){
            MinerVars.settings.put("_uiscale", Core.settings.getInt("_uiscale"));
            Core.settings.remove("_uiscale");
        }

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
                if(shouldChange[0]){
                    //if the user changed their UI scale, but then put it back, don't consider it 'changed'
                    Core.settings.put("uiscalechanged", s != lastUiScale[0]);
                }else{
                    shouldChange[0] = true;
                }

                MinerVars.settings.put("_uiscale", s, false, true);

                return s + "%";
            }));
        }
        Vars.ui.settings.graphics.rebuild();

        Scl.setProduct(MinerVars.settings.getInt("_uiscale", 100) / 100f);
    }

    public static void betterSchemeSize(){
        maxSchematicSize = 128;
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
