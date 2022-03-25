package MinerTools;

import MinerTools.core.*;
import MinerTools.ui.*;
import arc.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.payloads.*;

import static MinerTools.MinerFuncs.*;
import static MinerTools.MinerVars.*;
import static MinerTools.input.ModBinding.updateConveyor;
import static arc.Core.input;
import static mindustry.Vars.*;

public class MinerTools extends Mod{

    public MinerTools(){
        enableConsole = true;

        Events.on(EventType.ClientLoadEvent.class, e -> {
            MinerVars.init();

            Drawer.setEvents();
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            Timer.schedule(PowerInfo::load, 2f);

            showBannedInfo();
        });

        Events.run(Trigger.update, this::update);
    }

    public void update(){
        if((desktop && input.keyDown(updateConveyor)) || (mobile && enableUpdateConveyor)){
            tryUpdateConveyor();
        }
        PowerInfo.updateAll();
        mui.update();
    }
}