package MinerTools.ui.settings;

import MinerTools.*;
import MinerTools.ui.*;
import MinerTools.utils.ui.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

import static arc.Core.bundle;

public abstract class BaseSetting<T>{
    protected String name, title, describe;

    protected T defaultValue;

    public BaseSetting(String name){
        this.name = name;
        title = bundle.get("miner-tools.setting." + name + ".name");
        describe = bundle.get("miner-tools.setting." + name + ".describe", "");
    }

    public BaseSetting(String name, T defaultValue){
        this(name);

        MinerVars.settings.put(name, defaultValue, true, true);
    }

    public abstract void setup(Table table);

    protected void addDescribeTo(Element element){
        if(!describe.isEmpty()){
            ElementUtils.addTooltip(element, describe, Align.topLeft, true);
        }
    }

    protected T getValue(){
        return MinerVars.settings.get(name, defaultValue);
    }

    protected void setValue(T value){
        MinerVars.settings.put(name, value, false, true);
    }

    public static class CheckSetting extends BaseSetting<Boolean>{
        public @Nullable Boolc changed;

        public CheckSetting(String name, boolean defaultValue, Boolc changed){
            super(name, defaultValue);
            this.changed = changed;
        }

        @Override
        public void setup(Table table){
            BorderColorImage image = new BorderColorImage();

            image.setColor(getValue() ? Pal.accent : Color.red);

            table.button(b -> {
                b.left();
                b.add(image).size(32f).pad(4f);
                b.add(title);
            }, MStyles.clearAccentt, () -> {
                boolean value = !getValue();

                setValue(value);

                image.colorAction(getValue() ? Pal.accent : Color.red);

                if(changed != null){
                    changed.get(value);
                }
            }).with(this::addDescribeTo).growX();
        }

        public void change(){
            changed.get(getValue());
        }
    }

    public static class SliderSetting extends BaseSetting<Integer>{
        private StringProcessor processor;
        private int min, max, step;

        public SliderSetting(String name, int defaultValue, int min, int max, int step, StringProcessor processor){
            super(name, defaultValue);
            this.min = min;
            this.max = max;
            this.step = step;
            this.processor = processor;
        }

        @Override
        public void setup(Table table){
            Slider slider = new Slider(min, max, step, false);

            slider.setValue(getValue());

            Label value = new Label("", Styles.outlineLabel);
            Table content = new Table();
            content.add(title, Styles.outlineLabel).left().growX().wrap();
            content.add(value).padLeft(10f).right();
            content.margin(3f, 33f, 3f, 33f);
            content.touchable = Touchable.disabled;

            slider.changed(() -> {
                setValue((int)slider.getValue());
                value.setText(processor.get((int)slider.getValue()));
            });

            slider.change();

            Stack stack = table.stack(slider, content).maxWidth(400f).padTop(4f).growX().left().get();
            addDescribeTo(stack);
        }

        public void change(){
            processor.get(MinerVars.settings.getInt(name));
        }
    }
}
