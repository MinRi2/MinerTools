package MinerTools.ui.tables.members;

import MinerTools.ui.tables.MembersTable.*;
import arc.scene.ui.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;

import static mindustry.Vars.*;
import static mindustry.gen.Call.sendChatMessage;
import static mindustry.gen.Icon.play;
import static mindustry.gen.Tex.whiteui;
import static mindustry.ui.Styles.*;

public class TeamChanger extends MemberTable{
    public static int minSkipWave = 0, maxSkipWave = 50;

    private int skipWave;

    public TeamChanger(){
        super(Icon.editor);
        rebuild();
    }

    private void rebuild(){
        table(black3, skipTable -> {
            skipTable.table(t -> {
                t.button(play, 36, () -> {
                    for(int i = 0; i < skipWave; i++){
                        logic.skipWave();
                    }
                });

                t.add().width(-1f).growX();

                t.label(() -> skipWave + "Wave").padLeft(2f).get().setAlignment(Align.center);
            }).fillX().row();
            skipTable.slider(minSkipWave, maxSkipWave, 1, 0, n -> skipWave = (int)n).growX();
        }).grow().row();

        table(black6, t -> {
            int i = 0;
            for(Team team : Team.baseTeams){
                ImageButton button = t.button(whiteui, clearNoneTogglei, 48, () -> {
                    if(net.client()){
                        if(player.admin){
                            sendChatMessage("/team " + team.id);
                        }
                    }else{
                        player.team(team);
                    }
                }).size(48).pad(3f).checked(b -> player.team() == team).get();

                button.getStyle().imageUpColor = team.color;

                if(++i % 3 == 0) t.row();
            }
        }).grow();
    }
}
