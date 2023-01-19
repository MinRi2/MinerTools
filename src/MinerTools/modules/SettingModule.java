package MinerTools.modules;

import MinerTools.*;
import MinerTools.ui.*;
import MinerTools.ui.settings.*;
import MinerTools.ui.settings.CategorySetting.*;
import arc.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public abstract class SettingModule extends AbstractModule<SettingModule>{

    public SettingModule(String name){
        super(name);
    }

    public SettingModule(SettingModule parent, String name){
        super(parent, name);
    }

    @Override
    public void load(){
        if(parent == null){
            MinerVars.ui.settings.modules.addCategory(name, this::setSettings, getBuilder(this));
        }

        for(SettingModule child : children){
            child.load();
        }
    }

    public void setSettings(MSettingTable settings){
        for(SettingModule child : children){
            settings.addCategory(child.name, child::setSettings, getBuilder(child));
        }
    }

    private static CategoryBuilder getBuilder(SettingModule module){
        return (container, category) -> {
            container.table(table -> {
                table.button(button -> {
                    button.add(category.localizedName()).left();
                    button.label(() -> {
                        return module.isEnable() ? Core.bundle.get("enabled") : Core.bundle.get("disabled");
                    }).growX().right().style(Styles.outlineLabel);
                }, MStyles.clearToggleTranst, module::toggle)
                .minWidth(150f).height(32f).checked(b -> module.isEnable()).growX();
                
                table.button(b -> {
                    b.image(() -> {
                        return (category.isShown() ? Icon.downSmall : Icon.upSmall).getRegion();
                    }).size(24f);
                }, Styles.clearNoneTogglei, category::toggle).size(32f)
                .checked(b -> category.isShown());
            }).growX();

            container.row();

            container.table(Tex.pane2, t -> {
                t.collapser(category, true, () -> module.isEnable() && category.isShown())
                .growX().get().setDuration(0.4f);
            }).growX().padLeft(12f);
        };
    }
}
