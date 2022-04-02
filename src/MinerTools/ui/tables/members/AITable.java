package MinerTools.ui.tables.members;

import MinerTools.ai.*;
import MinerTools.ai.types.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class AITable extends MemberTable{
    private PlayerAI[] ais = new PlayerAI[]{new PlayerMinerAI()};
    private PlayerAI target;

    private Table displayTable = new Table(Styles.flatDown);

    public AITable(){
        super(Icon.android);

        rebuild();
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
                buttons.button(ai.icon, Styles.clearTogglePartiali, () -> setTarget(ai))
                .checked(b -> target == ai);
            }
        }).row();

        add(displayTable).fillX();
    }

    private void rebuildDisplayTable(){
        displayTable.clear();

        if(target != null) target.display(displayTable);
    }
}
