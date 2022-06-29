package MinerTools.ui.tables.members;

import MinerTools.*;
import MinerTools.ui.BlockFinder.*;
import MinerTools.ui.tables.MembersTable.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;

public class FinderTable extends MemberTable{
    private int chunkIndex;
    private BlockChunk showed;

    private Seq<BlockChunk> chunks;

    public FinderTable(){
        super(Icon.edit);

        button("Last", () -> {
            chunkIndex--;
            checkChunk();
            paneToChunk();
        });

        button("Next", () -> {
            chunkIndex++;
            checkChunk();
            paneToChunk();
        });

        Events.run(Trigger.draw, () -> {
            if(showed != null){
                Draw.z(Layer.blockBuilding + 1f);

                for(Building building : showed.getBlock(Blocks.titaniumConveyor)){
                    float s = Mathf.sin(Time.time / 36f);

                    float size = building.block.size * Vars.tilesize / 2f;

                    Drawf.dashRect(building.team.color, showed.rect);

                    Draw.color(building.team.color, 0.85f + s / 3);
                    Fill.rect(building.x, building.y, size, size);

                    Draw.reset();
                }
            }
        });
    }

    private void checkChunk(){
        chunks = MinerVars.finder.findBlock(Blocks.titaniumConveyor);

        if(chunks.isEmpty()){
            return;
        }

        chunkIndex = Mathf.clamp(chunkIndex, 0, chunks.size - 1);
        showed = chunks.get(chunkIndex);
    }

    private void paneToChunk(){
        Core.camera.position.set(showed.centerPos());

        if(Vars.control.input instanceof DesktopInput input){
            input.panning = true;
        }
    }

}
