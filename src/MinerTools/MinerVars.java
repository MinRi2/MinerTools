package MinerTools;

import MinerTools.ui.Dialogs.*;
import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.distribution.Conveyor.*;
import mindustry.world.blocks.distribution.Junction.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;
import org.jetbrains.annotations.*;

import static arc.Core.input;
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
