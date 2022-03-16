package MinerTools.ui;

import arc.util.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;

import static mindustry.Vars.ui;

public class MSettingsTable extends SettingsTable{

    public MSettingsTable(){
        ui.settings.resized(() -> {
            Log.info("Resize");
        });
    }
}
