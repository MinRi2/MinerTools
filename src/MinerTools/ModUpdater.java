package MinerTools;

import arc.*;
import arc.util.*;
import arc.util.serialization.*;
import mindustry.*;
import mindustry.mod.Mods.*;
import mindustry.ui.dialogs.*;

public class ModUpdater{
    private final VersionChecker checker;
    private final LoadedMod mod;
    private final String repo;

    public ModUpdater(String modName){
        this(modName, VersionChecker.defaultChecker);
    }

    public ModUpdater(String modName, VersionChecker checker){
        this.mod = Vars.mods.locateMod(modName);
        if(this.mod == null){
            throw new RuntimeException("Mod '" + modName + "' doesn't exit. Please check your modName parameter.");
        }

        repo = this.mod.getRepo();
        if(repo == null){
            throw new RuntimeException("Mod '" + modName + "' doesn't have repo.");
        }

        this.checker = checker;
    }

    public void checkUpdate(){
        String modVersion = mod.meta.version;

        Http.get(Vars.ghApi + "/repos/" + repo + "/releases/latest", res -> {
            var json = Jval.read(res.getResultAsString());
            String latestVersion = json.getString("tag_name").substring(1);

            Core.app.post(() -> {
                if(!checker.check(modVersion, latestVersion)){
                    confirmUpdate(latestVersion);
                }
            });
        }, e -> {
//            Log.err(e);
        });
    }

    private void confirmUpdate(String version){
        Vars.ui.showCustomConfirm("@updater.name",
        Core.bundle.format("@updater.info", mod.meta.version, version),
        "@updater.load", "@ok", () -> {
            Reflect.invoke(ModsDialog.class, Vars.ui.mods, "githubImportJavaMod", new Object[]{repo, null}, String.class, String.class);
        }, () -> {
        });
    }

    public interface VersionChecker{
        VersionChecker defaultChecker = (currentVersion, latestVersion) -> {
            String[] current = currentVersion.split("\\.");
            String[] latest = currentVersion.split("\\.");

            if(current.length != latest.length){
                return false;
            }

            for(int i = 0; i < current.length; i++){
                int currentPart;
                int latestPart;

                try{
                    currentPart = Integer.parseInt(current[i]);
                    latestPart = Integer.parseInt(latest[i]);
                }catch(Exception e){
                    Log.err("'@' or '@' cannot format into number", current[i], latest[i]);
                    return false;
                }

                if(currentPart >= latestPart){
                    continue;
                }

                return false;
            }

            return true;
        };

        boolean check(String currentVersion, String latestVersion);
    }
}
