package MinerTools;

import MinRi2.ModCore.io.*;
import MinerTools.input.*;
import MinerTools.ui.*;
import arc.KeyBinds.*;
import arc.files.*;
import arc.util.*;
import mindustry.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class MinerVars{
    public static final String modName = "miner-tools";
    public static final String modSymbol = "[yellow][M]";
    public static final float worldFontScl = Vars.tilesize / 36f;

    public static MinModSettings settings;
    public static MUI ui;

    public static boolean desktop;

    public static void init(){
        settings = MinModSettings.registerSettings(modName);
        migrateOldSettings();

        ui = new MUI();

        desktop = app.isDesktop();

        // update controls
        if(desktop){
            initBindings();
        }

        ui.init();

        betterSchemeSize();
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

    private static void migrateOldSettings(){
        Fi old = modDirectory.child("MinerTools").child("settings");

        if(old.exists()){
            old.copyTo(settings.settingsFi);
            settings.load();
            settings.save();
            modDirectory.child("MinerTools").deleteDirectory();
        }
    }
}
