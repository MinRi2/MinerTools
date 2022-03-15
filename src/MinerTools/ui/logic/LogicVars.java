package MinerTools.ui.logic;

import MinerTools.ui.*;
import arc.graphics.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.logic.LExecutor.*;

import static MinerTools.ui.MStyles.logicVarsTogglet;
import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class LogicVars extends DraggableTable{
    public static String split = ".";

    private LExecutor executor;

    private Table headNodeTable;
    private Seq<Node> headNodes = new Seq<>();

    private NodesTable nodesTable = new NodesTable();

    public LogicVars(){
        super();

        ui.logic.shown(() -> {
            executor = Reflect.get(ui.logic, "executor");
            rebuild();
            show();
        });

        ui.logic.hidden(this::remove);

        /* def position */
        setPosition(graphics.getWidth() / 4f, graphics.getHeight() / 2f);

        update(() -> {
            toFront();
            pack();
        });
    }

    private void show(){
        scene.add(this);
    }

    private void rebuild(){
        clear();

        table(((TextureRegionDrawable)Tex.whiteui).tint(Color.red),table -> {
            table.label(() -> "Logic Vars");
            setDraggier(table);
        }).pad(0f).fillX();

        row();

        table(table -> {
            table.table(Tex.buttonOver, t -> headNodeTable = t).top();
            table.table(t -> t.add(nodesTable)).pad(0f).top();

            rebuildVarsTable();
        }).pad(0f).top();
    }

    private void rebuildVarsTable(){
        Table table = headNodeTable;

        table.clear();

        resetVars();

        for(Node headNode : headNodes){
            table.button(headNode.name, logicVarsTogglet, () -> nodesTable.handleHeadNode(headNode))
            .pad(0f).minWidth(85f).checked(b -> nodesTable.contains(headNode)).fillX();

            table.row();
        }
    }

    private void resetVars(){
        if(executor == null){
            return;
        }

        headNodes.clear();

        for(Var var : executor.vars){
            if(var.name.contains(split) && !var.name.startsWith("___")){
                resolveHeadNode(var.name);
            }
        }
    }

    private void resolveHeadNode(String str){
        String[] strs = str.split("\\" + split, 2);

        Node node;
        if((node = headNodes.find(node1 -> node1.name.equals(strs[0]))) == null){
            node = new Node(strs[0], 0);
            headNodes.add(node);
        }

        if(strs.length == 2) resolveNode(node, strs[1], new int[]{1});
    }

    private void resolveNode(Node parent, String str, int[] level){
        if(!str.contains(split)){
            parent.addNode(new Node(str, level[0]++));
        }else{
            String[] strs = str.split("\\" + split, 2);

            Node node;
            if((node = parent.findChild(node1 -> node1.name.equals(strs[0]))) == null){
                node = new Node(strs[0], level[0]);
                parent.addNode(node);
            }

            level[0]++;

            if(strs.length == 2) resolveNode(node, strs[1], level);
        }
    }
}
