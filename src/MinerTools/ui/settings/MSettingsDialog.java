package MinerTools.ui.settings;

import MinerTools.*;
import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;

public class MSettingsDialog extends Dialog{
    private final Table buttons;
    public MSettingsMenu menu;

    public MSettingsDialog(){
        menu = MinerVars.ui.settings;
        buttons = new Table();

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
        stack(menu, buttons).width(Core.scene.getWidth() * (3f / 4f)).growY();
    }

    @Override
    public void addCloseButton(){
        buttons.defaults().size(width, 64f);
        buttons.button("@back", Icon.left, this::hide).size(width, 64f);

        closeOnBack();
    }
}
