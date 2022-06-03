package MinerTools.ui.settings;

import MinerTools.*;
import MinerTools.ui.*;
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
import mindustry.ui.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

import static arc.Core.bundle;

public class MSettingTable extends Table{
    private final Drawable icon;
    private final String name;

    private final Seq<BaseSetting> settings = new Seq<>();
    private final Seq<CategorySetting> categories = new Seq<>();

    public MSettingTable(Drawable icon, String name){
        this.icon = icon;
        this.name = name;
    }

    public CategorySetting addCategory(String name){
        return addCategory(name, categorySetting -> {});
    }

    public CategorySetting addCategory(String name, Cons<CategorySetting> cons){
        CategorySetting category = new CategorySetting(this.name + "." + name);
        categories.add(category);

        cons.get(category);

        rebuild();

        return category;
    }

    protected void rebuild(){
        clearChildren();

        for(BaseSetting setting : settings){
            setting.add(this);
        }

        row();

        for(CategorySetting category : categories){
            table(Tex.pane, t -> {
                t.button(category.localizedName(), MStyles.clearToggleTranst, category::toggle).checked(b -> category.isShown()).growX()
                .get().getLabel().setAlignment(Align.left);

                t.row();

                t.collapser(category, true, category::isShown).padTop(6f).left()
                .get().setDuration(0.2f);
            }).padTop(5f).fillX().row();
        }
    }

    public CheckSetting checkPref(String name, boolean def){
        return checkPref(name, def, null);
    }

    public CheckSetting checkPref(String name, boolean def, Boolc changed){
        CheckSetting setting;
        settings.add(setting = new CheckSetting(name, def, changed));
        rebuild();
        return setting;
    }

    public SliderSetting sliderPref(String name, int def, int min, int max, StringProcessor s){
        return sliderPref(name, def, min, max, 1, s);
    }

    public SliderSetting sliderPref(String name, int def, int min, int max, int step, StringProcessor s){
        SliderSetting setting;
        settings.add(setting = new SliderSetting(name, def, min, max, step, s));
        rebuild();
        return setting;
    }

    public Drawable icon(){
        return icon;
    }

    public String name(){
        return name;
    }

    public static class CategorySetting extends MSettingTable{
        private boolean shown = true;

        public CategorySetting(String name){
            super(null, name);
        }

        public String localizedName(){
            return Core.bundle.format("miner-tools.setting.category." + name() + ".name");
        }

        public void toggle(){
            shown = !shown;
        }

        public boolean isShown(){
            return shown;
        }
    }

    public static abstract class BaseSetting{
        String name, title, describe;

        public BaseSetting(String name){
            this.name = name;
            title = bundle.get("miner-tools.setting." + name + ".name");
            describe = bundle.get("miner-tools.setting." + name + ".describe");
        }

        public BaseSetting(String name, Object def){
            this(name);

            MinerVars.settings.put(name, def, true, true);
        }

        public abstract void add(Table table);

        protected void addDesc(Element element){
            if(!describe.startsWith("???") && !describe.endsWith("???")){
                ElementUtils.addTooltip(element, describe, Align.topLeft, true);
            }
        }

        protected void putSetting(Object value){
            MinerVars.settings.put(name, value, false, true);
        }
    }

    public static class CheckSetting extends BaseSetting{
        private CheckBox box;

        boolean def;
        Boolc changed;

        public CheckSetting(String name, boolean def, Boolc changed){
            super(name, def);
            this.def = def;
            this.changed = changed;
        }

        @Override
        public void add(Table table){
            box = new CheckBox(title);

            box.update(() -> box.setChecked(MinerVars.settings.getBool(name)));

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
        public void add(Table table){
            Slider slider = new Slider(min, max, step, false);

            slider.setValue(MinerVars.settings.getInt(name));

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

        public void change(){
            sp.get(MinerVars.settings.getInt(name));
        }
    }
}