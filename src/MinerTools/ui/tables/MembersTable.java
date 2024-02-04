package MinerTools.ui.tables;

import MinerTools.*;
import MinerTools.interfaces.*;
import MinerTools.ui.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.ui.*;

public class MembersTable extends Table{
    private final Seq<MemberTable> members = new Seq<>();
    MembersBuilder builder;
    private Table container;
    private MemberTable showMember;

    public MembersTable(){
        this(MembersBuilder.defaultBuilder);
    }

    public MembersTable(MembersBuilder builder){
        this.builder = builder;
    }

    public void addMember(MemberTable... members){
        this.members.addAll(members);
    }

    public void setContainer(Table container){
        this.container = container;
    }

    public void setMember(MemberTable member){
        container.clear();
        showMember = member;
        if(member != null){
            container.add(member).grow().padRight(2.0f);
            member.memberRebuild();
        }
    }

    public boolean memberShowing(){
        return showMember != null;
    }

    public boolean memberShowing(MemberTable memberTable){
        return showMember == memberTable;
    }

    public Seq<MemberTable> getMembers(){
        return members;
    }

    public void rebuildMembers(){
        builder.build(this);
    }

    public interface MembersBuilder extends TableBuilder<MembersTable>{
        MembersBuilder defaultBuilder = table -> {
            table.left().top();

            Seq<MemberTable> members = table.getMembers();

            table.pane(Styles.noBarPane, buttons -> {
                buttons.background(Styles.black3);
                for(MemberTable member : members){
                    if(!member.canShown()) continue;

                    member.left().top();

                    buttons.button(member.icon, MStyles.clearToggleAccentb, () -> {
                        if(table.memberShowing(member)){
                            table.setMember(null);
                        }else{
                            table.setMember(member);
                        }
                    }).padTop(4.0f).size(32.0f).checked(b -> table.memberShowing(member)).row();
                }
            }).top();

            table.table(table::setContainer).grow().left().top();
        };
    }

    public static class MemberTable extends Table{
        public boolean desktopOnly = false;
        public boolean mobileOnly = false;
        public Drawable icon;

        public MemberTable(Drawable icon){
            this.icon = icon;
        }

        public boolean canShown(){
            return !(this.mobileOnly && !Vars.mobile || this.desktopOnly && !MinerVars.desktop);
        }

        public void memberRebuild(){
        }
    }

}

