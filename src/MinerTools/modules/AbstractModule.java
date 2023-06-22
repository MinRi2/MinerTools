package MinerTools.modules;

import arc.struct.*;

public abstract class AbstractModule<T extends AbstractModule> implements Module{
    protected final String name;
    protected boolean enable = true;

    protected final T parent;
    protected final ObjectSet<T> children = new ObjectSet<>();

    public AbstractModule(String name){
        this(null, name);
    }

    public AbstractModule(T parent, String name){
        this.parent = parent;
        this.name = name;
        
        if(parent != null){
            parent.addChild(this);
        }
    }
    
    public void addChild(T child){
        children.add(child);
    }

    @Override
    public boolean isEnable(){
        return enable && dependencyEnable();
    }

    public void setEnable(boolean enable){
        this.enable = enable;

        if(this.enable){
            enable();
        }else{
            disable();
        }
    }

    public void toggle(){
        setEnable(!enable);
    }

    public void enable(){
    }

    public void disable(){
    }

    protected boolean dependencyEnable(){
        return parent == null || parent.isEnable();
    }

}
