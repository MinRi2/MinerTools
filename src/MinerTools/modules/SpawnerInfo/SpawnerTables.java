package MinerTools.modules.SpawnerInfo;

import MinerTools.ui.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class SpawnerTables{
    private final Group uiGroup = new WidgetGroup();
    private final Vec2 worldCenter = new Vec2();
    private int wave;
    private Seq<SpawnGroup> spawnGroups;
    private Seq<SpawnerGroup> groundGroups;
    private Seq<SpawnerGroup> flyerGroups;

    public void setGroups(Seq<SpawnerGroup> groundGroups, Seq<SpawnerGroup> flyerGroups){
        this.groundGroups = groundGroups;
        this.flyerGroups = flyerGroups;
    }

    public void setup(){
        uiGroup.touchable = Touchable.childrenOnly;
        uiGroup.setFillParent(true);
        uiGroup.update(() -> {
            int currentWave = Vars.state.wave - 1;
            if(wave != currentWave){
                wave = currentWave;
                rebuild();
            }
        });

        Vars.ui.hudGroup.addChild(uiGroup);
    }

    public void load(){
        spawnGroups = Vars.state.rules.spawns;

        worldCenter.set(
        Vars.world.unitWidth() / 2f,
        Vars.world.unitHeight() / 2f
        );
    }

    private void rebuild(){
        uiGroup.clear();

        setupGroupTables(groundGroups);
        setupGroupTables(flyerGroups);
    }

    private void setupGroupTables(Seq<SpawnerGroup> groups){
        for(SpawnerGroup group : groups){
            SpawnerInfoTable table = new SpawnerInfoTable(group);
            uiGroup.addChild(table);
        }
    }

    class SpawnerInfoTable extends Table{
        private static final float length = Vars.tilesize * 5;

        private final SpawnerGroup group;
        private final Vec2 centroid;

        public SpawnerInfoTable(SpawnerGroup group){
            super((Drawable)null);

            this.group = group;
            centroid = group.getCentroid();

            setup();
        }

        @Override
        public void draw(){
            Vec2 v1 = Tmp.v1.set(centroid);
            Vec2 v2 = Tmp.v2.set(worldCenter);

            v2.sub(v1).setLength(length).add(worldCenter);

            Vec2 v3 = Core.input.mouseScreen(v2.x, v2.y);
            float startX = v3.x, startY = v3.y;

            Lines.stroke(3, Pal.accent);

            for(int pos : group.spawnerPos.items){
                float x = Point2.x(pos) * Vars.tilesize,
                y = Point2.y(pos) * Vars.tilesize;

                Vec2 pv = Core.input.mouseScreen(x, y);

                Lines.line(startX, startY, pv.x, pv.y);
            }

            Draw.color();

            super.draw();
        }

        public void setup(){
            touchable = Touchable.childrenOnly;

            top().left();

            table(Styles.black3, this::setupSpawnerInfoTable).growX().row();

            table(Styles.black3, this::setupCounterTable).growX().row();

            pane(Styles.noBarPane, this::setupDetailsTable)
            .width(256).maxHeight(32 * 3.5f).scrollX(false);

            update(() -> {
                Vec2 v = Core.input.mouseScreen(centroid.x, centroid.y);

                setPosition(v.x, v.y, Align.top);
            });
        }

        private void setupSpawnerInfoTable(Table table){
            table.touchable = Touchable.disabled;

            table.image(Blocks.spawn.uiIcon).size(32);
            table.add("x" + group.spawnerPos.size);
        }

        private void setupCounterTable(Table table){
            table.left();

            SpawnCounter counter = SpawnCounter.count(spawnGroups, group.spawnerPos, wave);

            table.table(null, t -> {
                t.left();

                t.add(Core.bundle.get("nextWave")).color(Pal.lightishGray).style(Styles.outlineLabel);
                t.add("" + wave + 1).color(Pal.accent).style(Styles.outlineLabel).padLeft(8);
            }).growX().row();

            table.table(null, unitTable -> {
                unitTable.left();

                unitTable.add("总单位").color(Pal.lightishGray).style(Styles.outlineLabel);

                unitTable.table(null, container -> {
                    var map = counter.units;

                    if(map.isEmpty()){
                        container.image(Icon.noneSmall).size(32);
                        return;
                    }

                    final int[] i = {0};
                    Vars.content.units().each(unitType -> {
                        int count = map.get(unitType);

                        if(count == 0){
                            return;
                        }

                        container.add(new NumImage(unitType.uiIcon, count))
                        .size(32).padLeft(8);

                        if(++i[0] % 4 == 0){
                            container.row();
                        }
                    });
                }).padLeft(8);
            }).growX().row();

            table.table(null, t -> {
                t.left();

                t.image(Icon.defense).size(32);

                t.add(Core.bundle.get("nextWave")).color(Pal.lightishGray).style(Styles.outlineLabel);
                t.add("" + counter.totalHealth).color(Pal.accent).style(Styles.outlineLabel).padLeft(8);
            }).growX().row();

            table.table(null, t -> {
                t.left();

                t.image(Icon.commandRally).size(32);

                t.add(Core.bundle.get("nextWave")).color(Pal.lightishGray).style(Styles.outlineLabel);
                t.add("" + counter.totalShield).color(Pal.accent).style(Styles.outlineLabel).padLeft(8);
            }).growX().row();
        }

        private void setupDetailsTable(Table table){

        }

    }

}
