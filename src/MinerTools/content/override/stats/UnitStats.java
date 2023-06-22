package MinerTools.content.override.stats;

import MinerTools.content.override.stats.MStats.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

public class UnitStats extends StatOverride<UnitType>{

    public UnitStats(){
        addValues(
        new StatAdder<>("itemMineSpeed"){
            @Override
            public boolean isValid(UnitType type){
                return type.mineTier >= 0 && type.mineSpeed > 0;
            }

            @Override
            public void override(Stats stats, Table table, UnitType type){
                table.table(t -> {
                    int index = 0;

                    for(Block block : getMineBlocks(type)){
                        t.image(block.uiIcon).size(Vars.iconSmall).padLeft(4f);

                        t.add(block.localizedName).color(Pal.lightishGray).padRight(5f);

                        float mineSpeed = getMineSpeed(type, block.itemDrop);

                        t.add(Strings.autoFixed(mineSpeed, 2) + StatUnit.perSecond.localized()).padRight(8f);

                        if(++index % 4 == 0) t.row();
                    }
                }).left();
            }
        }
        );
    }

    private static Seq<Block> getMineBlocks(UnitType type){
        return Vars.content.blocks().select(b ->
        b.itemDrop != null &&
        (b instanceof Floor f && (((f.wallOre && type.mineWalls) || (!f.wallOre && type.mineFloor))) ||
        (!(b instanceof Floor) && type.mineWalls)) &&
        b.itemDrop.hardness <= type.mineTier
        );
    }

    private static float getMineSpeed(UnitType type, Item item){
        return 60 * type.mineSpeed / (50.0f + (!type.mineHardnessScaling ? 15f : (item.hardness * 15.0f)));
    }

}
