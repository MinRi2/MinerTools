package MinerTools.ui.tables.floats;

import MinerTools.game.*;
import MinerTools.ui.*;
import MinerTools.utils.*;
import arc.func.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static arc.Core.bundle;

public class ScriptButtons extends FloatTable{
    private static final String bundleName = "miner-tools.buttons.tooltips";

    private Table buttons;
    private int index = 0;

    public ScriptButtons(){
        super("scriptButtons");
    }

    @Override
    protected void setupCont(Table cont){
        super.setupCont(cont);

        cont.table(Styles.black3, buttons -> {
            this.buttons = buttons;
        }).growX();

        setupButtons();
    }

    private void setupButtons(){
        buttons.defaults().minSize(80f, 64f).growX();

        if(Vars.mobile){
            addScriptButton("stopBuilding", Icon.hammer, Styles.clearNoneTogglei, () -> {
                Vars.control.input.isBuilding = !Vars.control.input.isBuilding;
            }, b -> !Vars.control.input.isBuilding);

            addScriptButton("updateConveyor", Icon.distribution, Styles.clearNoneTogglei, () -> {
                MinerFunc.enableUpdateConveyor = !MinerFunc.enableUpdateConveyor;
            }, b -> MinerFunc.enableUpdateConveyor);

            addScriptButton("observerMode", Icon.pause, Styles.clearNoneTogglei,
            MobileObserverMode::toggle, b -> MobileObserverMode.isObserving());
        }

        addScriptButton("wayzerObserver", Icon.eyeSmall, MStyles.rclearTransi, () -> {
            Call.sendChatMessage("/ob");
        });

        addScriptButton("quickVoteGameOver", Icon.trashSmall, Styles.clearNonei, () -> {
            Vars.ui.showConfirm("@confirm", "@confirmvotegameover", () -> {
                Call.sendChatMessage("/vote gameover");
                Call.sendChatMessage("1");
            });
        });
    }

    private Cell<Button> addScriptButton(String name, Drawable icon, ButtonStyle style,
                                         Runnable runnable){
        return addScriptButton(name, icon, style, runnable, null);
    }

    private Cell<Button> addScriptButton(String name, Drawable icon, ButtonStyle style,
                                         Runnable runnable, Boolf<Button> checked){
        Cell<Button> cell = buttons.button(button -> {
            button.left();

            button.image(icon);

            button.add(bundle.get(bundleName + "." + name))
            .padLeft(4f).wrap().growX().right();
        }, style, runnable);

        if(checked != null){
            cell.checked(checked);
        }

        if(++index % 2 == 0){
            buttons.row();
        }

        return cell;
    }

}
