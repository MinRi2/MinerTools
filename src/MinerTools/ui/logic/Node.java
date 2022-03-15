package MinerTools.ui.logic;

import arc.func.*;
import arc.struct.*;

import java.util.*;

public class Node{
    public String name;
    public int level;

    public Node parent;
    public Seq<Node> children = new Seq<>();

    public Node(String name, int level){
        this.name = name;
        this.level = level;
    }

    public void addNode(Node node){
        children.add(node);
        node.setParent(this);
    }

    public void removeChildNode(Node node){
        children.remove(node);
        node.setParent(null);
    }

    public void setParent(Node newParent){
        if(parent != null){
            parent.removeChildNode(this);
        }

        parent = newParent;
    }

    public Node findChild(Boolf<Node> prov){
        return children.find(prov);
    }

    public boolean hasChild(){
        return children.size != 0;
    }

    public boolean hasParent(){
        return parent != null;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Node varNode = (Node)o;
        if(hasParent() && varNode.hasParent() && parent != varNode.parent) return false;
        return name.equals(varNode.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }
}