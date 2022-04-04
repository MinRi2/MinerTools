package MinerTools.ui;

import arc.graphics.*;
import arc.scene.style.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.TextButton.*;
import arc.scene.ui.TextField.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static mindustry.gen.Tex.*;
import static mindustry.ui.Styles.*;

public class MStyles{
    public static TextureRegionDrawable whiteuiRegion, transAccentDrawable;

    public static ButtonStyle clearToggleAccentb, logicVarTogglet, chatb;
    public static ImageButtonStyle rclearTransi;
    public static TextButtonStyle floatb;
    public static TextFieldStyle noneField;

    public static void load(){
        whiteuiRegion = (TextureRegionDrawable)Tex.whiteui;
        transAccentDrawable = (TextureRegionDrawable)(whiteuiRegion.tint(Tmp.c1.set(Pal.accent).a(0.55f)));

        clearToggleAccentb = new ButtonStyle(){{
           checked = transAccentDrawable;
        }};

        logicVarTogglet = new ButtonStyle(){{
            up = buttonOver;
            down = buttonDown;
            checked = buttonDown;
        }};

        chatb = new ButtonStyle(defaultb){{
            up = black6;
            over = flatOver;
            down = flatDown;
        }};

        rclearTransi = new ImageButtonStyle(clearTransi){{
            up = none;
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
