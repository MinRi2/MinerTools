package MinerTools.ui;

import arc.scene.ui.Button.*;

import static mindustry.gen.Tex.*;
import static mindustry.ui.Styles.defaultt;

public class MStyles{
    public static ButtonStyle logicVarTogglet;

    public static void load(){
        logicVarTogglet = new ButtonStyle(defaultt){{
            checked = buttonDown;
            down = buttonDown;
            up = buttonOver;
        }};
    }
}
