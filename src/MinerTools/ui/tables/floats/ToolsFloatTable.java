package MinerTools.ui.tables.floats;

import MinerTools.ui.tables.*;
import MinerTools.ui.tables.members.*;
import arc.scene.ui.layout.*;

public class ToolsFloatTable extends FloatTable{
    private MembersTable toolsTable;

    public ToolsFloatTable(){
        super("tools");
    }

    @Override
    protected void init(){
        super.init();

        toolsTable = new MembersTable();

        toolsTable.addMember(new TeamsInfo(), new TeamChanger(), new PlayerList(), new AITable());
        toolsTable.rebuildMembers();
    }

    @Override
    protected void setupBody(Table body){
        super.setupBody(body);
        body.add(toolsTable).grow();
    }
}

