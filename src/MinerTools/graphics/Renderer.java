package MinerTools.graphics;

import MinerTools.graphics.draw.build.*;
import MinerTools.graphics.draw.build.select.*;
import MinerTools.graphics.draw.player.*;
import MinerTools.graphics.draw.unit.*;
import MinerTools.graphics.renderer.*;
import arc.*;
import arc.graphics.gl.*;
import arc.struct.*;
import mindustry.game.EventType.*;

import static mindustry.Vars.renderer;

public class Renderer{
    public static FrameBuffer effectBuffer;

    private static final Seq<BaseRender<?>> allRenderer = new Seq<>();

    public static void init(){
        MShaders.init();

        effectBuffer = renderer.effectBuffer;

        allRenderer.addAll(
        new SelectRender()
            .addBuildDrawers(new ConstructBlockInfo(), new BuildSelect(), new BridgeLinkedList()),

        new BuildRender()
            .addDrawers(new TurretAlert())
            .addCameraDrawers(
            new TurretAmmoDisplay(), new BuildStatus(),
            new UnitBuildInfo(), new BuildHealthBar(),
            new UnitAssemblerInfo(), new OverdriveZone()),

        new UnitRender()
            .addDrawers(new UnitAlert(), new EnemyIndicator())
            .addCameraDrawers(new UnitInfoBar()),
        new PlayerRender()
            .addDrawers(new PlayerRange())
            .addCameraDrawers(new PayloadDropHint())
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
