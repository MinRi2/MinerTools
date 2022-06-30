package MinerTools.ui.tables;

import MinerTools.*;
import arc.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.game.*;

public abstract class MembersTable extends Table implements Addable{
    final MemberManager memberManager = new MemberManager();

    public Table members;

    public MembersTable(){
        Events.on(EventType.WorldLoadEvent.class, e -> memberManager.resetMember());
    }

    public void setMember(MemberTable member){
        memberManager.setMember(member);
    }

    public void addMember(MemberTable... members){
        memberManager.addMember(members);
    }

    public abstract void rebuildMembers();

    public static class MemberManager{
        private Table container;

        private MemberTable showMember;
        private final Seq<MemberTable> memberTables = new Seq<>();

        public void addMember(MemberTable... members){
            memberTables.addAll(members);
        }

        public void setContainer(Table container){
            this.container = container;
        }

        public void resetMember(){
            setMember(showMember);
        }

        public void setMember(MemberTable member){
            container.clear();

            showMember = member;

            if(member != null){
                container.add(member).fill().padRight(2f);
                member.memberRebuild();
            }
        }

        public boolean isShown(){
            return showMember != null;
        }

        public boolean isShown(MemberTable memberTable){
            return showMember == memberTable;
        }

        public Seq<MemberTable> getMemberTables(){
            return memberTables;
        }
    }

    public static class MemberTable extends Table{
        /* Only show on desktop */
        public boolean desktopOnly = false;
        /* Only show on mobile */
        public boolean mobileOnly = false;

        public Drawable icon;

        public MemberTable(Drawable icon){
            this.icon = icon;
        }

        public boolean canShown(){
            return (!mobileOnly || Vars.mobile) && (!desktopOnly || MinerVars.desktop);
        }

        /**
         * MemberTable设置成自己时会调用此方法
         */
        public void memberRebuild(){}
    }
}
