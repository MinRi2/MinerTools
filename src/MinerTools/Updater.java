package MinerTools;

import arc.*;
import arc.util.*;
import arc.util.serialization.*;
import mindustry.mod.Mods.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.*;

public class Updater{
    private static LoadedMod mod;
    private static String repo;

    public static void checkUpdate(){
        mod = mods.locateMod("miner-tools");
        repo = mod.getRepo();

        Http.get(ghApi + "/repos/" + repo + "/releases/latest", res -> {
            var json = Jval.read(res.getResultAsString());
            String version = json.getString("tag_name").substring(1);

            if(version.equals(mod.meta.version)) return;

            ui.showCustomConfirm("@miner-tools.updater.name",
            Core.bundle.format("miner-tools.updater.info", mod.meta.version, version),
            "@miner-tools.updater.load", "@ok", () -> {
                Reflect.invoke(ModsDialog.class, ui.mods, "githubImportJavaMod", new Object[]{repo, null}, String.class, String.class);
            }, () -> {});
        }, e -> {});
    }
}
