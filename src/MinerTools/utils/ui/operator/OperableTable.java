package MinerTools.utils.ui.operator;

import arc.scene.*;
import arc.scene.ui.layout.*;

public class OperableTable extends Table{
    private final OperateCons cons;

    public OperableTable(boolean keepInStage){
        cons = new OperateCons(keepInStage){

            @Override
            public void onDragged(float deltaX, float deltaY){
                OperableTable.this.onDragged(deltaX, deltaY);
            }

            @Override
            public void onResized(float deltaWidth, float deltaHeight){
                OperableTable.this.onResized(deltaWidth, deltaHeight);
            }

            @Override
            public void onReleased(){
                OperableTable.this.onReleased();
            }

            @Override
            public void onAligned(Element aligned, int alignFrom, int alignTo){
                OperableTable.this.onAligned(aligned, alignFrom, alignTo);
            }
        };

        ElementOperator.operableTables.add(this);
    }

    public void operate(){
        ElementOperator.operate(this, cons);
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

    protected void onDragged(float deltaX, float deltaY){
    }

    protected void onResized(float deltaWidth, float deltaHeight){
    }

    protected void onReleased(){
    }

    protected void onAligned(Element aligned, int alignFrom, int alignTo){
    }
}
