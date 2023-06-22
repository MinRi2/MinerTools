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

    private int index = 0;

    public ScriptButtons(){
        super("scriptButtons");
    }

    @Override
    protected void setupCont(Table cont){
        super.setupCont(cont);

        cont.table(Styles.black3, buttons -> {
            buttons.defaults().minSize(64f).growX();

            if(Vars.mobile){
                addScriptButton(buttons, "stopBuilding", Icon.hammer, Styles.emptyTogglei, () -> {
                    Vars.control.input.isBuilding = !Vars.control.input.isBuilding;
                }, b -> Vars.control.input.isBuilding);

                addScriptButton(buttons, "updateConveyor", Icon.distribution, Styles.emptyTogglei, () -> {
                    MinerFunc.enableUpdateConveyor = !MinerFunc.enableUpdateConveyor;
                }, b -> MinerFunc.enableUpdateConveyor);

                addScriptButton(buttons, "observerMode", Icon.pause, Styles.emptyTogglei,
                    ObserverMode::toggle, b -> ObserverMode.isObserving());
            }

            addScriptButton(buttons, "wayzerObserver", Icon.eyeSmall, MStyles.rclearTransi, () -> {
                Call.sendChatMessage("/ob");
            });

            addScriptButton(buttons, "quickVoteGameOver", Icon.trashSmall, Styles.clearNonei, () -> {
                Vars.ui.showConfirm("@confirm", "@confirmvotegameover", () -> {
                    Call.sendChatMessage("/vote gameover");
                    Call.sendChatMessage("1");
                });
            });
        }).growX();
    }

    private Cell<Button> addScriptButton(Table table, String name, Drawable icon, ButtonStyle style,
                                 Runnable runnable){
        return addScriptButton(table, name, icon, style, runnable, null);
    }

    private Cell<Button> addScriptButton(Table table, String name, Drawable icon, ButtonStyle style,
                                 Runnable runnable, Boolf<Button> checked){
        Cell<Button> cell = table.button(button -> {
            button.image(icon).grow();

            button.add(bundle.get(bundleName + "." + name)).padLeft(4f);
        }, style, runnable);

        if(checked != null){
            cell.checked(checked);
        }

        if(index % 4 == 0){
            table.row();
        }

        return cell;
    }

}
