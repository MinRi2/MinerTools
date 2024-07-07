package MinerTools.ui.tables.members;

import MinerTools.ai.*;
import MinerTools.ai.types.*;
import MinerTools.ui.tables.MembersTable.*;
import arc.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class AITable extends MemberTable{
    private final PlayerAI[] ais = new PlayerAI[]{new PlayerMinerAI(), new PlayerFollowAI()};
    private final Table displayTable = new Table(Styles.black6);
    private PlayerAI target;

    public AITable(){
        super(Icon.android);

        /* 重构DisplayTable以适配ContentLoader */
        Events.on(EventType.ContentInitEvent.class, e -> {
            /* 延时1s执行,等待MinerVars.allOres的初始化 */
            Timer.schedule(this::rebuildDisplayTable, 1);
        });
    }

    @Override
    public void onSelected(){
        clear();

        table(Styles.black6, buttons -> {
            buttons.defaults().size(55f);

            for(PlayerAI ai : ais){
                buttons.button(ai.icon, Styles.clearNoneTogglei, 50f, () -> setTarget(ai))
                .checked(b -> target == ai);
            }
        }).left().row();

        add(displayTable).growX();
    }

    private void setTarget(PlayerAI ai){
        if(target == ai){
            target = null;
            BaseAI.resetController();
        }else{
            target = ai;
            target.requireUpdate();
        }

        rebuildDisplayTable();
    }

    private void rebuildDisplayTable(){
        displayTable.clear();

        if(target != null) target.display(displayTable);
    }
}
