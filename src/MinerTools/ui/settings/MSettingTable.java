package MinerTools.ui.settings;

import MinerTools.ui.settings.BaseSetting.*;
import MinerTools.ui.settings.CategorySetting.*;
import arc.*;
import arc.func.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

public class MSettingTable extends Table{
    private final Drawable icon;

    private final Seq<BaseSetting<?>> settings = new Seq<>();
    private final Seq<CategorySetting> categories = new Seq<>();

    public MSettingTable(Drawable icon, String name){
        this.icon = icon;
        this.name = name;

        top();
    }

    public void rebuild(){
        clearChildren();

        defaults().growX();

        for(BaseSetting<?> setting : settings){
            table(setting::setup);
            row();
        }

        if(categories.any()){
            for(CategorySetting category : categories){
                category.rebuild();
                table(category::build).padTop(8f);
                row();
            }
        }
    }

    public Drawable icon(){
        return icon;
    }

    public String name(){
        return Core.bundle.get("miner-tools.setting." + name + ".name", name);
    }

    public boolean hasSetting(){
        return settings.any() || categories.any();
    }

    public CategorySetting addCategory(String name){
        return addCategory(name, categorySetting -> {
        });
    }

    public CategorySetting addCategory(String name, Cons<CategorySetting> cons){
        CategorySetting category = new CategorySetting(this.name + "." + name);
        categories.add(category);

        cons.get(category);

        return category;
    }

    public void addCategory(String name, Cons<CategorySetting> cons, CategoryBuilder builder){
        CategorySetting category = new CategorySetting(this.name + "." + name, builder);
        categories.add(category);

        cons.get(category);
    }

    public CheckSetting checkPref(String name, boolean def){
        return checkPref(name, def, null);
    }

    public CheckSetting checkPref(String name, boolean def, Boolc changed){
        CheckSetting setting;
        settings.add(setting = new CheckSetting(name, def, changed));
        return setting;
    }

    public SliderSetting sliderPref(String name, int def, int min, int max, StringProcessor s){
        return sliderPref(name, def, min, max, 1, s);
    }

    public SliderSetting sliderPref(String name, int def, int min, int max, int step, StringProcessor s){
        SliderSetting setting;
        settings.add(setting = new SliderSetting(name, def, min, max, step, s));
        return setting;
    }

}