package MinerTools.ui.tables;

import arc.input.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.layout.*;
import arc.util.*;

/** Table that can be dragged */
public class DraggableTable extends Table{
    public Element draggier;
    public boolean isDragging = false;

    public DraggableTable(){
        this(null);
    }

    public DraggableTable(Element draggier){
        setDraggier(draggier);
    }

    public void setDraggier(Element draggier){
        Element lastDraggier = this.draggier;
        this.draggier = draggier;
        if(lastDraggier != draggier) addListener();
    }

    public void addListener(){
        if(draggier == null){
            return;
        }

        draggier.addListener(new InputListener(){
            float fromx, fromy;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                fromx = x;
                fromy = y;
                isDragging = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer){
                Vec2 v = localToStageCoordinates(Tmp.v1.set(x, y));
                setPosition(v.x - fromx, v.y - fromy);

                keepInStage();
            }

            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, KeyCode button){
                isDragging = false;
            }
        });
    }
}