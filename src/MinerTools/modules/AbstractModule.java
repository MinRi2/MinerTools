package MinerTools.modules;

import MinerTools.*;
import MinerTools.ui.settings.*;

public abstract class AbstractModule implements Module{
    protected final String name;
    private boolean enable = true;

    public AbstractModule(String name){
        this.name = name;
    }

    @Override
    public boolean isEnable(){
        return enable;
    }

    @Override
    public void setEnable(boolean enable){
        this.enable = enable;
    }

    public static abstract class SettingModule extends AbstractModule{

        public SettingModule(String name){
            super(name);
        }

        @Override
        public void load(){
            setSettings(MinerVars.ui.settings);
        }

        protected void setSettings(MSettingsTable settings){
            settings.game.addCategory("module." + name, categorySetting -> {
                categorySetting.checkPref("module." + name + ".enable", true, this::setEnable);
            });
        }

        @Override
        public boolean isEnable(){
            return MinerVars.settings.getBool("module." + name + ".enable", false);
        }

    }

}
