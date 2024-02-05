package MinerTools.utils;

import mindustry.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.input.*;

import static arc.Core.*;

public class GameUtils{
    public static String colorMark(Team team){
        return "[#" + team.color + "]";
    }

    public static void tryPanToController(){
        Unit unit = Units.closestOverlap(Vars.player.team(), input.mouseWorldX(), input.mouseWorldY(), 5f, u -> !u.isLocal());
        if(unit != null && unit.controller() instanceof LogicAI ai && ai.controller != null){
            ((DesktopInput)Vars.control.input).panning = true;
            camera.position.set(ai.controller);
            Fx.spawn.at(ai.controller);
        }
    }

    public static int playerCount(Team team){
        return team.data().players.size;
    }
}

