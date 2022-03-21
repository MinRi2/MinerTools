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

        setup();

        Events.on(EventType.WorldLoadEvent.class, e -> {
            addUI();
        });

        setDraggier(title);

        update(this::update);
    }

    /**
     * 在rebuildCont方法执行前初始化变量
     */
    protected void init(){
        title = new Table(black6);
        cont = new Table();
    }

    private void setup(){
        setupTitle();
        setupCont(cont);

        add(title).fillX().minWidth(250f);

        row();

        collapser(cont, true, () -> showCont).growX().top().left();

        invalidateHierarchy();
    }

    private void setupTitle(){
        title.clearChildren();

        title.add(Core.bundle.get("miner-tools.floats." + name)).padLeft(3f).growX().left();

        title.table(buttons -> {
            buttons.defaults().width(35f).growY().right();

            ImageButton lockedBut = buttons.button(isLocked() ? Icon.lockSmall : Icon.lockOpenSmall, clearTogglePartiali, this::toggleLocked)
            .checked(b -> isLocked()).get();

            ImageButton shownBut = buttons.button(showCont ? Icon.upSmall : Icon.downSmall, clearPartiali, this::toggleCont)
            .get();

            lockedBut.changed(() -> lockedBut.getImage().setDrawable(isLocked() ? Icon.lockSmall : Icon.lockOpenSmall));
            shownBut.changed(() -> shownBut.getImage().setDrawable(showCont ? Icon.upSmall : Icon.downSmall));

            buttons.button("x", floatb, () -> {
            });
        }).growY().right();
    }

    @OverrideOnly
    protected void setupCont(Table cont){
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
        keepInStage();
    }

    private void toggleCont(){
        showCont = !showCont;
    }
}
