package MinerTools.graphics.draw.build.select;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.distribution.ItemBridge.*;

public class ItemBridgeSelect extends BuildDrawer<ItemBridgeBuild>{
    private static final Seq<ItemBridgeBuild> currentBridges = new Seq<>();

    public ItemBridgeSelect(){
        super(block -> block instanceof ItemBridge);
    }

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("itemBridgeLinksShow");
    }

    @Override
    protected void draw(ItemBridgeBuild build){
        ItemBridge block = (ItemBridge)build.block;

        Draw.z(Layer.overlayUI);

        drawSelect(block, build, false);

        Draw.reset();

        currentBridges.clear();
    }

    private static ItemBridgeBuild getLinkBuild(ItemBridge block, ItemBridgeBuild build){
        int linkPos = build.link;

        if(linkPos == -1) return null;

        Tile linkTile = Vars.world.tile(linkPos);

        if(!block.linkValid(build.tile, linkTile)) return null;

        return (ItemBridgeBuild)(linkTile.build);
    }

    private static Seq<ItemBridgeBuild> getDumpBridges(ItemBridgeBuild bridge){
        Seq<ItemBridgeBuild> returnBridges = new Seq<>();

        for(Building building : bridge.proximity){
            if(building instanceof ItemBridgeBuild other && !currentBridges.contains(other) && bridge.canDump(other, null)){
                returnBridges.add(other);
            }
        }

        return returnBridges;
    }

    private static void drawSelect(ItemBridge block, ItemBridgeBuild build){
        drawSelect(block, build, true);
    }

    private static void drawSelect(ItemBridge block, ItemBridgeBuild build, boolean checkCons){
        if(checkCons && currentBridges.contains(build)) return;

        currentBridges.add(build);

        ItemBridgeBuild linkBuild = getLinkBuild(block, build);

        if(linkBuild == null){
            Seq<ItemBridgeBuild> bridges = getDumpBridges(build);

            if(bridges.isEmpty()) return;

            for(ItemBridgeBuild bridge : bridges){
                drawSelect(block, bridge, false);
            }
            return;
        }

        drawInput(block, build, linkBuild);

        drawSelect(block, linkBuild);
    }

    private static void drawInput(ItemBridge block, ItemBridgeBuild build, ItemBridgeBuild linkBuild){
        Tmp.v2.trns(build.angleTo(linkBuild), 2f);
        float tx = build.x, ty = build.y;
        float ox = linkBuild.x, oy = linkBuild.y;
        float alpha = Math.abs(100 - (Time.time * 2f) % 100f) / 100f;
        float x = Mathf.lerp(ox, tx, alpha);
        float y = Mathf.lerp(oy, ty, alpha);

        int rel = linkBuild.tile.absoluteRelativeTo(linkBuild.tileX(), linkBuild.tileY());

        //draw "background"
        Draw.color(Pal.gray);
        Lines.stroke(2.5f);
        Lines.square(ox, oy, 2f, 45f);
        Lines.stroke(2.5f);
        Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

        //draw foreground colors
        Draw.color(Pal.place);
        Lines.stroke(1f);
        Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

        Lines.square(ox, oy, 2f, 45f);
        Draw.mixcol(Draw.getColor(), 1f);
        Draw.color();
        Draw.rect(block.arrowRegion, x, y, rel * 90);
        Draw.mixcol();
    }

}
