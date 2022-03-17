package MinerTools.ui.utils;

import arc.func.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.ui.*;

public class ElementUtils{
    /**
     * 为ui元素添加提示（煎饼anuke）
     * @param element 需要添加提示的元素
     * @param text 提示的信息
     * @param allowMobile 是否需要手机提示
     * @return 需要添加提示的元素
     */
    public static Element addTooltip(Element element, String text, boolean allowMobile){
        return addTooltip(element, t -> t.background(Styles.black8).margin(4f).add(text).style(Styles.outlineLabel), allowMobile);
    }

    /**
     * 为ui元素添加提示（煎饼anuke）
     * @param element 需要添加提示的元素
     * @param cons 自定义的信息编辑
     * @param allowMobile 是否需要手机提示
     * @return 需要添加提示的元素
     */
    public static Element addTooltip(Element element, Cons<Table> cons, boolean allowMobile){
        var tip = new Tooltip(cons){
            @Override
            protected void setContainerPosition(Element element, float x, float y){
                this.targetActor = element;
                Vec2 pos = element.localToStageCoordinates(Tmp.v1.set(0, 0));
                container.pack();
                container.setPosition(pos.x, pos.y, Align.top);
                container.setOrigin(0, element.getHeight());
            }
        };
        tip.allowMobile = allowMobile;
        element.addListener(tip);
        return element;
    }
}
