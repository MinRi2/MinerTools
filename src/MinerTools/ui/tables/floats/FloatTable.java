package MinerTools.ui.tables.floats;

import MinerTools.*;
import MinerTools.ui.*;
import MinerTools.ui.settings.*;
import MinerTools.ui.tables.*;
import MinerTools.utils.ui.*;
import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.actions.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;

import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class FloatTable extends DraggableTable implements Addable{
    private final Vec2 lastTitlePosition = new Vec2();

    private Table title, cont;

    private boolean showCont = true;

    public FloatTable(String name){
        super(name, true);

        init();

        setDraggier(title);
        setLastPos();

        setup();

        visibility = () -> !state.isMenu() && ui.hudfrag.shown && !ui.minimapfrag.shown();
    }

    @Override
    public void addUI(){
        ui.hudGroup.addChild(this);
    }

    @Override
    public boolean remove(){
        MUI.showInfoToastAt(getX(Align.center), getTop() + 8, Core.bundle.get("miner-tools.floats.reshow-hint"), 2, Align.bottom);
        return super.remove();
    }

    /**
     * 在rebuildCont方法执行前初始化变量
     */
    protected void init(){
        title = new Table(black6);
        cont = new Table();

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
        setupCont(cont);

        add(title).growX().minWidth(128f);

        row();

        collapser(cont, () -> showCont).growX();

        pack();
    }

    protected void setupCont(Table cont){
    }

    private void setupTitle(){
        title.clearChildren();

        title.add(Core.bundle.get("@miner-tools.floats." + name, "unnamed")).padLeft(4f).growX().left();

        title.table(buttons -> {
            buttons.defaults().width(48f).growY().right();

            setupButtons(buttons);

            buttons.button(isLocked() ? Icon.lockSmall : Icon.lockOpenSmall, clearNoneTogglei, this::toggleLocked).checked(b -> {
                b.getImage().setDrawable(isLocked() ? Icon.lockSmall : Icon.lockOpenSmall);
                return isLocked();
            });

            RotatedImage image = new RotatedImage(Icon.downSmall, 180);
            buttons.button(Icon.downSmall, clearNonei, () -> {
                toggleCont();
                image.rotate(showCont ? 0 : 1, 0.5f, Interp.pow2Out);
            }).with(b -> b.replaceImage(image));

            buttons.button("x", MStyles.clearPartial2t, () -> {
                MinerVars.settings.put("floats." + name + ".shown", false);
                remove();
            }).size(48f);
        }).growY().right();
    }

    protected void setupButtons(Table buttons){
    }

    private void toggleCont(){
        showCont = !showCont;

        if(showCont){
            ElementUtils.getOriginPosition(cont, lastTitlePosition);
            Core.app.post(() -> moveToSmooth(lastTitlePosition.x, lastTitlePosition.y - cont.getPrefHeight()));
        }else{
            ElementUtils.getOriginPosition(title, lastTitlePosition);
            Core.app.post(() -> moveToSmooth(lastTitlePosition.x, lastTitlePosition.y));
        }

        Core.app.post(() -> {
            pack();
            keepInStage();
        });
    }

    private void moveToSmooth(float x, float y){
        actions(Actions.moveTo(x, y, 0.5f, Interp.smooth));
    }

}
