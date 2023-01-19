package MinerTools.modules.SpawnerInfo;

import MinerTools.modules.*;
import MinerTools.ui.settings.*;
import MinerTools.utils.*;
import MinerTools.utils.PanRequests.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;

public class SpawnerViewer extends SettingModule{
    private final Seq<SpawnerGroup> viewGroups = new Seq<>();
    public boolean isViewing = false;
    public boolean preview;
    public boolean alwaysShow;

    public SpawnerViewer(SettingModule parent){
        super(parent, "spawnerViewer");
    }

    public void clear(){
        viewGroups.clear();
    }

    public void addViewGroup(Seq<SpawnerGroup> groups){
        viewGroups.addAll(groups);
    }

    @Override
    public void load(){
        Events.run(Trigger.draw, () -> {
            if(isEnable() && (alwaysShow || isViewing)){
                drawSpawnerPos();
            }
        });
    }

    @Override
    public void setSettings(MSettingTable settings){
        super.setSettings(settings);

        settings.checkPref(name + ".preview", true, b -> {
            preview = b;
        }).change();

        settings.checkPref(name + ".alwaysShow", true, b -> {
            alwaysShow = b;
        }).change();
    }
    
    public void worldLoad(){
        if(!isEnable()){
            return;
        }
        
        if(preview){
            Timer.schedule(this::view, 0.75f);
        }
    }

    public void view(){
        isViewing = true;

        PanRequests.markCamera();

        for(SpawnerGroup group : viewGroups){
            PanRequests.panWait(group.getCentroid(), 0.55f);
        }

        PanRequest request = PanRequests.panToLastMark();
        request.setFinished(() -> {
            isViewing = false;
        });
    }

    private void drawSpawnerPos(){
        float radius = 12f + Mathf.sin(Time.globalTime / 12) * 2;
        for(SpawnerGroup group : viewGroups){
            for(Vec2 v : group.spawnerPos){
                drawSpawnerTarget(v.x, v.y, radius);
            }
        }
    }

    private void drawSpawnerTarget(float x, float y, float radius){
        Draw.z(Layer.overlayUI);

        Draw.rect(Blocks.spawn.fullIcon, x, y, 8f, 8f);

        Draw.color(Pal.accent, 0.6f);
        Draw.mixcol(Pal.remove, 0.4f + Mathf.absin(Time.globalTime, 6f, 0.28f));

        Lines.stroke(2f);
        Lines.poly(x, y, 4, radius, 90f);

        Lines.stroke(1f);
        Lines.poly(x, y, 4, radius * 1.5f, 90f);

        Lines.spikes(x, y, radius * 1.5f, 8f, 4, 90f);

        Draw.reset();
    }

}
