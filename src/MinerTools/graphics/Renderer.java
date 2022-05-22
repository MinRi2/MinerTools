package MinerTools.graphics;

import MinerTools.graphics.draw.build.*;
import MinerTools.graphics.draw.unit.*;
import MinerTools.graphics.renderer.*;
import MinerTools.graphics.renderer.build.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.ConstructBlock.*;

import static arc.Core.input;

public class Renderer{
    private static final Seq<BaseRender<?>> allRenderer = new Seq<>();

    public static void init(){
        allRenderer.addAll(
        new TurretRender().addDrawers(new TurretAlert()).addCameraDrawers(new TurretAmmoDisplay()),
        new UnitRender().addDrawers(new UnitAlert()).addCameraDrawers(new UnitInfoBar())
        );

        updateEnable();

        updateSettings();

        Events.run(Trigger.draw, () -> {
            Vec2 v = input.mouseWorld();
            Building select = Vars.world.build(World.toTile(v.x), World.toTile(v.y));
            if(select != null){
                drawSelect(select);
            }

            for(BaseRender<?> render : allRenderer){
                render.render();
            }
        });
    }

    public static void updateEnable(){
        for(BaseRender<?> render : allRenderer){
            render.updateEnable();
        }
    }

    public static void updateSettings(){
        for(BaseRender<?> render : allRenderer){
            render.updateSetting();
        }
    }

    public static float drawText(String text, float scl, float dx, float dy, Color color, int halign){
        Font font = Fonts.outline;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(scl);
        layout.setText(font, text);

        float height = layout.height;

        font.setColor(color);
        font.draw(text, dx, dy + layout.height + 1, halign);

        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);

        return height;
    }

    public static void drawSelect(Building building){
        if(building instanceof ConstructBuild c){
            constructInfo(c);
        }
    }

    /**
     * 构造中的建筑的耗材信息
     */
    public static void constructInfo(ConstructBuild c){
        if(c.team.core() != null){
            // BlockUnit之上
            Draw.z(Layer.flyingUnit + 0.1f);

            float scl = c.block.size / 8f / 2f / Scl.scl(1f);

            drawText(String.format("%.2f", c.progress * 100) + "%", scl, c.x, c.y + c.block.size * Vars.tilesize / 2f, Pal.accent, Align.center);

            float nextPad = 0f;
            for(int i = 0; i < c.current.requirements.length; i++){
                ItemStack stack = c.current.requirements[i];

                float dx = c.x - (c.block.size * Vars.tilesize) / 2f, dy = c.y - (c.block.size * Vars.tilesize) / 2f + nextPad;
                boolean hasItem = (1.0f - c.progress) * Vars.state.rules.buildCostMultiplier * stack.amount <= c.team.core().items.get(stack.item);

                nextPad += drawText(
                stack.item.emoji() + (int)(c.progress * Vars.state.rules.buildCostMultiplier * stack.amount) + "/" +
                (int)(Vars.state.rules.buildCostMultiplier * stack.amount) + "/" +
                UI.formatAmount(c.team.core().items.get(stack.item)),
                scl, dx, dy, hasItem ? Pal.accent : Pal.remove, Align.left);
                nextPad ++;
            }
        }
    }
}
