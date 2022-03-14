package MinerTools;

import mindustry.input.*;

import static mindustry.Vars.*;

public class MinerVars{
    public static boolean desktop;

    public static float fontScale = 0.75f;
    public static float imgSize = iconSmall * fontScale;

    public static boolean enableUpdateConveyor;

    public static void init(){
        desktop = control.input instanceof DesktopInput;
    }
}
