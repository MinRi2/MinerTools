package MinerTools.ui.logic;

import arc.func.*;
import arc.struct.*;

import java.util.*;

public class MVar{
    public String name;
    public int level;

    public MVar parent;
    public Seq<MVar> children = new Seq<>();

    public MVar(String name, int level){
        this.name = name;
        this.level = level;
    }

    public void addChild(MVar node){
        children.add(node);
        node.setParent(this);
    }

    public void removeChild(MVar node){
        children.remove(node);
        node.setParent(null);
    }

    public void setParent(MVar newParent){
        if(parent != null){
            parent.removeChild(this);
        }

        parent = newParent;
    }

    public MVar findChild(Boolf<MVar> prov){
        return children.find(prov);
    }

    public boolean hasChildren(){
        return children.size != 0;
    }

    public boolean hasParent(){
        return parent != null;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        MVar varNode = (MVar)o;
        if(hasParent() && varNode.hasParent() && parent != varNode.parent) return false;
        return name.equals(varNode.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }
}