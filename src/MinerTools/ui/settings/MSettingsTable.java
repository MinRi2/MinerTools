package MinerTools.ui.settings;

import MinerTools.graphics.*;
import MinerTools.ui.tables.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

import static mindustry.ui.Styles.*;

public class MSettingsTable extends Table implements Addable{
    public Seq<MSettingTable> settingTables = new Seq<>();

    private MSettingTable show;
    private final Table settingTableCont = new Table();

    public MSettingTable game, graphics, ui;

    public MSettingsTable(){
        addSettings();

        setup();
    }

    @Override
    public void addUI(){
        Table menu = Reflect.get(Vars.ui.settings, "menu");
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
        });
    }

    public void addSettings(){
        game = new MSettingTable(Icon.list, "game"){
        };

        graphics = new MSettingTable(Icon.image, "graphics"){
            {
                addCategory("unit", setting -> {
                    drawerCheck(setting, "enemyUnitIndicator", true);
                    drawerRadiusSlider(setting, "enemyUnitIndicatorRadius", 100, 25, 250);

                    drawerCheck(setting, "unitAlert", true);
                    drawerRadiusSlider(setting, "unitAlertRadius", 10, 5, 50);

                    drawerCheck(setting, "unitInfoBar", true);
                });

                addCategory("build", setting -> {
                    drawerCheck(setting, "turretAlert", true);
                    drawerRadiusSlider(setting, "turretAlertRadius", 10, 5, 50);

                    drawerCheck(setting, "itemTurretAmmoShow", true);

                    drawerCheck(setting, "overdriveZone", true);

                    setting.addCategory("info", info -> {
                        drawerCheck(info, "buildStatus", true);
                        drawerCheck(info, "buildHealthBar", true);

                        drawerCheck(info, "constructBuildInfo", true);
                        drawerCheck(info, "unitBuildInfo", true);
                    });
                });

                addCategory("select", setting -> {
                    drawerCheck(setting, "buildSelectInfo", true);
                    drawerCheck(setting, "itemBridgeLinksShow", true);
                });

                addCategory("player", setting -> {
                    drawerCheck(setting, "payloadDropHint", true);
                    drawerCheck(setting, "playerRange", true);
                });
            }

            public static void drawerCheck(MSettingTable table, String name, boolean def){
                table.checkPref(name, def, b -> Renderer.updateEnable());
            }

            public static void drawerRadiusSlider(MSettingTable table, String name, int def, int min, int max){
                table.sliderPref(name, def, min, max, s -> {
                    Renderer.updateSettings();
                    return s + "(Tile)";
                });
            }
        };

        ui = new MSettingTable(Icon.chat, "ui");

        settingTables.addAll(game, graphics, ui);
    }

    private void setup(){
        table(t -> {
            t.table(table -> {
                table.add("MinerToolsSettings").center();
            }).growX().row();

            t.image().color(Pal.accent).minWidth(550f).growX();

            t.row();

            t.table(buttons -> {
                for(MSettingTable settingTable : settingTables){
                    buttons.button(settingTable.icon(), clearTogglePartiali, () -> {
                        settingTableCont.clear();

                        if(show != settingTable){
                            show = settingTable;
                            settingTableCont.add(settingTable).left();
                        }else{
                            show = null;
                        }
                    }).grow().checked(b -> show == settingTable);
                }
            }).minSize(70f * settingTables.size, 48f).padTop(5f).padBottom(5f);
        }).top();

        row();

        add(settingTableCont).top();
    }
}
