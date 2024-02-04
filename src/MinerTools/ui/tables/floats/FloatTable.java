package MinerTools.ui.tables.floats;

import MinerTools.*;
import MinerTools.ui.*;
import MinerTools.ui.settings.*;
import MinerTools.ui.tables.*;
import MinerTools.utils.ui.ElementOperator.*;
import MinerTools.utils.ui.*;
import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;

import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class FloatTable extends OperableTable implements Addable{
    private final Rect lastBound = new Rect();

    public Table title, body;
    public boolean showCont = true;

    private Table bodyCont;

    public FloatTable(String name){
        this(name, true);
    }

    public FloatTable(String name, boolean alizable){
        super(alizable);

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
        
        body.setFillParent(true);

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

        add(title).growX().minWidth(128f);

        row();

        table(t -> {
            bodyCont = t;
            bodyCont.add(body).grow();
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

            buttons.button(Icon.editSmall, clearNoneTogglei, this::operate).checked(b -> operating());

            RotatedImage image = new RotatedImage(Icon.downSmall, 180);
            buttons.button(Icon.downSmall, clearNonei, () -> {
                toggleCont();
                image.rotate(showCont ? 0 : 1, 0.5f, Interp.pow2Out);
            }).with(b -> b.replaceImage(image));

            buttons.button("x", MStyles.clearPartial2t, () -> {
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

    private void toggleCont(){
        showCont = !showCont;

        if(showCont){
            bodyCont.add(body).grow();

            setBounds(lastBound.x, lastBound.y, lastBound.width, lastBound.height);
        }else{
            bodyCont.removeChild(body);

            invalidate();

            ElementUtils.getBoundScene(this, lastBound);

            Vec2 v = ElementUtils.getOriginPosition(title, Tmp.v1);
            setPosition(v.x, v.y);
            pack();
        }

        Core.app.post(this::keepInStage);
    }

}
