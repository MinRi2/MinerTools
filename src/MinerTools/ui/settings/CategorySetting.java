package MinerTools.ui.settings;

import MinerTools.ui.*;
import arc.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;

/**
 * 设置分类组
 */
public class CategorySetting extends MSettingTable{
    private CategoryBuilder builder;

    private boolean shown = true;

    public CategorySetting(String name){
        this(name, CategoryBuilder.defaultBuilder);
    }

    public CategorySetting(String name, CategoryBuilder builder){
        super(null, name);

        this.builder = builder;

        top().left();
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

    public void build(Table container){
        builder.build(container, this);
    }

    public interface CategoryBuilder{
        CategoryBuilder defaultBuilder = (container, category) -> {
            container.button(category.localizedName(), MStyles.clearToggleTranst, category::toggle)
            .height(32f).checked(b -> category.isShown()).growX();

            container.row();

            container.table(Tex.pane2, t -> {
                t.collapser(category, true, category::isShown)
                .growX().get().setDuration(0.4f);
            }).growX();
        };

        void build(Table container, CategorySetting category);
    }

}
