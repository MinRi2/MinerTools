package MinerTools.ui.settings;

import MinerTools.*;
import MinerTools.utils.ui.*;
import arc.*;
import arc.func.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

import static arc.Core.bundle;

public abstract class BaseSetting{
    String name, title, describe;

    public BaseSetting(String name){
        this.name = name;
        title = bundle.get("miner-tools.setting." + name + ".name");
        describe = bundle.get("miner-tools.setting." + name + ".describe", "");
    }

    public BaseSetting(String name, Object def){
        this(name);

        MinerVars.settings.put(name, def, true, true);
    }

    public abstract void setup(Table table);

    protected void addDescribeTo(Element element){
        if(!describe.isEmpty()){
            ElementUtils.addTooltip(element, describe, Align.topLeft, true);
        }
    }

    protected void setValue(Object value){
        MinerVars.settings.put(name, value, false, true);
    }

    public static class CheckSetting extends BaseSetting{
        boolean def;
        Boolc changed;
        private CheckBox box;

        public CheckSetting(String name, boolean def, Boolc changed){
            super(name, def);
            this.def = def;
            this.changed = changed;
        }

        @Override
        public void setup(Table table){
            box = new CheckBox(title);

            box.update(() -> box.setChecked(MinerVars.settings.getBool(name)));

            box.changed(() -> {
                setValue(box.isChecked());

                if(changed != null){
                    changed.get(box.isChecked());
                }
            });

            box.left();
            addDescribeTo(box);

            table.add(box).left().padTop(3f);
            table.row();
        }

        public void change(){
            changed.get(MinerVars.settings.getBool(name));
        }
    }

    public static class SliderSetting extends BaseSetting{
        int def, min, max, step;
        StringProcessor sp;

        public SliderSetting(String name, int def, int min, int max, int step, StringProcessor s){
            super(name, def);
            this.def = def;
            this.min = min;
            this.max = max;
            this.step = step;
            this.sp = s;
        }

        @Override
        public void setup(Table table){
            Slider slider = new Slider(min, max, step, false);

            slider.setValue(MinerVars.settings.getInt(name));

            Label value = new Label("", Styles.outlineLabel);
            Table content = new Table();
            content.add(title, Styles.outlineLabel).left().growX().wrap();
            content.add(value).padLeft(10f).right();
            content.margin(3f, 33f, 3f, 33f);
            content.touchable = Touchable.disabled;

            slider.changed(() -> {
                setValue((int)slider.getValue());
                value.setText(sp.get((int)slider.getValue()));
            });

            slider.change();

            addDescribeTo(table.stack(slider, content).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).left().padTop(4f).get());
            table.row();
        }

        public void change(){
            sp.get(MinerVars.settings.getInt(name));
        }
    }
}
