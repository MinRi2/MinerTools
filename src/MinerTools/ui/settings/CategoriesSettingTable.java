package MinerTools.ui.settings;

import MinerTools.ui.*;
import arc.*;
import arc.scene.style.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

public class CategoriesSettingTable extends MSettingTable{
    private final Seq<CategorySetting> categories = new Seq<>();

    public CategoriesSettingTable(Drawable icon){
        super(icon);
    }

    @Override
    protected void rebuild(){
        super.rebuild();

        for(CategorySetting category : categories){
            table(Tex.pane, t -> {
                t.button(category.localizedName(), MStyles.clearToggleTransAccentt, category::toggle).checked(b -> category.isShown()).growX()
                .get().getLabel().setAlignment(Align.left);

                t.row();

                t.collapser(category, true, category::isShown).padTop(6f).left()
                .get().setDuration(0.2f);
            }).padTop(5f).fillX().row();
        }
    }

    public CategorySetting addCategorySetting(String name){
        var category = new CategorySetting(name);
        categories.add(category);
        rebuild();
        return category;
    }

    public static class CategorySetting extends MSettingTable{
        public final String name;
        private boolean shown = true;

        public CategorySetting(String name){
            super(null);

            this.name = name;
        }

        public String localizedName(){
            return Core.bundle.format("miner-tools.setting.category." + name + ".name");
        }

        public void toggle(){
            shown = !shown;
        }

        public boolean isShown(){
            return shown;
        }
    }
}
