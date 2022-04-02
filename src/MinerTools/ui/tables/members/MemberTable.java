package MinerTools.ui.tables.members;

import arc.scene.style.*;
import arc.scene.ui.layout.*;
import org.jetbrains.annotations.ApiStatus.*;

public class MemberTable extends Table{
    /* 仅电脑显示 */
    public boolean desktopOnly = false;
    /* 仅手机显示 */
    public boolean mobileOnly = false;

    public Drawable icon;

    public MemberTable(Drawable icon){
        this.icon = icon;
    }

    /**
     * MemberTable设置成自己时会调用此方法
     */
    @OverrideOnly
    public void memberRebuild(){}
}
