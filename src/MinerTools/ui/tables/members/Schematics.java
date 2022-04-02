package MinerTools.ui.tables.members;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.dialogs.SchematicsDialog.*;

import java.lang.reflect.*;

import static MinerTools.ui.MUI.panes;
import static arc.graphics.Color.white;
import static arc.util.Align.center;
import static arc.util.Scaling.fit;
import static mindustry.Vars.ui;
import static mindustry.ui.Styles.*;

public class Schematics extends MemberTable{
    public static float imageSize = 85f;

    private Table schematicsTable = new Table(black3);

    private int selectedSchemCount;

    private Seq<String> tags = new Seq<>(), selectedTags = new Seq<>();

    private Field tagsField;


    public Schematics(){
        super(Icon.paste);

        try{
            tagsField = SchematicsDialog.class.getDeclaredField("tags");
            tagsField.setAccessible(true);
        }catch(Exception e){
            ui.showException(e);
        }

        background(black3);

        rebuild();
    }

    private void rebuild(){
        try{
            tags = (Seq<String>)tagsField.get(ui.schematics);
        }catch(Exception e){
            ui.showException(e);
        }
        ScrollPane pane = pane(nonePane, schematicsTable).maxHeight(imageSize * 2.5f).top().get();

        ScrollPane pane2 = pane(nonePane, tagsTable -> {
            for(String tag : tags){
                tagsTable.button(tag, togglet, () -> {
                    if(selectedTags.contains(tag)) selectedTags.remove(tag);
                    else selectedTags.add(tag);
                    schematicsRebuild();
                }).checked(b -> selectedTags.contains(tag)).growY().maxWidth(35).maxHeight(35).get().getLabelCell().fontScale(0.95f);

                tagsTable.row();
            }

            schematicsRebuild();
        }).maxHeight(imageSize * 2.5f).right().get();

        pane.setScrollingDisabled(true, false);
        pane2.setScrollingDisabled(true, false);

        panes.add(pane);
        panes.add(pane2);
    }

    private void schematicsRebuild(){
        schematicsTable.clear();

        schematicsTable.label(() -> selectedSchemCount + "/" + Vars.schematics.all().size).row();

        selectedSchemCount = 0;
        int i = 0;
        for(Schematic schematic : Vars.schematics.all()){
            if(selectedTags.isEmpty() || schematic.labels.containsAll(selectedTags)){
                selectedSchemCount++;

                schematicsTable.button(b -> {
                    b.stack(
                    new SchematicImage(schematic).setScaling(fit),
                    new Table(n -> {
                        n.top();
                        n.table(black3, c -> {
                            Label l = c.add(schematic.name()).style(outlineLabel).color(white).top().growX().maxWidth(70 - 8).get();
                            l.setEllipsis(true);
                            l.setAlignment(center);
                        }).growX().margin(1).pad(4).maxWidth(Scl.scl(imageSize - 8)).padBottom(0);
                    })).size(imageSize);
                }, () -> {
                    Vars.control.input.useSchematic(schematic);
                });

                if(++i % 3 == 0) schematicsTable.row();
            }
        }
    }
}
