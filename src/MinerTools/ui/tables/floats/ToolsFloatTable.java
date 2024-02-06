package MinerTools.ui.tables.floats;

import MinerTools.ui.tables.*;
import MinerTools.ui.tables.members.*;
import arc.scene.ui.layout.*;

public class ToolsFloatTable extends FloatTable{
    public MembersTable toolsTable;

    public ToolsFloatTable(){
        super("tools");

        toolsTable = new MembersTable();

        toolsTable.addMember(new TeamsInfo(), new TeamChanger(), new PlayerList(), new AITable());
    }

    @Override
    public void rebuild(){
        super.rebuild();

        toolsTable.rebuildMembers();
    }

    @Override
    protected void rebuildBody(Table body){
        super.rebuildBody(body);
        
        body.add(toolsTable).grow();
    }
}

