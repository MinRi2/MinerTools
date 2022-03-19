package MinerTools.ui.tables;

import arc.*;
import arc.scene.ui.layout.*;
import org.jetbrains.annotations.ApiStatus.*;

import static arc.Core.scene;
import static mindustry.ui.Styles.*;

public class FloatTable extends DraggableTable{
    private String name;

    private Table title;

    public FloatTable(String name){
        this.name = name;
        setup();
    }

    private void setup(){
        table(black3, t -> {
            title = t;
            setDraggier(t);
            t.add(Core.bundle.get(name)).padLeft(3f).left();
            t.button("X", () -> {});
        }).growX();

        row();

        table(this::rebuildCont).fillX();
    }

    @Override
    public void addUI(){
        scene.add(this);
    }

    @OverrideOnly
    private void rebuildCont(Table table){}
}
