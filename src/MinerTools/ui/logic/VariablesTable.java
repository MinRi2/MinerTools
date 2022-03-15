package MinerTools.ui.logic;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;

import static MinerTools.ui.MStyles.logicVarTogglet;
import static mindustry.ui.Styles.*;

public class VariablesTable extends Table{
    public Seq<MVar> vars = new Seq<>();
    public Seq<MVar> selectedVars = new Seq<>();

    public VariablesTable(){
        super();
    }

    public void rebuild(){
        clear();

        if(vars.isEmpty()){
            return;
        }

        table(table -> {
            table.table(black6, t -> {
                t.add("1").color(Color.gray).center();
            }).growX();

            table.row();

            table.pane(nonePane, t -> {
                t.top();

                for(MVar mVar : vars){
                    t.button(b -> {
                        b.add(mVar.name).center();
                    }, logicVarTogglet, () -> {
                        handleMVar(mVar);
                    }).fillX().checked(b -> selectedVars.contains(mVar));

                    t.row();
                }
            }).maxHeight(Core.graphics.getHeight() / 2.5f).scrollX(false);

        }).top();

        for(MVar mVar : selectedVars){
            if(!mVar.hasChildren()){
                continue;
            }

            table(table -> {
                table.table(black6, t -> {
                    t.add("" + (mVar.level + 1)).color(Color.gray).center();
                }).growX();

                table.row();

                table.pane(nonePane, t -> {
                    t.top();

                    for(MVar v : mVar.children){
                        t.button(b -> {
                            b.add(v.name).center();
                        }, logicVarTogglet, () -> handleMVar(v))
                        .fillX().checked(b -> selectedVars.contains(v));

                        t.row();
                    }
                }).maxHeight(Core.graphics.getHeight() / 2.5f).scrollX(false);
            }).top();
        }
    }

    public void handleMVar(MVar mVar){
        var seq = selectedVars;

        boolean contains = seq.contains(mVar);

        if(mVar.level <= seq.size){
            seq.removeRange(mVar.level - 1, seq.size - 1);
        }
        if(!contains) seq.add(mVar);

        rebuild();
    }

    /** Removes all children, actions, and listeners from this group. */
    @Override
    public void clear(){
        super.clear();

        selectedVars.clear();
    }
}
