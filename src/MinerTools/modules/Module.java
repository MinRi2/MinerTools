package MinerTools.modules;

public interface Module{
    void load();

    boolean isEnable();

    void setEnable(boolean enable);
}
