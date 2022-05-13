package MinerTools.ui.settings;

import MinerTools.graphics.*;
import MinerTools.ui.tables.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static mindustry.ui.Styles.clearNoneTogglei;

public class MSettingsTable extends Table implements Addable{
    public Seq<MSettingTable> settingTables = new Seq<>();

    private MSettingTable show;
    private final Table settingTableCont = new Table();

    public MSettingTable game, graphics;
    public CategoriesSettingTable ui;

    public MSettingsTable(){
        addSettings();

        setup();
    }

    @Override
    public void addUI(){
        /*Table menu = Reflect.get(Vars.ui.settings, "menu");
        Table prefs = Reflect.get(Vars.ui.settings, "prefs");

        menu.row();
        menu.button("MinerTools", Styles.cleart, () -> {
            prefs.clearChildren();
            prefs.add(this);
        }).name("miner-tools-settings");

        menu.update(() -> {
            if(menu.find("miner-tools-settings") == null){
                this.addUI();
            }
        });*/

        /* Use the setting category */
        Vars.ui.settings.addCategory("MinerTools", t -> t.add(this));
    }

    public void addSettings(){
        game = new MSettingTable(Icon.list){
        };

        graphics = new MSettingTable(Icon.image){
            {
                drawerCheck("enemyUnitIndicator", true);
                drawerRadiusSlider("enemyUnitIndicatorRadius", 100, 25, 250);

                drawerCheck("turretAlert", true);
                drawerRadiusSlider("turretAlertRadius", 10, 5, 50);

                drawerCheck("unitAlert", true);
                drawerRadiusSlider("unitAlertRadius", 10, 5, 50);

                drawerCheck("itemTurretAmmoShow", true);
            }

            public void drawerCheck(String name, boolean def){
                checkPref(name, def, b -> Drawer.updateEnable());
            }

            public void drawerRadiusSlider(String name, int def, int min, int max){
                sliderPref(name, def, min, max, s -> {
                    Drawer.updateSettings();
                    return s + "(Tile)";
                });
            }
        };

        ui = new CategoriesSettingTable(Icon.chat);

        settingTables.addAll(game, graphics, ui);
    }

    private void setup(){
        table(t -> {
            t.add("MinerToolsSettings").center().row();
            t.image().color(Pal.accent).minWidth(550f).growX();

            t.row();

            t.table(buttons -> {
                for(MSettingTable settingTable : settingTables){
                    buttons.button(settingTable.icon, clearNoneTogglei, () -> {
                        settingTableCont.clear();

                        if(show != settingTable){
                            show = settingTable;
                            settingTableCont.add(settingTable).left();
                        }else{
                            show = null;
                        }
                    }).grow().checked(b -> show == settingTable);
                }
            }).minWidth(70f * settingTables.size).padTop(5f).padBottom(5f);
        }).top();

        row();

        add(settingTableCont).top();
    }
}
