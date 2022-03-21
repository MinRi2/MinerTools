package MinerTools.ui.tables.floats;

import MinerTools.ui.tables.*;
import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.game.*;
import mindustry.gen.*;
import org.jetbrains.annotations.ApiStatus.*;

import static MinerTools.ui.MStyles.floatb;
import static arc.Core.scene;
import static mindustry.Vars.state;
import static mindustry.ui.Styles.*;

public class FloatTable extends DraggableTable{
    private Table title;
    private Table cont;

    private boolean showCont = true;

    public FloatTable(String name){
        super(name, true);

        init();

        setLastPos();

        Events.on(EventType.WorldLoadEvent.class, e -> {
            addUI();
            rebuild();
        });

        setDraggier(title);

        update(this::update);
    }

    private void init(){
        title = new Table(black6);
        cont = new Table();
    }

    private void rebuild(){
        clearChildren();

        rebuildTitle();
        rebuildCont(cont);

        add(title).fillX().minWidth(250f);

        row();

        collapser(cont, () -> showCont).fillX().top().right();

        pack();
        keepInStage();
        invalidateHierarchy();
    }

    private void rebuildTitle(){
        title.clearChildren();

        title.add(Core.bundle.get("miner-tools.floats." + name)).padLeft(3f).growX().left();

        ImageButton lockedBut = title.button(isLocked() ? Icon.lockSmall : Icon.lockOpenSmall, clearTogglePartiali, this::toggleLocked)
        .width(35f).right().checked(b -> isLocked()).get();

        ImageButton shownBut = title.button(showCont ? Icon.upSmall : Icon.downSmall, clearPartiali, this::toggleCont)
        .width(35f).right().get();

        lockedBut.changed(() -> lockedBut.getImage().setDrawable(isLocked() ? Icon.lockSmall : Icon.lockOpenSmall));
        shownBut.changed(() -> shownBut.getImage().setDrawable(showCont ? Icon.upSmall : Icon.downSmall));

        title.button("x", floatb, () -> {
        }).width(35f).right();
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
        pack();
    }

    private void toggleCont(){
        showCont = !showCont;
    }
}
