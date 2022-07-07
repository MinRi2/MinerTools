package MinerTools.ui.tables;

import MinerTools.ui.tables.members.*;
import arc.*;
import arc.scene.style.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.layout.*;
import mindustry.content.*;
import mindustry.gen.*;

import static mindustry.Vars.ui;
import static mindustry.ui.Styles.*;

public class MinerToolsTable extends MembersTable{
    private final TeamsInfo teamInfo = new TeamsInfo();
    private boolean showInfo = true;

    public MinerToolsTable(){
        addMember(new TeamChanger(), new PlayerList(), new SchematicList(), new AITable());

        setup();
    }

    @Override
    public void addUI(){
        ui.hudGroup.fill(t -> {
            t.top().right().name = "miner-tools";
            t.visible(() -> ui.hudfrag.shown && !ui.minimapfrag.shown());

            t.add(this);

            Table minimap = ui.hudGroup.find("minimap/position");
            Table overlayMarker = ui.hudGroup.find("overlaymarker");
            t.update(() -> {
                if(t.getPrefWidth() + overlayMarker.getPrefWidth() + minimap.getPrefWidth() > Core.scene.getWidth()){
                    t.translation.x = 0;
                    t.translation.y = -minimap.getPrefHeight();
                }else{
                    t.translation.x = -minimap.getPrefWidth();
                    t.translation.y = 0;
                }
            });
        });
    }

    public void setup(){
        clear();

        table(t -> {
            t.collapser(teamInfo, true, () -> showInfo).right();

            ImageButtonStyle style = new ImageButtonStyle(clearTogglei){{
                up = black3;
            }};

            t.button(new TextureRegionDrawable(Items.copper.uiIcon), style, 35, () -> showInfo = !showInfo)
            .update(b -> b.setChecked(showInfo)).growY();
        }).right();

        row();

        rebuildMembers();

        add(members);
    }

    @Override
    public void rebuildMembers(){
        table(t -> {
            t.table(memberManager::setContainer).fillX().top();

            t.table(black3, buttons -> {

                int index = 0;
                for(MemberTable member : memberManager.getMemberTables()){

                    if(!member.canShown()) continue;

                    buttons.button(member.icon, clearTogglePartiali, 35, () -> setMember(member))
                    .checked(b -> memberManager.isShown(member)).growY();

                    if(++index % 2 == 0) buttons.row();
                }

                buttons.button(Icon.none, clearTogglePartiali, 35, () -> setMember(null))
                .checked(b -> !memberManager.isShown()).growY();
            }).fillX().growY();
        }).right();
    }
}
