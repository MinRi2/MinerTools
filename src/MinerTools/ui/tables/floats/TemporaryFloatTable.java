package MinerTools.ui.tables.floats;

import MinerTools.utils.ui.*;
import arc.scene.*;
import arc.scene.ui.layout.*;

public class TemporaryFloatTable extends FloatTable{
    public TemporaryFloatTable(String name){
        super(name, false, true);
    }

    public void alignTo(Element target, int align, int alignTarget){
        ElementUtils.alignTo(this, target, align, alignTarget);
    }

    @Override
    protected void setupNameTable(Table table){
        table.add(name).padLeft(4f).growX().left();
    }

    @Override
    protected void setupButtons(Table buttons){
        super.setupButtons(buttons);
    }

    @Override
    protected void rebuildBody(Table body){
        super.rebuildBody(body);
    }
}
