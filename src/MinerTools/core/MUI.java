package MinerTools.core;

import MinerTools.ui.*;
import MinerTools.ui.logic.*;

public class MUI{
    public MinerToolsTable minerToolsTable;
    public LogicVars logicVars;

    public MUI(){
    }

    public void init(){
        MStyles.load();

        minerToolsTable = new MinerToolsTable();
        logicVars = new LogicVars();

        addUI();
    }

    public void addUI(){
        minerToolsTable.addUI();
    }
}
