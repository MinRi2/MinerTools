package MinerTools.ui.settings;

import MinerTools.ui.settings.BaseSetting.*;
import MinerTools.ui.settings.CategorySetting.*;
import arc.func.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

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
        return addCategory(name, categorySetting -> {
        });
    }

    public CategorySetting addCategory(String name, Cons<CategorySetting> cons){
        CategorySetting category = new CategorySetting(this.name + "." + name);
        categories.add(category);

        cons.get(category);

        rebuild();

        return category;
    }

    public void addCategory(String name, Cons<CategorySetting> cons, CategoryBuilder builder){
        CategorySetting category = new CategorySetting(this.name + "." + name, builder);
        categories.add(category);

        cons.get(category);

        rebuild();
    }

    protected void rebuild(){
        clearChildren();

        for(BaseSetting setting : settings){
            setting.setup(this);
        }

        row();

        if(categories.any()){
            for(CategorySetting category : categories){
                table(category::build).padTop(8f).growX().top().row();
            }
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

    public boolean hasSetting(){
        return settings.any() || categories.any();
    }

}