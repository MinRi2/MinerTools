package MinerTools.ui;

import arc.scene.ui.TextButton.*;

import static mindustry.gen.Tex.*;
import static mindustry.ui.Styles.*;

public class MStyles{
    public static TextButtonStyle logicVarsTogglet;

    public static void load(){
        logicVarsTogglet = new TextButtonStyle(defaultt){{
            checked = buttonDown;
            down = buttonDown;
            up = buttonOver;
        }};
    }
}
