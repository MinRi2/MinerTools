package MinerTools.graphics;

import MinerTools.graphics.draw.build.*;
import MinerTools.graphics.draw.build.select.*;
import MinerTools.graphics.draw.unit.*;
import MinerTools.graphics.renderer.*;
import arc.*;
import arc.struct.*;
import mindustry.game.EventType.*;

public class Renderer{
    private static final Seq<BaseRender<?>> allRenderer = new Seq<>();

    public static void init(){
        allRenderer.addAll(
        new SelectRender()
            .addBuildDrawers(new ConstructBlockInfo(), new BuildSelect(), new ItemBridgeSelect()),
        new BuildRender()
            .addDrawers(new TurretAlert())
            .addCameraDrawers(new TurretAmmoDisplay(), new BuildStatus(), new UnitBuildInfo(), new BuildHealthBar()),
        new UnitRender()
            .addDrawers(new UnitAlert(), new EnemyIndicator())
            .addCameraDrawers(new UnitInfoBar())
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

}
