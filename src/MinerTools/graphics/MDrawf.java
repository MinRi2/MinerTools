package MinerTools.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.ui.*;

public class MDrawf{


    public static Rect drawText(String text, float scl, float dx, float dy){
        return drawText(text, scl, dx, dy, Align.center);
    }

    public static Rect drawText(String text, float scl, float dx, float dy, int align){
        return drawText(text, scl, dx, dy, Color.white, align);
    }

    public static Rect drawText(String text, float scl, float dx, float dy, Color color){
        return drawText(text, scl, dx, dy, color, Align.center);
    }

    public static Rect drawText(String text, float scl, float dx, float dy, Color color, int align){
        Font font = Fonts.outline;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(scl);
        layout.setText(font, text);

        float ry = dy + layout.height + 1;
        float width = layout.width, height = layout.height;

        font.setColor(color);
        font.draw(text, dx, ry, align);

        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);

        return Tmp.r1.set(dx, ry, width, height);
    }

    public static float drawProgressBar(float x, float y, float x2, float y2, float progress, float backBarStroke, float backBarAlpha, Color backBarColor, float barStroke, float barAlpha, Color barColor){
        /* Background */
        Lines.stroke(backBarStroke, backBarColor);
        Draw.alpha(backBarAlpha);
        Lines.line(x, y, x2, y2);

        float barX = x + (x2 - x) * progress;

        /* Process bar */
        Lines.stroke(barStroke, barColor);
        Draw.alpha(barAlpha);
        Lines.line(x, y, barX, y2);

        Draw.reset();

        return barX;
    }

}
