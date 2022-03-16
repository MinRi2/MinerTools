package MinerTools.core;

import MinerTools.ui.*;
import MinerTools.ui.logic.*;

import static MinerTools.MinerVars.desktop;

public class MUI{
    public MSettingsTable mSettings;
    public MinerToolsTable minerToolsTable;
    public LogicVars logicVars;

    public MUI(){
    }

    public void init(){
        MStyles.load();

        mSettings = new MSettingsTable();

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
