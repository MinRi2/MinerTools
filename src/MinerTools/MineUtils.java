package MinerTools;

import arc.math.*;
import arc.struct.*;
import mindustry.ai.types.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.turrets.ItemTurret.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.modules.*;

import static mindustry.Vars.*;

public class MineUtils{
    public static float fontScale = 0.75f;
    public static float imgSize = iconSmall * fontScale;

    public static int countMiner(Team team){
        return team.data().units.count(unit -> unit.controller() instanceof MinerAI);
    }

    public static int countPlayer(Team team){
        return Groups.player.count(player -> player.team() == team);
    }

    public static void rebuildBlocks(){
        if(!player.unit().canBuild()) return;

        int i = 0;
        for(BlockPlan block : player.team().data().blocks){
            if(Mathf.len(block.x - player.tileX(), block.y - player.tileY()) >= buildingRange) continue;
            if(++i > 511) break;
            player.unit().addBuild(new BuildPlan(block.x, block.y, block.rotation, content.block(block.block), block.config));
        }
    }

    public static void dropItems(){
        indexer.eachBlock(player.team(), player.x, player.y, itemTransferRange,
            build -> build.acceptStack(player.unit().item(), player.unit().stack.amount, player.unit()) > 0 && !(build.block instanceof CoreBlock || build.block instanceof ItemBridge ||build.block instanceof Autotiler ||build.block instanceof MassDriver),
            build -> Call.transferInventory(player, build)
        );
    }

}
