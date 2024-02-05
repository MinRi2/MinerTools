package MinerTools.ui.tables.floats;

import MinerTools.*;
import MinerTools.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class MainTable extends FloatTable{
    public MainTable(){
        super("main", false);

        title.background(MStyles.transAccent);
    }

    @Override
    protected void addSettings(){
    }

    @Override
    protected void setupBody(Table body){
        body.background(Styles.black3);

        body.defaults().height(40f).growX();

        body.button("@miner-tools.settings", Icon.settingsSmall, MStyles.clearAccentt, () -> {
            MinerVars.ui.settingsDialog.show();
        });
    }
}
