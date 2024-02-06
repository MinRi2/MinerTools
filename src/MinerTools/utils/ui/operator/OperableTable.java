package MinerTools.utils.ui.operator;

import arc.scene.ui.layout.*;

public class OperableTable extends Table implements OperateCons{
    public boolean keepWithinStage;

    public OperableTable(boolean keepWithinStage){
        this.keepWithinStage = keepWithinStage;

        ElementOperator.operableTables.add(this);
    }

    public void operate(){
        ElementOperator.operate(this, this);
    }

    public boolean operating(){
        return ElementOperator.operating(this);
    }

    public boolean dragging(){
        return operating() && ElementOperator.dragMode;
    }

    public boolean resizing(){
        return operating() && ElementOperator.resizeMode;
    }

    public boolean operable(){
        return ElementOperator.operable(this);
    }

    @Override
    public boolean keepWithinStage(){
        return keepWithinStage;
    }
}
