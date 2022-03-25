package MinerTools.ui;

import arc.scene.ui.Button.*;
import arc.scene.ui.TextButton.*;
import arc.scene.ui.TextField.*;

import static mindustry.gen.Tex.*;
import static mindustry.ui.Styles.*;

public class MStyles{
    public static ButtonStyle logicVarTogglet, chatb;
    public static TextButtonStyle floatb;
    public static TextFieldStyle noneField;

    public static void load(){
        logicVarTogglet = new ButtonStyle(defaultb){{
            up = buttonOver;
            down = buttonDown;
            checked = buttonDown;
        }};

        chatb = new ButtonStyle(defaultb){{
            up = black6;
            over = flatOver;
            down = flatDown;
        }};

        floatb = new TextButtonStyle(defaultt){{
            up = none;
            over = flatOver;
            down = flatDown;
        }};

        noneField = new TextFieldStyle(defaultField){{
            background = none;
        }};
    }
}
