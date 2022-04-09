package MinerTools.interfaces;

public interface Drawable<T>{
    /* 读取设置 */
    void readSetting();

    /* 是否开启功能 */
    boolean enabled();

    /* 游戏中是否生效 */
    boolean isValid();

    /* 绘制前初始化一些变量 */
    default void init(){}

    /* 对绘制种类是否生效 */
    boolean isValid(T type);

    /* 尝试绘制 */
    default void tryDraw(T type){
        if(isValid(type)) draw(type);
    }

    /* 绘制 */
    void draw(T type);
}
