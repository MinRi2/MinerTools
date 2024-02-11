package MinerTools.ui.settings;

import MinerTools.*;
import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class MSettingsDialog extends Dialog{
    private final Table buttons;
    private final ScrollPane pane;
    public MSettingsMenu menu;

    public MSettingsDialog(){
        menu = MinerVars.ui.settings;

        buttons = new Table();
        pane = new ScrollPane(menu, Styles.noBarPane);

        buttons.bottom();

        setFillParent(true);
        addCloseButton();

        top();

        menu.addUI();

        resized(this::rebuild);
        shown(this::rebuild);
    }

    private void rebuild(){
        clearChildren();

        menu.rebuild();

        Cell<?> cell = stack(pane, buttons).grow();

        if(Core.scene.getWidth() >= 1920f / 2f){
            cell.maxWidth(Core.scene.getWidth() * 3f / 4f);
        }
    }

    @Override
    public void addCloseButton(){
        buttons.defaults().size(width, 64f);
        buttons.button("@back", Icon.left, this::hide).size(width, 64f);

        closeOnBack();
    }
}
