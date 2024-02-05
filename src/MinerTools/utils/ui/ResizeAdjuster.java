package MinerTools.utils.ui;

import arc.*;
import arc.scene.*;
import arc.struct.*;
import mindustry.game.EventType.*;

/**
 * 当窗口重设大小时 按比例变动元素位置
 */
public class ResizeAdjuster{
    private static final ObjectSet<Element> elements = new ObjectSet<>();
    private static float lastSceneWidth, lastSceneHeight;

    static{
        lastSceneWidth = Core.scene.getWidth();
        lastSceneHeight = Core.scene.getHeight();

        Events.on(ResizeEvent.class, e -> {
            adjust();
        });
    }

    public static void add(Element element){
        elements.add(element);
    }

    private static void adjust(){
        float sceneWidth = Core.scene.getWidth();
        float sceneHeight = Core.scene.getHeight();

        float resizeScaleX = sceneWidth / lastSceneWidth;
        float resizeScaleY = sceneHeight / lastSceneHeight;

        for(Element element : elements){
            float x = element.x;
            float y = element.y;

            element.setPosition(x * resizeScaleX, y * resizeScaleY);
        }

        lastSceneWidth = sceneWidth;
        lastSceneHeight = sceneHeight;
    }
}
