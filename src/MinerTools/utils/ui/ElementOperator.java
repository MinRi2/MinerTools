package MinerTools.utils.ui;

import MinerTools.ui.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

/**
 * 元素操作
 * 可以对元素进行的大小缩放和位置拖拽的缩放
 * @author minri2
 */
public class ElementOperator{
    // 原版可对齐的元素
    private static final Seq<Element> vanillaElements = new Seq<>();
    // 可操作的表
    private static final Seq<OperableTable> operableTables = new Seq<>();
    // 大小边框所占比例(中间是拖拽)
    private static final float resizeBorderRatio = 2f / 10f;

    public static boolean operating = false;

    // 操作目标元素
    private static Element target;
    private static PuppetElement puppet;
    private static @Nullable OperateCons consumer;
    private static OperatorBackground background;
    private static boolean initialized;
    private static int touchEdge;
    private static boolean dragMode, resizeMode;

    static{
        Events.on(ResetEvent.class, e -> getVanillaElements());
    }

    private ElementOperator(){
    }

    /**
     * 获取原版可对齐的元素
     */
    private static void getVanillaElements(){
        vanillaElements.clear();

        Group hudGroup = Vars.ui.hudGroup;

        Element mainStack = Reflect.get(Vars.ui.hudfrag.blockfrag, "mainStack"),
        wavesTable = hudGroup.find("waves"),
        minimap = hudGroup.find("minimap"),
        position = hudGroup.find("position"),
        coreInfo = Reflect.get(Vars.ui.hudfrag, "coreItems");

        Element coreInfoWrapper = coreInfo.parent;

        vanillaElements.addAll(mainStack, wavesTable, minimap, position, coreInfoWrapper);
    }

    private static void init(){
        background = new OperatorBackground();
        puppet = new OperatorPuppet();

        background.add(puppet);

        puppet.addListener(new InputListener(){
            float lastX, lastY;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                Vec2 v = puppet.localToStageCoordinates(Tmp.v1.set(x, y));
                lastX = v.x;
                lastY = v.y;

                updateEdge(x, y);

                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer){
                Vec2 v = puppet.localToStageCoordinates(Tmp.v1.set(x, y));

                float deltaX = v.x - lastX;
                float deltaY = v.y - lastY;

                if(dragMode){
                    updateDragMode(deltaX, deltaY);
                }else if(resizeMode){
                    updateResizeMode(deltaX, deltaY);
                }

                lastX = v.x;
                lastY = v.y;
            }
        });
        background.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                Vec2 scenePos = background.localToStageCoordinates(Tmp.v1.set(x, y));

                OperableTable operableTable = operableTables.find(o -> {
                    if(!o.visible || !o.hasParent()){
                        return false;
                    }

                    Vec2 pos = o.stageToLocalCoordinates(Tmp.v2.set(scenePos));
                    return ElementUtils.isOverlays(o, pos.x, pos.y);
                });

                if(operableTable != null && operableTable != target){
                    operableTable.operate();
                }else if(Core.scene.hit(scenePos.x, scenePos.y, false) == background){
                    // Scene will make touch focus on background. So hide next frame.
                    Core.app.post(ElementOperator::hide);
                }

                return true;
            }
        });

        initialized = true;
    }

    /**
     * 操作元素
     * @param element 操作的元素
     */
    public static void operate(Element element){
        operate(element, null);
    }

    /**
     * 操作元素
     * @param element 操作的元素
     * @param operateCons 操作传感
     */
    public static void operate(Element element, @Nullable OperateCons operateCons){

        if(!initialized){
            init();
        }

        if(!operable(element)){
            return; // throw an error?
        }

        if(target != null){
            consumer.onReleased();
        }

        target = element;
        consumer = operateCons;

        puppet.setTarget(target);

        show();
    }

    private static boolean operable(Element element){
        return element != null && element.visible && element.hasParent();
    }

    private static void show(){
        if(operating){
            return;
        }

        operating = true;
        background.show();

        MUI.showInfoToastAt(target.getX(Align.center), target.getTop(), "@miner-tools.operator.show-hint", 1f, Align.bottom);
    }

    private static void hide(){
        if(!operating){
            return;
        }

        target = null;
        operating = false;
        background.hide();
    }

    // x,y: Local coordinate.
    private static void updateEdge(float x, float y){
        float width = puppet.getWidth(), height = puppet.getHeight();
        float borderX = width * resizeBorderRatio, borderY = height * resizeBorderRatio;

        dragMode = resizeMode = false;
        touchEdge = 0;

        if(x >= borderX && x <= width - borderX
        && y >= borderY && y <= height - borderY){
            dragMode = true;
            touchEdge = Align.center;
            return;
        }

        resizeMode = true;

        if(x < borderX){
            touchEdge |= Align.left;
        }else if(x > width - borderX){
            touchEdge |= Align.right;
        }

        if(y < borderY){
            touchEdge |= Align.bottom;
        }else if(y > height - borderY){
            touchEdge |= Align.top;
        }
    }

    private static void updateDragMode(float deltaX, float deltaY){
        puppet.moveBy(deltaX, deltaY);

        if(consumer != null){
            if(consumer.keepInStage){
                puppet.keepInStage();
            }

            consumer.onDragged(deltaX, deltaY);
        }

        updateDragAlign();
    }

    // TODO
    private static void updateDragAlign(){
    }

    private static void updateResizeMode(float deltaX, float deltaY){
        boolean keepInStage = consumer != null && consumer.keepInStage;

        float width = puppet.getWidth();
        float height = puppet.getHeight();

        float minWidth = puppet.getMinWidth();
        float minHeight = puppet.getMinHeight();

        float maxWidth = puppet.getScene().getWidth();
        float maxHeight = puppet.getScene().getHeight();

        float x = puppet.x;
        float y = puppet.y;

        if(Align.isLeft(touchEdge)){
            if(width - deltaX < minWidth) deltaX = -(minWidth - width);
            if(keepInStage && x + deltaX < 0) deltaX = -x;
            width -= deltaX;
            x += deltaX;
        }
        if(Align.isBottom(touchEdge)){
            if(height - deltaY < minHeight) deltaY = -(minHeight - height);
            if(keepInStage && y + deltaY < 0) deltaY = -y;
            height -= deltaY;
            y += deltaY;
        }
        if(Align.isRight(touchEdge)){
            if(width + deltaX < minWidth) deltaX = minWidth - width;
            if(keepInStage && x + width + deltaX > maxWidth)
                deltaX = maxWidth - x - width;
            width += deltaX;
        }
        if(Align.isTop(touchEdge)){
            if(height + deltaY < minHeight) deltaY = minHeight - height;
            if(keepInStage && y + height + deltaY > maxHeight)
                deltaY = maxHeight - y - height;
            height += deltaY;
        }

        puppet.setBounds(x, y, width, height);

        target.invalidateHierarchy();

        if(consumer != null){
            consumer.onResized(deltaX, deltaY);
        }

        updateResizeAlign();
    }

    // TODO
    private static void updateResizeAlign(){

    }

    private static class OperatorPuppet extends PuppetElement{
        private static void drawPuppet(float x, float y, float width, float height){
            float halfWidth = width / 2, halfHeight = height / 2;
            float halfX = x + halfWidth, halfY = y + halfHeight;

            // 单边宽高
            float borderWidth = width * resizeBorderRatio, borderHeight = height * resizeBorderRatio;

            float halfXAxes = (width - borderWidth) / 2;
            float halfYAxes = (height - borderHeight) / 2;

            // 边框
            Drawf.dashRect(Pal.accent, x, y, width, borderHeight);
            Drawf.dashRect(Pal.accent, x, y, borderWidth, height);
            Drawf.dashRect(Pal.accent, x + halfXAxes * 2, y, borderWidth, height);
            Drawf.dashRect(Pal.accent, x, y + halfYAxes * 2, width, borderHeight);

            // 填充
            Draw.color(Pal.accent, 0.3f);
            Fill.rect(halfX, halfY, width, height);

            // 四方的小图标提示
            float size = 16;
            Draw.color(Color.green, 1f);

            TextureRegion dragRegion = Icon.add.getRegion();
            Draw.rect(dragRegion, halfX, halfY, size, size);

            TextureRegion upRegion = Icon.up.getRegion();
            TextureRegion downRegion = Icon.down.getRegion();
            TextureRegion leftRegion = Icon.left.getRegion();
            TextureRegion rightRegion = Icon.right.getRegion();

            Draw.rect(upRegion, halfX, halfY + halfYAxes, size, size);
            Draw.rect(downRegion, halfX, halfY - halfYAxes, size, size);
            Draw.rect(rightRegion, halfX + halfXAxes, halfY, size, size);
            Draw.rect(leftRegion, halfX - halfXAxes, halfY, size, size);

            // 角落的小图标提示
            float degree = Mathf.atan2(width, height) * Mathf.radiansToDegrees;
            Draw.rect(rightRegion, halfX + halfXAxes, halfY + halfYAxes, size, size, degree); // ↗
            Draw.rect(rightRegion, halfX + halfXAxes, halfY - halfYAxes, size, size, -degree); // ↘
            Draw.rect(rightRegion, halfX - halfXAxes, halfY + halfYAxes, size, size, 180 - degree); // ↖
            Draw.rect(rightRegion, halfX - halfXAxes, halfY - halfYAxes, size, size, degree - 180); // ↙

            Draw.reset();
        }

        @Override
        public void draw(){
            super.draw();

            drawPuppet(x, y, width, height);
        }

    }

    private static class OperatorBackground extends Table{

        public OperatorBackground(){
            super(Styles.black5);

            touchable = Touchable.enabled;
            setFillParent(true);
        }

        private static void drawOperableTable(float x, float y, float width, float height){
            float halfWidth = width / 2, halfHeight = height / 2;
            float halfX = x + halfWidth, halfY = y + halfHeight;

            // 边框
            Drawf.dashRect(Pal.lightishGray, x, y, width, height);

            // 填充
            Draw.color(Pal.lightishGray, 0.3f);
            Fill.rect(halfX, halfY, width, height);

            Draw.reset();
        }

        private static void drawVanillaElement(float x, float y, float width, float height){
            // 边框
            Drawf.dashRect(Color.sky, x, y, width, height);

            Draw.reset();
        }

        @Override
        public void draw(){
            super.draw();

            for(OperableTable table : operableTables){
                if(table.operable()){
                    Vec2 pos = ElementUtils.getOriginPosition(table, Tmp.v1);
                    stageToLocalCoordinates(pos);

                    drawOperableTable(pos.x, pos.y, table.getWidth(), table.getHeight());
                }
            }

            for(Element element : vanillaElements){
                if(operable(element)){
                    Vec2 pos = ElementUtils.getOriginPosition(element, Tmp.v1);
                    stageToLocalCoordinates(pos);

                    drawVanillaElement(pos.x, pos.y, element.getWidth(), element.getHeight());
                }
            }
        }

        public void show(){
            // Showing
            if(hasParent()){
                return;
            }

            Core.scene.add(this);
        }

        public void hide(){
            if(!hasParent()){
                return;
            }

            remove();
        }
    }

    public static class OperateCons{
        public final boolean alizable;
        public final boolean keepInStage;

        public OperateCons(boolean alizable, boolean keepInStage){
            this.alizable = alizable;
            this.keepInStage = keepInStage;
        }

        public boolean alizable(){
            return alizable;
        }

        public void onDragged(float deltaX, float deltaY){
        }

        public void onResized(float deltaX, float deltaY){
        }

        public void onSnapped(Element snap, Align align){
        }

        public void onReleased(){
        }
    }

    public static class OperableTable extends Table{
        private final OperateCons cons;

        public OperableTable(boolean alizable){
            this(alizable, true);
        }

        public OperableTable(boolean alizable, boolean keepInStage){
            cons = new OperateCons(alizable, keepInStage){
                @Override
                public boolean alizable(){
                    return super.alizable() && OperableTable.this.visible;
                }

                @Override
                public void onDragged(float deltaX, float deltaY){
                    OperableTable.this.onDragged(deltaX, deltaY);
                }

                @Override
                public void onResized(float deltaX, float deltaY){
                    OperableTable.this.onResized(deltaX, deltaY);
                }

                @Override
                public void onReleased(){
                    OperableTable.this.onReleased();
                }

                @Override
                public void onSnapped(Element snap, Align align){
                    OperableTable.this.onSnapped(snap, align);
                }
            };

            operableTables.add(this);
        }

        public void operate(){
            ElementOperator.operate(this, cons);
        }

        public boolean operable(){
            return ElementOperator.operable(this);
        }

        protected void onDragged(float x, float y){
        }

        protected void onResized(float width, float height){
        }

        protected void onReleased(){
        }

        protected void onSnapped(Element snap, Align align){
        }

        public boolean operating(){
            return target == this;
        }
    }
}
