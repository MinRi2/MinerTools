package MinerTools.graphics;

import MinerTools.graphics.draw.*;
import MinerTools.graphics.draw.build.*;
import MinerTools.graphics.draw.unit.*;
import MinerTools.interfaces.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.ConstructBlock.*;

import static arc.Core.input;
import static mindustry.Vars.*;

public class Drawer{
    private static final Seq<BuildDrawer<? extends Building>> allBuildDrawers = Seq.with(new TurretAlert(), new TurretAmmoDisplay());
    private static final Seq<UnitDrawer> allUnitDrawers = Seq.with(new UnitAlert(), new EnemyIndicator());

    private static Seq<BuildDrawer<? extends Building>> enableBuildDrawers;
    private static Seq<UnitDrawer> enableUnitDrawers;

    private static final Seq<Drawable<?>> drawers = new Seq<>();

    private static boolean drawBuilding, drawUnit;

    public static void init(){
        drawers.addAll(allBuildDrawers).addAll(allUnitDrawers);

        updateEnable();

        updateSettings();

        Events.run(Trigger.draw, () -> {
            Vec2 v = input.mouseWorld();
            Building select = world.build(World.toTile(v.x), World.toTile(v.y));
            if(select != null){
                drawSelect(select);
            }

            drawEntity();
        });
    }

    public static void updateSettings(){
        readSettings();
    }

    public static void readSettings(){
        for(var drawer : drawers){
            drawer.readSetting();
        }
    }

    public static void updateEnable(){
        enableBuildDrawers = allBuildDrawers.select(Drawable::enabled);
        enableUnitDrawers = allUnitDrawers.select(Drawable::enabled);

        drawBuilding = allBuildDrawers.any();
        drawUnit = enableUnitDrawers.any();
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

    public static void drawEntity(){
        Seq<TeamData> activeTeams = state.teams.getActive();
        for(TeamData data : activeTeams){
            if(drawBuilding) drawBuilding(data);
            if(drawUnit) drawUnit(data);
        }
    }

    public static void drawSelect(Building building){
        if(building instanceof ConstructBuild c){
            constructInfo(c);
        }
    }

    /* Draw Buildings */
    private static void drawBuilding(TeamData data){
        if(data.buildings != null){
            for(var drawer : enableBuildDrawers){
                if(drawer.isValid()) drawer.init();
            }

            for(Building building : data.buildings){
                for(var drawer : enableBuildDrawers){
                    if(drawer.isValid()) drawer.tryDraw(building);
                }
            }
        }
    }

    /* Draw Units */
    private static void drawUnit(TeamData data){
        for(var drawer : enableUnitDrawers){
            if(drawer.isValid()) drawer.init();
        }

        for(Unit unit : data.units){
            for(var drawer : enableUnitDrawers){
                if(drawer.isValid()) drawer.tryDraw(unit);
            }
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

            drawText(String.format("%.2f", c.progress * 100) + "%", scl, c.x, c.y + c.block.size * tilesize / 2f, Pal.accent, Align.center);

            float nextPad = 0f;
            for(int i = 0; i < c.current.requirements.length; i++){
                ItemStack stack = c.current.requirements[i];

                float dx = c.x - (c.block.size * tilesize) / 2f, dy = c.y - (c.block.size * tilesize) / 2f + nextPad;
                boolean hasItem = (1.0f - c.progress) * state.rules.buildCostMultiplier * stack.amount <= c.team.core().items.get(stack.item);

                nextPad += drawText(
                stack.item.emoji() + (int)(c.progress * state.rules.buildCostMultiplier * stack.amount) + "/" +
                (int)(state.rules.buildCostMultiplier * stack.amount) + "/" +
                UI.formatAmount(c.team.core().items.get(stack.item)),
                scl, dx, dy, hasItem ? Pal.accent : Pal.remove, Align.left);
                nextPad ++;
            }
        }
    }
}
