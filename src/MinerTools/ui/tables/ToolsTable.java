package MinerTools.ui.tables;

import MinerTools.ui.tables.members.*;

public class ToolsTable extends MembersTable {
    public ToolsTable() {
        addMember(new TeamsInfo(), new TeamChanger(), new PlayerList(), new SchematicList(), new AITable());
        rebuildMembers();
    }
}

