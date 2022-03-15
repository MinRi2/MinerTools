package MinerTools.ui.logic;

import MinerTools.ui.*;
import arc.graphics.*;
import arc.scene.style.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.logic.LExecutor.*;
import mindustry.ui.*;

import static arc.Core.*;
import static mindustry.Vars.ui;
import static mindustry.ui.Styles.black6;

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

        table(((TextureRegionDrawable)Tex.whiteui).tint(Color.blue),table -> {
            table.label(() -> "Logic Vars").center();
            setDraggier(table);
        }).pad(0f).fillX();

        row();

        table(black6, table -> {
            table.label(() -> varsTable.makeVarName());
            table.add().growX();
            table.button(Icon.copy, Styles.clearPartiali, () -> app.setClipboardText(varsTable.makeVarName()));
        }).pad(0f).fillX();

        row();

        table(table -> {
            table.add(varsTable).pad(0f).top().growX();

            rebuildVarsTable();
        }).pad(0f).top().fillX();
    }

    private void rebuildVarsTable(){
        varsTable.clear();
        varsTable.clearSeq();

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
