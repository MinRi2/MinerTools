package MinerTools;

import MinerTools.ai.*;
import MinerTools.content.*;
import MinerTools.content.override.*;
import MinerTools.graphics.*;
import MinerTools.modules.*;
import arc.*;
import arc.input.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

import static MinerTools.MinerVars.desktop;
import static MinerTools.game.ConveyorUpdater.*;
import static MinerTools.input.ModBinding.updateConveyor;
import static MinerTools.utils.GameUtils.tryPanToController;
import static arc.Core.input;
import static mindustry.Vars.mobile;

public class MinerTools extends Mod{
    public MinerTools(){
        // mobile = true; // Only for debug
        Events.on(ContentInitEvent.class, e -> MContents.init());

        Events.on(ClientLoadEvent.class, e -> {
            new ModUpdater(MinerVars.modName).checkUpdate();

            MinerVars.init();

            MStats.init();

            Modules.init();

            MRenderer.init();

            Modules.load();
        });

        Events.run(Trigger.update, this::update);
    }

    public void update(){
        if((desktop && input.keyDown(updateConveyor)) || (mobile && enable)){
            tryUpdateConveyor();
        }

        if(desktop && input.alt() && input.keyTap(KeyCode.mouseLeft)){
            tryPanToController();
        }

        if(MinerVars.ui != null){
            MinerVars.ui.update();
        }

        BaseAI.updateController();
    }

}
