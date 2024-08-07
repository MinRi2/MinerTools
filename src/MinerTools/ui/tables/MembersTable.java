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
    public final Seq<MemberTable> members = new Seq<>();
    public MembersBuilder builder;

    private Table container;
    private MemberTable currentMember;

    public MembersTable(){
        this(MembersBuilder.defaultBuilder);
    }

    public MembersTable(MembersBuilder builder){
        this.builder = builder;
    }

    public void setContainer(Table container){
        this.container = container;
    }

    public void rebuildCont(){
        clearChildren();
        builder.build(this);
    }

    public void addMember(MemberTable... members){
        this.members.addAll(members);
    }

    public void setMember(MemberTable member){
        container.clear();
        currentMember = member;
        if(member != null){
            container.add(member).grow().padRight(2.0f);
            member.onSelected();
        }
    }

    public void toggleMember(MemberTable member){
        if(isShowing(member)){
            setMember(null);
        }else{
            setMember(member);
        }
    }

    public boolean isShowing(){
        return currentMember != null;
    }

    public boolean isShowing(MemberTable memberTable){
        return currentMember == memberTable;
    }

    public Seq<MemberTable> getMembers(){
        return members;
    }

    public interface MembersBuilder extends TableBuilder<MembersTable>{
        /**
         * 横向排布
         */
        MembersBuilder defaultBuilder = table -> {
            table.top();

            Seq<MemberTable> members = table.getMembers();

            table.table(buttons -> {
                buttons.background(Styles.black3);

                for(MemberTable member : members){
                    if(!member.canShown()) continue;

                    member.left().top();

                    buttons.button(member.icon, MStyles.clearToggleAccentb, () -> {
                        table.toggleMember(member);
                    }).height(32f).padTop(4.0f).growX().checked(b -> table.isShowing(member));
                }
            }).growX().top();

            table.row();

            table.table(table::setContainer).grow().top();
        };
    }

    public static class MemberTable extends Table{
        public boolean desktopOnly;
        public boolean mobileOnly;
        public Drawable icon;

        public MemberTable(Drawable icon){
            this.icon = icon;
        }

        public boolean canShown(){
            return !(this.mobileOnly && !Vars.mobile || this.desktopOnly && !MinerVars.desktop);
        }

        public void onSelected(){
        }
    }

}

