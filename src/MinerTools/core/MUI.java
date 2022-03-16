package MinerTools.core;

import MinerTools.ui.*;
import MinerTools.ui.logic.*;
import MinerTools.ui.settings.MSettingsTable;

import static MinerTools.MinerVars.desktop;

public class MUI{
    public MSettingsTable minerSettings;
    public MinerToolsTable minerToolsTable;
    public LogicVars logicVars;

    public MUI(){
    }

    public void init(){
        MStyles.load();

        minerSettings = new MSettingsTable();

        minerToolsTable = new MinerToolsTable();

        if(desktop){
            logicVars = new LogicVars();
        }

        addUI();
    }

    public void addUI(){
        minerToolsTable.addUI();
    }
}
