package MinerTools.ui.settings;

import MinerTools.graphics.*;
import MinerTools.ui.tables.*;
import MinerTools.ui.utils.*;
import arc.*;
import arc.func.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

import static MinerTools.MinerVars.mSettings;
import static arc.Core.bundle;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class MSettingsTable extends Table implements Addable{
    public Seq<MSettingTable> settingTables = new Seq<>();

    private MSettingTable show;
    private Table settingTableCont = new Table();

    public MSettingsTable(){
        addSettings();

        setup();
    }

    @Override
    public void addUI(){
        Table menu = Reflect.get(ui.settings, "menu");
        Table prefs = Reflect.get(ui.settings, "prefs");

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
        MSettingTable game = new MSettingTable(Icon.list){{
        }};

        MSettingTable graphics = new MSettingTable(Icon.image){
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
    }

    private void setup(){
        add("MinerToolsSettings").center().row();
        image().color(Pal.accent).growX();

        row();

        table(buttons -> {
            for(MSettingTable settingTable : settingTables){
                buttons.button(settingTable.icon, clearToggleTransi, () -> {
                    settingTableCont.clear();

                    if(show != settingTable){
                        show = settingTable;
                        settingTableCont.add(settingTable).left();
                    }else{
                        show = null;
                    }
                }).grow().checked(b -> show == settingTable);
            }
        }).minWidth(70f * settingTables.size);

        row();

        add(settingTableCont).growX();
    }

    public class MSettingTable extends Table{
        public Drawable icon;
        public Seq<MSetting> settings = new Seq<>();

        public MSettingTable(Drawable icon){
            this.icon = icon;
            settingTables.add(this);
        }

        public MCheckSetting checkPref(String name, boolean def){
            return checkPref(name, def, null);
        }

        public MCheckSetting checkPref(String name, boolean def, Boolc changed){
            MCheckSetting setting;
            settings.add(setting = new MCheckSetting(name, def, changed));
            rebuild();
            return setting;
        }

        public MSliderSetting sliderPref(String name, int def, int min, int max, StringProcessor s){
            return sliderPref(name, def, min, max, 1, s);
        }

        public MSliderSetting sliderPref(String name, int def, int min, int max, int step, StringProcessor s){
            MSliderSetting setting;
            settings.add(setting = new MSliderSetting(name, def, min, max, step, s));
            rebuild();
            return setting;
        }

        private void rebuild(){
            clearChildren();

            for(MSetting setting : settings){
                setting.add(this);
            }
        }

        public static abstract class MSetting{
            String name, title, describe;

            public MSetting(String name){
                this.name = name;
                title = bundle.get("miner-tools.setting." + name + ".name");
                describe = bundle.get("miner-tools.setting." + name + ".describe");
            }

            public MSetting(String name, Object def){
                this(name);

                mSettings.put(name, def, true, true);
            }

            public abstract void add(Table table);

            protected void addDesc(Element element){
                if(!describe.equals("???miner-tools.setting." + name + ".describe???")){
                    ElementUtils.addTooltip(element, describe, Align.topLeft, true);
                }
            }

            protected void putSetting(Object value){
                mSettings.put(name, value, false, true);
            }
        }

        public static class MCheckSetting extends MSetting{
            boolean def;
            Boolc changed;

            public MCheckSetting(String name, boolean def, Boolc changed){
                super(name, def);
                this.def = def;
                this.changed = changed;
            }

            @Override
            public void add(Table table){
                CheckBox box = new CheckBox(title);

                box.update(() -> box.setChecked(mSettings.getBool(name)));

                box.changed(() -> {
                    putSetting(box.isChecked());

                    if(changed != null){
                        changed.get(box.isChecked());
                    }
                });

                box.left();
                addDesc(box);

                table.add(box).left().padTop(3f);
                table.row();
            }
        }

        public static class MSliderSetting extends MSetting{
            int def, min, max, step;
            StringProcessor sp;

            public MSliderSetting(String name, int def, int min, int max, int step, StringProcessor s){
                super(name, def);
                this.def = def;
                this.min = min;
                this.max = max;
                this.step = step;
                this.sp = s;
            }

            @Override
            public void add(Table table){
                Slider slider = new Slider(min, max, step, false);

                slider.setValue(mSettings.getInt(name));

                Label value = new Label("", Styles.outlineLabel);
                Table content = new Table();
                content.add(title, Styles.outlineLabel).left().growX().wrap();
                content.add(value).padLeft(10f).right();
                content.margin(3f, 33f, 3f, 33f);
                content.touchable = Touchable.disabled;

                slider.changed(() -> {
                    putSetting((int)slider.getValue());
                    value.setText(sp.get((int)slider.getValue()));
                });

                slider.change();

                addDesc(table.stack(slider, content).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).left().padTop(4f).get());
                table.row();
            }
        }
    }
}
