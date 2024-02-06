package MinerTools.ui.settings;

import MinerTools.graphics.*;
import MinerTools.ui.*;
import MinerTools.utils.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class MSettingsMenu extends Table{
    private final Table settingTableCont;
    public Seq<MSettingTable> settingTables = new Seq<>();
    public MSettingTable modules, graphics, ui;
    private MSettingTable select;

    public MSettingsMenu(){
        settingTableCont = new Table(Tex.pane2);

        addSettings();

        setup();
    }

    public void addSettings(){
        modules = new MSettingTable(Icon.list, "modules"){
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
                table.checkPref(name, def, b -> MRenderer.updateEnable());
            }

            public static void drawerRadiusSlider(MSettingTable table, String name, int def, int min, int max){
                table.sliderPref(name, def, min, max, s -> {
                    MRenderer.updateSettings();
                    return s + "(Tile)";
                });
            }
        };

        ui = new MSettingTable(Icon.chat, "ui");

        settingTables.addAll(modules, graphics, ui);
    }

    private void setup(){
        top();
        settingTableCont.top();

        ElementUtils.addTitle(this, "@miner-tools.settings", Pal.accent);

        table(buttons -> {
            buttons.defaults().grow();

            boolean isFirst = true;
            for(MSettingTable settingTable : settingTables){
                buttons.button(b -> {
                    b.image(settingTable.icon()).size(48f);
                    b.add(settingTable.name()).padLeft(8f);
                }, MStyles.settingt, () -> {
                    select(settingTable);
                }).checked(b -> select == settingTable).padLeft(isFirst ? 0f : 4f);

                isFirst = false;
            }
        }).height(80f).growX();

        row();

        pane(Styles.noBarPane, settingTableCont).scrollX(false).grow();
    }

    private void select(MSettingTable settingTable){
        settingTableCont.clear();

        if(select != settingTable){
            select = settingTable;
            settingTableCont.add(settingTable).grow();
        }else{
            select = null;
        }
    }
}
