package MinerTools.ui.utils;

import arc.func.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.ui.*;

import static arc.Core.bundle;

public class ElementUtils{
    public static Cell<?> getCell(Element element){
        Group parent = element.parent;

        if(parent instanceof Table table){
            return table.getCell(element);
        }

        return null;
    }

    public static Element addTooltip(Element element, String text, boolean allowMobile){
        return addTooltip(element, text, Align.top, allowMobile);
    }

    public static Element addTooltip(Element element, String text, int align, boolean allowMobile){
        return addTooltip(element, t -> t.background(Styles.black8).margin(4f).add(text).style(Styles.outlineLabel), align, allowMobile);
    }

    public static Element addTooltip(Element element, Cons<Table> cons, boolean allowMobile){
        return addTooltip(element, cons, Align.top, allowMobile);
    }

    /**
     * 为ui元素添加提示
     * @param element 需要添加提示的元素
     * @param cons 自定义的信息编辑
     * @param align 对齐位置
     * @param allowMobile 是否需要手机提示
     * @return 需要添加提示的元素
     */
    public static Element addTooltip(Element element, Cons<Table> cons, int align, boolean allowMobile){
        var tip = new Tooltip(cons){
            {
                targetActor = element;

                container.update(() -> {
                    if(!targetActor.hasMouse()){
                        hide();
                    }
                });
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Element toActor){}

            @Override
            protected void setContainerPosition(Element element, float x, float y){
                Vec2 pos = element.localToStageCoordinates(Tmp.v1.set(0, 0));

                container.pack();
                container.setPosition(pos.x, pos.y, align);
                container.setOrigin(0, element.getHeight());
            }
        };
        tip.allowMobile = allowMobile;
        element.addListener(tip);
        return element;
    }

    public static void addIntroductionFor(Group group, String bundleName, boolean allowMobile){
        for(Element child : group.getChildren()){
            /* add some tooltips */
            if(child.name != null){
                addTooltip(child, bundle.get(bundleName + "." + child.name), allowMobile);
            }
        }
    }

    /**
     * hit但是无视是否可点击
     */
    public static Element hitUnTouchable(Group group, float x, float y){
        Vec2 point = Tmp.v1;
        Element[] childrenArray = group.getChildren().items;
        for(int i = group.getChildren().size - 1; i >= 0; i--){
            Element child = childrenArray[i];
            if(!child.visible) continue;

            child.parentToLocalCoordinates(point.set(x, y));

            Element hit;

            if(child instanceof Group g){
                hit = hitUnTouchable(g, point.x, point.y);
            }else{
                hit = hitUnTouchable(child, point.x, point.y);
            }

            if(hit != null) return hit;
        }
        return null;
    }


    /**
     * hit但是无视是否可点击
     */
    public static Element hitUnTouchable(Element e, float x, float y){
        return x >= e.translation.x && x < e.getWidth() + e.translation.x && y >= e.translation.y && y < e.getHeight() + e.translation.y ? e : null;
    }
}
