package MinerTools.graphics;

import MinerTools.graphics.draw.build.*;
import MinerTools.graphics.draw.unit.*;
import MinerTools.graphics.renderer.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.pooling.*;
import mindustry.game.EventType.*;
import mindustry.ui.*;

public class Renderer{
    private static final Seq<BaseRender<?>> allRenderer = new Seq<>();

    public static void init(){
        allRenderer.addAll(
        new SelectRender().addBuildDrawers(new ConstructBlockInfo(), new BuildSelect()),
        new BuildRender().addDrawers(new TurretAlert()).addCameraDrawers(new TurretAmmoDisplay(), new BuildStatus()),
        new UnitRender().addDrawers(new UnitAlert(), new EnemyIndicator()).addCameraDrawers(new UnitInfoBar())
        );

        updateEnable();

        updateSettings();

        Events.run(Trigger.draw, () -> {
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

    public static float drawText(String text, float scl, float dx, float dy, Color color, int align){
        Font font = Fonts.outline;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(scl);
        layout.setText(font, text);

        float height = layout.height;

        font.setColor(color);
        font.draw(text, dx, dy + layout.height + 1, align);

        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);

        return height;
    }
}
