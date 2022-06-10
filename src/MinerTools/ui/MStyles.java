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
    public static TextureRegionDrawable whiteuiRegion, transAccentDrawable, clearFlatOver;

    public static ButtonStyle clearToggleAccentb, logicVarTogglet, chatb;
    public static ImageButtonStyle rclearTransi;
    public static TextButtonStyle clearPartial2t, clearToggleTranst;
    public static TextFieldStyle noneField;

    public static void load(){
        whiteuiRegion = (TextureRegionDrawable)Tex.whiteui;
        transAccentDrawable = (TextureRegionDrawable)(whiteuiRegion.tint(Tmp.c1.set(Pal.accent).a(0.55f)));
        clearFlatOver = (TextureRegionDrawable)whiteuiRegion.tint(Color.lightGray.cpy().a(0.45f));

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

        rclearTransi = new ImageButtonStyle(clearPartiali){{
            up = none;
        }};

        clearPartial2t = new TextButtonStyle(defaultt){{
            up = none;
            over = flatOver;
            down = flatDown;
        }};

        clearToggleTranst = new TextButtonStyle(defaultt){{
            up = whiteuiRegion.tint(Color.gray);
            over = flatOver;
            down = transAccentDrawable;
            checked = transAccentDrawable;
        }};

        noneField = new TextFieldStyle(defaultField){{
            background = none;
        }};
    }
}
