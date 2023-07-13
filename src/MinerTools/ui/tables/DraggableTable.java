package MinerTools.ui.tables;

import MinerTools.*;
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

    private boolean savePos;

    public DraggableTable(boolean savePos){
        this("", savePos);
    }

    public DraggableTable(String name, boolean savePos){
        this(name, null, savePos);
    }

    public DraggableTable(String name, Element draggier, boolean savePos){
        this.name = name;
        this.savePos = savePos;

        setDraggier(draggier);

        if(savePos){
            setLastPos();
        }
    }

    public void setLastPos(){
        if(name != null && !name.equals("")){
            float x = MinerVars.settings.getFloat("ui." + name + ".pos" + ".x");
            float y = MinerVars.settings.getFloat("ui." + name + ".pos" + ".y");
            setPosition(x, y);
            keepInStage();
        }
    }

    public void setDraggier(Element draggier){
        Element lastDraggier = this.draggier;
        if(lastDraggier != draggier){
            this.draggier = draggier;
            if(lastDraggier != null) lastDraggier.getListeners().removeAll(l -> l instanceof DragListener);
            addListener();
        }
    }

    public boolean isLocked(){
        return MinerVars.settings.getBool("ui." + name + ".pos" + ".locked");
    }

    public void toggleLocked(){
        MinerVars.settings.put("ui." + name + ".pos" + ".locked", !isLocked());
    }

    public void addListener(){
        if(draggier == null){
            return;
        }

        draggier.addListener(new DragListener(this));
    }

    public static class DragListener extends InputListener{
        DraggableTable target;
        float lastX, lastY;

        public DragListener(DraggableTable target){
            this.target = target;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
            lastX = x;
            lastY = y;
            target.isDragging = true;
            return true;
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer){
            if(!target.isLocked()){
                Vec2 v = target.localToStageCoordinates(Tmp.v1.set(x, y));
                target.setPosition(v.x - lastX, v.y - lastY);

                target.keepInStage();

                if(target.savePos){
                    MinerVars.settings.put("ui." + target.name + ".pos" + ".x", target.x);
                    MinerVars.settings.put("ui." + target.name + ".pos" + ".y", target.y);
                }

                target.keepInStage();
            }
        }

        @Override
        public void touchUp(InputEvent e, float x, float y, int pointer, KeyCode button){
            target.isDragging = false;
        }
    }
}