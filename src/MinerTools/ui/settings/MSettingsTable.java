package MinerTools.ui.settings;

import arc.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

import static mindustry.Vars.ui;

public class MSettingsTable extends SettingsTable{
    public static Table menu;
    public static Table prefs;

    {
        menu = Reflect.get(ui.settings, "menu");
        prefs = Reflect.get(ui.settings, "prefs");
    }

    public MSettingsTable(){
        addSettings();

        menu.update(() -> {
            if(menu.find("miner-tools-settings") == null){
                addMenuButton();
            }
        });
    }

    public void addSettings(){
        checkPref("enemyUnitIndicator", true);
        checkPref("itemTurretAmmoShow", true);
    }

    private void addMenuButton(){
        menu.row();
        menu.button("MinerTools", Styles.cleart, this::visible).name("miner-tools-settings");
    }

    private void visible(){
        prefs.clearChildren();
        prefs.add(this);
    }
}
