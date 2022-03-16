package MinerTools.ui.logic;

import arc.*;
import arc.graphics.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;

import static MinerTools.ui.MStyles.logicVarTogglet;
import static MinerTools.ui.logic.LogicVars.split;
import static mindustry.ui.Styles.*;

public class VariablesTable extends Table{
    public Seq<MVar> vars = new Seq<>();
    public Seq<MVar> selectedVars = new Seq<>();

    private Seq<ScrollPane> panes = new Seq<>();

    public VariablesTable(){
        super();
    }

    public String makeVarName(){
        return selectedVars.toString(split, v -> v.name);
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

            if(panes.isEmpty()){
                panes.add(new ScrollPane(new Table().top(), nonePane));
            }

            Table t = (Table)panes.get(0).getWidget();

            table.add(panes.get(0)).maxHeight(Core.graphics.getHeight() / 2f).scrollX(false).get();

            t.clear();
            for(MVar mVar : vars){
                t.button(b -> {
                    b.add(mVar.name).center();
                }, logicVarTogglet, () -> {
                    handleMVar(mVar);
                }).fillX().checked(b -> selectedVars.contains(mVar));

                t.row();
            }
        }).growX().top();

        for(MVar mVar : selectedVars){
            if(!mVar.hasChildren()){
                continue;
            }

            table(table -> {
                table.table(black6, t -> {
                    t.add("" + (mVar.level + 1)).color(Color.gray).center();
                }).growX();

                table.row();

                if(panes.size - 1 < mVar.level){
                    panes.add(new ScrollPane(new Table().top(), nonePane));
                }

                table.add(panes.get(mVar.level)).maxHeight(Core.graphics.getHeight() / 2f).scrollX(false).get();

                Table t = (Table)panes.get(mVar.level).getWidget();

                t.clear();
                for(MVar v : mVar.children){
                    t.button(b -> {
                        b.add(v.name).center();
                    }, logicVarTogglet, () -> handleMVar(v))
                    .fillX().checked(b -> selectedVars.contains(v));

                    t.row();
                }
            }).growX().top();
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

    public void clearSeq(){
        selectedVars.clear();
    }
}
