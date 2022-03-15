package MinerTools.ui.logic;

import MinerTools.ui.*;
import arc.graphics.*;
import arc.scene.style.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.logic.LExecutor.*;

import static arc.Core.*;
import static mindustry.Vars.ui;

public class LogicVars extends DraggableTable{
    public static String split = ".";

    private LExecutor executor;


    private VariablesTable varsTable = new VariablesTable();

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
            table.table(t -> t.add(varsTable)).pad(0f).top();

            rebuildVarsTable();
        }).pad(0f).top();
    }

    private void rebuildVarsTable(){
        varsTable.clear();
        resetVars();
        varsTable.rebuild();
    }

    private void resetVars(){
        if(executor == null){
            return;
        }

        varsTable.vars.clear();

        for(Var var : executor.vars){
            if(var.name.contains(split) && !var.name.startsWith("___")){
                resolveVar(var.name);
            }
        }
    }

    private void resolveVar(String str){
        String[] strings = str.split("\\" + split, 2);

        MVar mVar;
        if((mVar = varsTable.vars.find(mVar1 -> mVar1.name.equals(strings[0]))) == null){
            mVar = new MVar(strings[0], 1);
            varsTable.vars.add(mVar);
        }

        resolveVar(mVar, strings[1], new int[]{2});
    }

    private void resolveVar(MVar parent, String str, int[] level){
        if(!str.contains(split)){
            parent.addChild(new MVar(str, level[0]++));
        }else{
            String[] strings = str.split("\\" + split, 2);

            MVar mVar;
            if((mVar = parent.findChild(mVar1 -> mVar1.name.equals(strings[0]))) == null){
                mVar = new MVar(strings[0], level[0]);
                parent.addChild(mVar);
            }

            level[0]++;

            if(strings.length == 2) resolveVar(mVar, strings[1], level);
        }
    }
}
