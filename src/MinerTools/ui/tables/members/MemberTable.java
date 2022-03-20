package MinerTools.ui.tables.members;

import MinerTools.ui.tables.*;
import arc.scene.style.*;
import org.jetbrains.annotations.ApiStatus.*;

public class MemberTable extends BaseTable{
    /* 仅电脑显示 */
    public boolean desktopOnly = false;
    /* 仅手机显示 */
    public boolean mobileOnly = false;
    public Drawable icon;

    /**
     * MemberTable设置成自己时会调用此方法
     */
    @OverrideOnly
    public void memberRebuild(){
        rebuild();
    }

    @OverrideOnly
    private void rebuild(){}
}
