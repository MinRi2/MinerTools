package MinerTools.ui.tables.floats;

import MinRi2.ModCore.ui.*;
import MinRi2.ModCore.ui.element.*;
import MinRi2.ModCore.ui.operator.*;
import MinerTools.*;
import MinerTools.ui.*;
import MinerTools.ui.settings.*;
import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;

import static mindustry.ui.Styles.black6;

public class FloatTable extends SavedTable implements Addable{
    private final Rect lastBounds = new Rect();
    private final boolean hasSetting, removable;

    public boolean showBody = true, shown;

    protected Table title, bodyCont, body;
    protected boolean isSetup;

    public FloatTable(String name){
        this(name, true);
    }

    public FloatTable(String name, boolean removable){
        this(name, true, removable);
    }

    public FloatTable(String name, boolean hasSetting, boolean removable){
        super(MinerVars.settings, name, true, true);

        if(name == null){
            throw new RuntimeException("FloatTable must have a name.");
        }

        this.hasSetting = hasSetting;
        this.removable = removable;

        title = new Table(black6);
        bodyCont = new Table();
        body = new Table();

        addSettings();
        shown = MinerVars.settings.getBool("floats." + name + ".shown");

        visibility = () -> !Vars.state.isMenu() && Vars.ui.hudfrag.shown && !Vars.ui.minimapfrag.shown();
        clicked(this::toFront);
    }

    @Override
    public final void addUI(){
        if(hasSetting && !shown){
            return;
        }

        if(!isSetup){
            setup();
        }

        rebuild();

        readPosition();
        readSize();

        keepInStage();

        Vars.ui.hudGroup.addChild(this);
        toFront();

        ResizeAdjuster.add(this);
        FloatManager.add(this);
    }

    @Override
    public final boolean remove(){
        FloatManager.remove(this);
        return super.remove();
    }

    protected void addSettings(){
        if(hasSetting){
            addSettings(MinerVars.ui.settings.ui.addCategory(name));
        }
    }

    protected void addSettings(MSettingTable uiSettings){
        uiSettings.checkPref("floats." + name + ".shown", true, b -> {
            shown = b;
            if(shown){
                addUI();
            }else{
                remove();
            }
        });
    }

    private void setup(){
        body.top();

        setupTitle();

        add(title).growX();
        row();
        add(bodyCont).grow();

        pack();

        isSetup = true;
    }

    public void rebuild(){
        rebuildBodyCont();
        rebuildBody(body);

        pack();
    }

    protected void setupTitle(){
        title.table(this::setupNameTable).growX();

        title.table(buttons -> {
            buttons.defaults().size(48f).growY().right();

            setupButtons(buttons);

            buttons.button(Icon.editSmall, MStyles.clearToggleAccentb, () -> {
                operate();
                UIUtils.showInfoToastAt(getX(Align.center), getTop(), "@miner-tools.operator.show-hint", 1f, Align.bottom);
            }).checked(b -> operating()).disabled(b -> !operable());

            RotatedImage image = new RotatedImage(Icon.downSmall, 180);
            buttons.button(Icon.downSmall, MStyles.clearToggleAccentb, () -> {
                toggleCont();
                image.rotate(showBody ? 0 : 1, 0.5f, Interp.pow2Out);
            }).with(b -> {
                b.replaceImage(image);
                ElementUtils.addTooltip(b, () -> showBody ? "@retract" : "@expand", true);
            });

            if(removable){
                buttons.button("x", MStyles.clearAccentt, this::removeManually);
            }

        }).growY().right();
    }

    protected void setupNameTable(Table table){
        table.add(Core.bundle.get("miner-tools.floats." + name, "unnamed")).padLeft(4f).growX().left();
    }

    protected void setupButtons(Table buttons){
    }

    protected void rebuildBody(Table body){
        body.clearChildren();
    }

    protected void removeManually(){
        if(hasSetting){
            MinerVars.settings.put("floats." + name + ".shown", false);
            UIUtils.showInfoToastAt(getX(Align.center), getTop() + 8, "@miner-tools.floats.reshow-hint", 2, Align.bottom);
        }
        remove();
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

        keepWithinStage();
    }

    @Override
    public void onResized(float deltaWidth, float deltaHeight){
        super.onResized(deltaWidth, deltaHeight);
        ElementUtils.getBoundsOnScene(this, lastBounds);
    }
}
