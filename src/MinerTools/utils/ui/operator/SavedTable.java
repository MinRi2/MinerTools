package MinerTools.utils.ui.operator;

import MinerTools.*;
import MinerTools.utils.*;

public class SavedTable extends OperableTable{
    private final DebounceTask savePositionTask = new DebounceTask(1f, () -> {
        MinerVars.settings.put(name + ".pos.x", x);
        MinerVars.settings.put(name + ".pos.y", y);
    }), saveSizeTask = new DebounceTask(1f, () -> {
        MinerVars.settings.put(name + ".size.width", width);
        MinerVars.settings.put(name + ".size.height", height);
    });

    public boolean savePosition, saveSize;

    public SavedTable(String name, boolean savePosition, boolean saveSize){
        super(true);

        this.name = name;

        this.savePosition = savePosition;
        this.saveSize = saveSize;
    }

    protected void readPosition(){
        float x = MinerVars.settings.get(name + ".pos.x", this.x);
        float y = MinerVars.settings.get(name + ".pos.y", this.y);
        setPosition(x, y);
    }

    protected void readSize(){
        float width = MinerVars.settings.get(name + ".size.width", this.width);
        float height = MinerVars.settings.get(name + ".size.height", this.height);
        setSize(width, height);
    }

    @Override
    protected void onDragged(float deltaX, float deltaY){
        if(savePosition){
            savePositionTask.run();
        }
    }

    @Override
    protected void onResized(float deltaWidth, float deltaHeight){
        if(saveSize){
            saveSizeTask.run();
        }
    }
}
