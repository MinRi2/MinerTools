package MinerTools.modules.SpawnerInfo;

import MinerTools.modules.SpawnerInfo.SpawnerInfo.*;
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
    private GroupStat ground;
    private GroupStat flyer;

    public void setGroupStats(GroupStat ground, GroupStat flyer){
        this.ground = ground;
        this.flyer = flyer;
    }

    public void setup(){
        uiGroup.touchable = Touchable.disabled;
        uiGroup.setFillParent(true);
        uiGroup.update(() -> {
            int currentWave = Vars.state.wave - 1;
            if(wave != currentWave){
                wave = currentWave;
                rebuild();
            }
        });

        Vars.ui.hudGroup.addChild(uiGroup);
        uiGroup.toBack();
    }

    public void load(){
        worldCenter.set(
        Vars.world.unitWidth() / 2f,
        Vars.world.unitHeight() / 2f
        );
        
        rebuild();
    }

    private void rebuild(){
        uiGroup.clear();

        setupGroupTables(ground);
        setupGroupTables(flyer);
    }

    private void setupGroupTables(GroupStat stat){
        for(SpawnerGroup group : stat.groups){
            SpawnerInfoTable table = new SpawnerInfoTable(group, stat.spawnGroups);
            uiGroup.addChild(table);
        }
    }

    class SpawnerInfoTable extends Table{
        private static final float length = Vars.tilesize * 5;

        private final SpawnerGroup group;
        private final Seq<SpawnGroup> spawnGroups;
        private final Vec2 position = new Vec2();

        public SpawnerInfoTable(SpawnerGroup group, Seq<SpawnGroup> spawnGroups){
            super((Drawable)null);

            this.group = group;
            this.spawnGroups = spawnGroups;

            initPosition(group.getCentroid());

            setup();
        }

        private void initPosition(Vec2 centroid){
            Vec2 v1 = Tmp.v1.set(worldCenter);
            Vec2 v2 = Tmp.v2.set(centroid);

            v2.sub(v1).setLength(length).inv().add(centroid);

            position.set(v2);
        }

        @Override
        public void draw(){
            Vec2 v3 = Core.input.mouseScreen(position.x, position.y);
            float startX = v3.x, startY = v3.y;

            Lines.stroke(3, Pal.accent);

            for(Vec2 pos : group.spawnerPos){
                Vec2 pv = Core.input.mouseScreen(pos.x, pos.y);

                Lines.line(startX, startY, pv.x, pv.y);
            }

            Draw.color();

            super.draw();
        }

        public void setup(){
            top().left();

            table(Styles.black3, this::setupSpawnerInfoTable).growX().row();

            table(Styles.black3, this::setupCounterTable).growX().row();

//            pane(Styles.noBarPane, this::setupDetailsTable)
//            .width(256).maxHeight(32 * 3.5f).scrollX(false);

            update(() -> {
                Vec2 v = Core.input.mouseScreen(position.x, position.y);

                setPosition(v.x, v.y, Align.top);
            });
        }

        private void setupSpawnerInfoTable(Table table){
            table.image(Blocks.spawn.uiIcon).size(32);
            table.add("x" + group.spawnerPos.size);
        }

        private void setupCounterTable(Table table){
            table.left();

            SpawnCounter counter = SpawnCounter.count(spawnGroups, group.spawnerPos, wave);

            table.table(null, t -> {
                t.left();

                t.add(Core.bundle.get("nextWave")).color(Pal.lightishGray).style(Styles.outlineLabel);
                t.add("" + (wave + 1)).color(Pal.accent).style(Styles.outlineLabel).padLeft(8);
            }).growX().row();

            table.table(null, unitTable -> {
                unitTable.left();

                unitTable.add(Core.bundle.get("totalUnits")).color(Pal.lightishGray).style(Styles.outlineLabel);

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

                t.add(Core.bundle.get("totalHealth")).color(Pal.lightishGray).style(Styles.outlineLabel);
                t.add("" + counter.totalHealth).color(Pal.accent).style(Styles.outlineLabel).padLeft(8);
            }).growX().row();

            table.table(null, t -> {
                t.left();

                t.image(Icon.commandRally).size(32);

                t.add(Core.bundle.get("totalShield")).color(Pal.lightishGray).style(Styles.outlineLabel);
                t.add("" + counter.totalShield).color(Pal.accent).style(Styles.outlineLabel).padLeft(8);
            }).growX().row();
        }

        private void setupDetailsTable(Table table){

        }

    }

}
