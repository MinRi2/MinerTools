package MinerTools;

import MinerTools.input.*;
import MinerTools.io.*;
import MinerTools.ui.*;
import arc.KeyBinds.*;
import arc.util.*;
import mindustry.*;

import static arc.Core.*;
import static mindustry.Vars.maxSchematicSize;

public class MinerVars{
    public static final String modName = "miner-tools";
    public static final String modSymbol = "[yellow][M]";
    public static final float worldFontScl = Vars.tilesize / 36f;

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
}
