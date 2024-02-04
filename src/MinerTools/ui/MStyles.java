package MinerTools.ui;

import arc.graphics.*;
import arc.scene.style.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.TextButton.*;
import arc.scene.ui.TextField.*;
import arc.util.*;
import mindustry.graphics.*;

import static mindustry.gen.Tex.*;
import static mindustry.ui.Styles.*;

public class MStyles{
    public static TextureRegionDrawable whiteuiRegion, transAccent, transRed, clearFlatOver;

    public static ImageButtonStyle clearToggleAccentb, logicVarTogglet, chatb, rclearTransi;
    public static TextButtonStyle clearPartial2t, clearAccentt, clearToggleTranst;
    public static TextFieldStyle noneField;

    public static void load(){
        whiteuiRegion = (TextureRegionDrawable)whiteui;
        transAccent = getColoredRegion(Pal.accent, 0.55f);
        transRed = getColoredRegion(Color.red, 0.55f);
        clearFlatOver = getColoredRegion(Color.lightGray, 0.45f);

        clearToggleAccentb = new ImageButtonStyle(){{
            over = flatOver;
            down = transAccent;
            checked = transAccent;
            disabled = transRed;
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

        clearAccentt = new TextButtonStyle(clearPartial2t){{
            down = transAccent;
        }};

        clearToggleTranst = new TextButtonStyle(defaultt){{
            up = whiteuiRegion.tint(Color.gray);
            over = flatOver;
            down = transAccent;
            checked = transAccent;
        }};

        noneField = new TextFieldStyle(defaultField){{
            background = none;
        }};
    }

    public static TextureRegionDrawable getColoredRegion(Color color){
        return (TextureRegionDrawable)whiteuiRegion.tint(color);
    }

    public static TextureRegionDrawable getColoredRegion(Color color, float alpha){
        return (TextureRegionDrawable)whiteuiRegion.tint(Tmp.c1.set(color).a(alpha));
    }
}
