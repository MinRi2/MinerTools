package MinerTools.ui;

import arc.scene.ui.Button.*;

import static mindustry.gen.Tex.*;
import static mindustry.ui.Styles.*;

public class MStyles{
    public static ButtonStyle logicVarTogglet, chatb;

    public static void load(){
        logicVarTogglet = new ButtonStyle(defaultt){{
            up = buttonOver;
            down = buttonDown;
            checked = buttonDown;
        }};

        chatb = new ButtonStyle(defaultb){{
            up = black6;
            over = flatOver;
            down = flatDown;
        }};
    }
}
