package MinerTools.ui.logic;

import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;

import static MinerTools.ui.MStyles.logicVarsTogglet;

public class NodesTable extends Table{
    private Seq<Node> nodes = new Seq<>();

    public NodesTable(){
        super();
    }

    public void rebuild(){
        clear();

        if(nodes.isEmpty()){
            return;
        }

        for(Node node : nodes){
            if(node.hasChild()){
                table(Tex.buttonOver, t -> {
                    for(Node child : node.children){
                        t.button(child.name, logicVarsTogglet, () -> handleNode(child))
                        .pad(0f).minWidth(85f).checked(b -> nodes.contains(child)).fillX();

                        t.row();
                    }
                }).top();
            }
        }
    }

    public void handleHeadNode(Node headNode){
        boolean contains = nodes.contains(headNode);

        nodes.clear();

        if(!contains){
            nodes.add(headNode);
        }

        rebuild();
    }

    public void handleNode(Node node){
        if(!nodes.isEmpty() && node.level <= nodes.size - 1){
            nodes.removeRange(node.level, nodes.size - 1);
        }
        nodes.add(node);
        rebuild();
    }

    public boolean contains(Node node){
        return nodes.contains(node);
    }
}
