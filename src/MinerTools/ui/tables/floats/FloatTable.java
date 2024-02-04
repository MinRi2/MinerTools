package MinerTools.ui.tables.floats;

import MinerTools.*;
import MinerTools.ui.*;
import MinerTools.ui.settings.*;
import MinerTools.ui.tables.*;
import MinerTools.utils.ui.*;
import MinerTools.utils.ui.operator.*;
import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;

import static mindustry.Vars.*;
import static mindustry.ui.Styles.black6;

public class FloatTable extends SavedTable implements Addable{
    private final Rect lastBounds = new Rect();

    public Table title, body;
    public boolean showBody = true;

    private Table bodyCont;

    public FloatTable(String name){
        super(name, true, true);

        this.name = name;

        init();

        setup();

        visibility = () -> !state.isMenu() && ui.hudfrag.shown && !ui.minimapfrag.shown();
    }

    @Override
    public void addUI(){
        ui.hudGroup.addChild(this);
    }

    /**
     * 在rebuildCont方法执行前初始化变量
     */
    protected void init(){
        title = new Table(black6);
        body = new Table();

        body.top();

        addSettings();
    }

    protected void addSettings(){
        addSettings(MinerVars.ui.settings.ui.addCategory(name));
    }

    protected void addSettings(MSettingTable uiSettings){
        uiSettings.checkPref("floats." + name + ".shown", true, b -> {
            if(b){
                addUI();
            }else{
                remove();
            }
        }).change();
    }

    private void setup(){
        setupTitle();
        setupBody(body);

        add(title).growX();

        row();

        table(t -> {
            bodyCont = t;

            rebuildBodyCont();
        }).grow();

        pack();
    }

    protected void setupBody(Table body){
    }

    private void setupTitle(){
        title.clearChildren();

        title.add(Core.bundle.get("miner-tools.floats." + name, "unnamed")).padLeft(4f).growX().left();

        title.table(buttons -> {
            buttons.defaults().width(48f).growY().right();

            setupButtons(buttons);

            buttons.button(Icon.editSmall, MStyles.clearToggleAccentb, this::operate).checked(b -> operating());

            RotatedImage image = new RotatedImage(Icon.downSmall, 180);
            buttons.button(Icon.downSmall, MStyles.clearToggleAccentb, () -> {
                toggleCont();
                image.rotate(showBody ? 0 : 1, 0.5f, Interp.pow2Out);
            }).with(b -> b.replaceImage(image));

            buttons.button("x", MStyles.clearAccentt, () -> {
                MinerVars.settings.put("floats." + name + ".shown", false);
                removeManually();
            }).size(48f);
        }).growY().right();
    }

    private void removeManually(){
        MUI.showInfoToastAt(getX(Align.center), getTop() + 8, Core.bundle.get("miner-tools.floats.reshow-hint"), 2, Align.bottom);
        remove();
    }

    protected void setupButtons(Table buttons){
    }

    private void rebuildBodyCont(){
        bodyCont.clearChildren();

        if(showBody){
            bodyCont.add(body).grow();
        }
    }

    private void toggleCont(){
        showBody = !showBody;

        if(showBody){
            bodyCont.add(body).grow();

            setBounds(lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height);
        }else{
            bodyCont.clear();

            ElementUtils.getBoundsOnScene(this, lastBounds);

            Vec2 v = ElementUtils.getOriginOnScene(title, Tmp.v1);
            setPosition(v.x, v.y);
            pack();
        }

        keepInStage();
    }

    @Override
    protected void onResized(float deltaWidth, float deltaHeight){
        ElementUtils.getBoundsOnScene(this, lastBounds);
    }
}
