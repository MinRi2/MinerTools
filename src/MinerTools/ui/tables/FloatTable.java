package MinerTools.ui.tables;

import arc.*;
import arc.scene.ui.layout.*;
import mindustry.game.*;
import org.jetbrains.annotations.ApiStatus.*;

import static MinerTools.ui.MStyles.floatb;
import static arc.Core.scene;
import static mindustry.Vars.state;
import static mindustry.gen.Tex.*;
import static mindustry.ui.Styles.*;

public class FloatTable extends DraggableTable{
    private Table title;
    private Table cont;

    public FloatTable(String name){
        super(name, true);

        setLastPos();
        setup();

        Events.on(EventType.WorldLoadEvent.class, e -> {
            addUI();
            rebuildCont(cont);
        });

        update(this::update);
    }

    private void setup(){
        table(black3, t -> {
            setTitle(t);

            t.add(Core.bundle.get(name)).padLeft(3f).growX().left();
            t.button("x", floatb, () -> {
//                TODO
            }).width(35f).right();
        }).fillX().minWidth(250f);

        row();

        table(this::setCont).fillX().top().right();
    }

    @OverrideOnly
    protected void rebuildCont(Table cont){
        cont.clear();
    }

    @Override
    public void addUI(){
        scene.add(this);
    }

    protected void update(){
        if(state.isMenu()){
            remove();
        }
    }

    public void setTitle(Table title){
        this.title = title;
        setDraggier(title);
    }

    public void setCont(Table cont){
        this.cont = cont;
    }
}
