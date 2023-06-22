package MinerTools.ui.tables.floats;

import MinerTools.ui.tables.*;
import arc.scene.ui.layout.*;

public class ToolsFloatTable extends FloatTable {
    private ToolsTable toolsTable;

    public ToolsFloatTable() {
        super("tools");
    }

    @Override
    protected void init() {
        super.init();

        toolsTable = new ToolsTable();
    }

    @Override
    protected void setupCont(Table cont) {
        super.setupCont(cont);
        cont.add(toolsTable).grow();
    }
}

