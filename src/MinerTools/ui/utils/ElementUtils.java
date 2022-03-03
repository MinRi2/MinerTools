package MinerTools.ui.utils;

import arc.func.*;
import arc.input.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.ui.*;

public class ElementUtils{
    /**
     * 为ui元素添加提示（煎饼anuke）
     * @param element 需要添加提示的元素
     * @param text 提示的信息
     * @param isMobile 是否需要手机提示
     * @return 需要添加提示的元素
     */
    public static Element addTooltip(Element element, String text, boolean isMobile){
        var tip = new Tooltip(t -> t.background(Styles.black8).margin(4f).add(text).style(Styles.outlineLabel));
        tip.allowMobile = isMobile;
        element.addListener(tip);
        return element;
    }

    /**
     * 为ui元素添加提示（煎饼anuke）
     * @param element 需要添加提示的元素
     * @param cons 自定义的信息编辑
     * @param isMobile 是否需要手机提示
     * @return 需要添加提示的元素
     */
    public static Element addTooltip(Element element, Cons<Table> cons, boolean isMobile){
        var tip = new Tooltip(cons::get);
        tip.allowMobile = isMobile;
        element.addListener(tip);
        return element;
    }
}
