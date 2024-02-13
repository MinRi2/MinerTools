package MinerTools.ui.tables.floats;

import MinRi2.ModCore.ui.*;
import MinerTools.*;
import MinerTools.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class MainTable extends FloatTable{
    public MainTable(){
        super("main", false, false);

        title.background(MinTex.transAccent);
    }

    @Override
    protected void rebuildBody(Table body){
        super.rebuildBody(body);

        body.background(Styles.black3);

        body.defaults().height(40f).growX();

        body.button("@miner-tools.settings", Icon.settingsSmall, MStyles.clearAccentt, () -> {
            MinerVars.ui.settingsDialog.show();
        });
    }
}
