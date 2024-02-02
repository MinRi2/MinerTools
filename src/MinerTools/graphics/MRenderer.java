package MinerTools.graphics;

import MinerTools.graphics.draw.build.*;
import MinerTools.graphics.draw.build.select.*;
import MinerTools.graphics.draw.player.*;
import MinerTools.graphics.draw.unit.*;
import MinerTools.graphics.provider.*;
import arc.*;
import arc.struct.*;
import mindustry.game.EventType.*;

public class MRenderer{
    private static final Seq<DrawerProvider<?>> providers = new Seq<>();

    public static void init(){
        MShaders.init();

        BuildProvider buildProvider = new BuildProvider();
        UnitProvider unitProvider = new UnitProvider();
        PlayerProvider playerProvider = new PlayerProvider();
        SelectProvider selectProvider = new SelectProvider();

        buildProvider.addGlobalDrawers(new TurretAlert())
        .addCameraDrawers(
        new TurretAmmoDisplay(), new BuildStatus(),
        new UnitBuildInfo(), new BuildHealthBar(),
        new UnitAssemblerInfo(), new OverdriveZone()
        );
        unitProvider.addGlobalDrawers(new UnitAlert(), new EnemyIndicator()).addCameraDrawers(new UnitInfoBar());
        playerProvider.addGlobalDrawers(new PlayerRange()).addLocalDrawers(new PayloadDropHint());
        selectProvider.addBuildDrawers(new ConstructBlockInfo(), new BuildSelect(), new BridgeLinkedList());

        providers.addAll(buildProvider, unitProvider, playerProvider, selectProvider);

        updateEnable();

        updateSettings();

        Events.run(Trigger.draw, () -> {
            for(DrawerProvider<?> provider : providers){
                provider.provide();
                provider.drawShader();
            }
        });
    }

    public static void updateEnable(){
        for(DrawerProvider<?> provider : providers){
            provider.updateEnable();
        }
    }

    public static void updateSettings(){
        for(DrawerProvider<?> provider : providers){
            provider.updateSetting();
        }
    }

}
