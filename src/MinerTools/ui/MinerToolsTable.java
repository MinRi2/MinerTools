package MinerTools.ui;

import MinerTools.ui.tables.*;
import MinerTools.ui.tables.members.Schematics;
import MinerTools.ui.tables.members.*;
import arc.*;
import arc.scene.style.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;

import static MinerTools.MinerVars.desktop;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class MinerToolsTable extends Table implements Addable{
    private final TeamsInfo teamInfo = new TeamsInfo();
    private boolean infoShown = true;

    private final MemberManager memberManager = new MemberManager();

    private MemberTable shown;
    private final Seq<MemberTable> members = Seq.with(new TeamChanger(), new PlayersList(), new Schematics(), new AITable());

    public MinerToolsTable(){
        rebuild();

        Events.on(EventType.WorldLoadEvent.class, e -> memberManager.setMember(shown));
    }

    public void addUI(){
        ui.hudGroup.fill(t -> {
            t.top().right().name = "miner-tools";
            t.visible(() -> ui.hudfrag.shown && !ui.minimapfrag.shown());

            t.add(this);

            Table minimap = ui.hudGroup.find("minimap/position");
            Table overlaymarker = ui.hudGroup.find("overlaymarker");
            t.update(() -> {
                if(t.getPrefWidth() + overlaymarker.getPrefWidth() + minimap.getPrefWidth() > Core.scene.getWidth()){
                    t.translation.x = 0;
                    t.translation.y = -minimap.getPrefHeight();
                }else{
                    t.translation.x = -minimap.getPrefWidth();
                    t.translation.y = 0;
                }
            });
        });
    }

    private void rebuild(){
        clear();

        table(t -> {
            t.collapser(teamInfo, true, () -> infoShown).right();

            ImageButtonStyle style = new ImageButtonStyle(clearTogglePartiali){{
                up = black3;
            }};

            t.button(new TextureRegionDrawable(Items.copper.uiIcon), style, 35, () -> infoShown = !infoShown)
            .update(b -> b.setChecked(infoShown)).growY();
        }).right();

        row();

        table(t -> {
            t.table(memberManager::setContainer).fillX().top();

            t.table(black3, buttons -> {
                for(MemberTable member : members){

                    if(!desktop && member.desktopOnly) continue;
                    if(!mobile && member.mobileOnly) continue;

                    buttons.button(member.icon, clearTogglePartiali, 35, () -> setMember(member))
                    .checked(b -> shown == member).growY();

                    buttons.row();
                }

                buttons.button(Icon.none, clearTogglePartiali, 35, () -> setMember(null))
                .checked(b -> shown == null).growY();
            }).fillX().growY();
        }).right();
    }

    public void setMember(MemberTable member){
        shown = member;
        memberManager.setMember(member);
    }

    public static class MemberManager{
        private Table container;

        public void setContainer(Table container){
            this.container = container;
        }

        public void setMember(MemberTable member){
            container.clear();

            if(member != null){
                container.add(member).fill().padRight(2f);
                member.memberRebuild();
            }
        }
    }
}
