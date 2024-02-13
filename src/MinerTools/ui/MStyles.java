package MinerTools.ui;

import MinerTools.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.TextButton.*;
import arc.scene.ui.TextField.*;
import mindustry.graphics.*;

import static MinRi2.ModCore.ui.MinTex.*;
import static mindustry.gen.Tex.*;
import static mindustry.ui.Styles.*;

public class MStyles{
    // TODO: 注解处理器生成
    public static TextureRegionDrawable accentGrayGran;

    public static ImageButtonStyle clearToggleAccentb, logicVarTogglet, chatb, rclearTransi;
    public static TextButtonStyle clearPartial2t, clearAccentt, toggleTranst, settingt;
    public static TextFieldStyle noneField;

    public static void load(){
        loadModSprites();

        clearToggleAccentb = new ImageButtonStyle(){{
            over = flatOver;
            down = transAccent;
            checked = transAccent;
            imageDisabledColor = Pal.darkerGray;
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

        toggleTranst = new TextButtonStyle(defaultt){{
            up = getColoredRegion(Color.gray);
            over = flatOver;
            down = transAccent;
            checked = transAccent;
        }};

        settingt = new TextButtonStyle(toggleTranst){{
            up = getColoredRegion(Pal.lightishGray, 0.3f);
            checked = getColoredRegion(Pal.lightishGray);
        }};

        noneField = new TextFieldStyle(defaultField){{
            background = none;
        }};
    }

    public static void loadModSprites(){
        accentGrayGran = new TextureRegionDrawable(getSprite("gradient"));
    }

    private static TextureRegion getSprite(String name){
        return Core.atlas.find(MinerVars.modName + "-" + name);
    }
}
