package MinerTools.ui;

import arc.graphics.*;
import arc.scene.style.*;
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

    public static ImageButtonStyle clearToggleAccentb, logicVarTogglet, chatb, rclearTransi;
    public static TextButtonStyle clearPartial2t, clearToggleTranst;
    public static TextFieldStyle noneField;

    public static void load(){
        whiteuiRegion = (TextureRegionDrawable)Tex.whiteui;
        transAccentDrawable = (TextureRegionDrawable)(whiteuiRegion.tint(Tmp.c1.set(Pal.accent).a(0.55f)));
        clearFlatOver = (TextureRegionDrawable)whiteuiRegion.tint(Color.lightGray.cpy().a(0.45f));

        clearToggleAccentb = new ImageButtonStyle(){{
            checked = transAccentDrawable;
        }};

        logicVarTogglet = new ImageButtonStyle(){{
            up = buttonOver;
            down = buttonDown;
            checked = buttonDown;
        }};

        chatb = new ImageButtonStyle(defaulti){{
            up = black6;
            over = flatOver;
            down = flatDown;
        }};

        rclearTransi = new ImageButtonStyle(clearNonei){{
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

    public static TextureRegionDrawable getColoredRegion(Color color){
        return (TextureRegionDrawable)whiteuiRegion.tint(color);
    }
}
