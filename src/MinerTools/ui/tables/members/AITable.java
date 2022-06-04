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
    private PlayerAI[] ais = new PlayerAI[]{new PlayerMinerAI(), new PlayerFollowAI()};
    private PlayerAI target;

    private Table displayTable = new Table(Styles.black6);

    public AITable(){
        super(Icon.android);

        rebuild();

        /* 重构DisplayTable以适配ContentLoader */
        Events.on(EventType.ContentInitEvent.class, e -> {
            /* 延时1s执行,等待MinerVars.allOres的初始化 */
            Timer.schedule(this::rebuildDisplayTable, 1);
        });
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

    private void rebuild(){
        table(Styles.black6, buttons -> {
            buttons.defaults().size(55f);

            for(PlayerAI ai : ais){
                buttons.button(ai.icon, Styles.clearPartiali, 50f, () -> setTarget(ai))
                .checked(b -> target == ai);
            }
        }).right().row();

        add(displayTable).growX();
    }

    private void rebuildDisplayTable(){
        displayTable.clear();

        if(target != null) target.display(displayTable);
    }
}
