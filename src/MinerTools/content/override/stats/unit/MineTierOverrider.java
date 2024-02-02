package MinerTools.content.override.stats.unit;

import MinerTools.*;
import MinerTools.content.override.stats.StatOverrider.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

public class MineTierOverrider extends StatAdder<UnitType>{
    public MineTierOverrider(){
        super("mineTier");

        clearStatValues = true;
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

    @Override
    public boolean isValid(UnitType type){
        return type.mineTier >= 0;
    }

    @Override
    public void override(Stats stats, Table table, UnitType type){
        table.row();
        table.table(t -> {
            int index = 0;

            for(Block block : getMineBlocks(type)){
                t.table(Styles.grayPanel, oreTable -> {
                    oreTable.left();

                    oreTable.image(block.uiIcon).scaling(Scaling.fit).size(Vars.iconXLarge).padLeft(4f);

                    oreTable.table(info -> {
                        info.defaults().left();

                        info.add(block.localizedName).color(Pal.lightishGray);

                        info.row();

                        info.image(block.itemDrop.uiIcon).scaling(Scaling.fit).size(Vars.iconSmall).padTop(2f);
                    }).padLeft(4f);

                    float mineSpeed = getMineSpeed(type, block.itemDrop);
                    oreTable.add(Strings.autoFixed(mineSpeed, 2) + StatUnit.perSecond.localized() + MinerVars.modSymbol).labelAlign(Align.right).padLeft(8f).growX();
                }).growX().margin(4f).pad(4f);

                if(++index % 2 == 0) t.row();
            }
        }).growX();
    }
}
