package MinerTools.ui;

import arc.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.game.*;
import mindustry.gen.*;
import static mindustry.content.Blocks.*;
import static mindustry.gen.Icon.*;
import static mindustry.ui.Styles.*;

public class MinerToolsTable extends Table{
    public static Seq<ScrollPane> panes = new Seq<>();

    private TeamsInfo teamInfos = new TeamsInfo();

    private Table memberTable;
    private int memberIndex = -1;
    private final Table[] membersTables = new Table[]{new TeamChanger(), new PlayersList(), new Schematics()};
    private final Drawable[] membersButtonsIcon = new Drawable[]{new TextureRegionDrawable(spawn.uiIcon), players, paste};
    public MinerToolsTable(){
        Events.on(EventType.WorldLoadEvent.class, e -> rebuild());
    }

    public void rebuild(){
        clear();

        table(t -> t.add(teamInfos)).right();

        row();

        table(members -> {
            members.table(member -> memberTable = member).fillX().growY();

            members.table(black3, buttons -> {
                for(int i = 0; i < membersButtonsIcon.length; i++){
                    int finalI = i;
                    buttons.button(membersButtonsIcon[i], clearTogglePartiali, 35, () -> setMemberIndex(finalI))
                    .update(b -> b.setChecked(memberIndex == finalI)).growY();

                    buttons.row();
                }

                buttons.button(Icon.none, clearTogglePartiali, 35, () -> setMemberIndex(-1))
                .update(b -> b.setChecked(memberIndex == -1)).growY();
            }).fillX().growY();

            membersRebuild();
        }).right();

        update(() -> {
            for(ScrollPane pane : panes){
                if(pane.hasScroll()){
                    Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                    if(result == null || !result.isDescendantOf(pane)){
                        Core.scene.setScrollFocus(null);
                    }
                }
            }
        });
    }

    private void membersRebuild(){
        memberTable.clear();

        if(memberIndex == -1) return;

        memberTable.table(t -> {
            t.add(membersTables[memberIndex]).fill().padRight(2f);
        }).fillY();
    }

    private void setMemberIndex(int index){
        memberIndex = index;
        membersRebuild();
    }
}
