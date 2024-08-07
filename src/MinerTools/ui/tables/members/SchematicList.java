package MinerTools.ui.tables.members;

import MinerTools.ui.tables.MembersTable.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.dialogs.SchematicsDialog.*;

import static MinerTools.ui.MUI.panes;
import static arc.graphics.Color.white;
import static arc.util.Align.center;
import static arc.util.Scaling.fit;
import static mindustry.Vars.ui;
import static mindustry.ui.Styles.*;

public class SchematicList extends MemberTable{
    public static float imageSize = 85f;

    private final Table schematicsTable = new Table(black3);
    private final Seq<String> selectedTags = new Seq<>();
    private int selectedCount;
    private Seq<String> tags = new Seq<>();

    private boolean isSetup = false;

    public SchematicList(){
        super(Icon.paste);

        background(black3);
    }

    private void setup(){
        tags = Reflect.get(ui.schematics, "tags");

        ScrollPane pane = pane(noBarPane, schematicsTable).maxHeight(imageSize * 2.5f).top().get();

        ScrollPane pane2 = pane(noBarPane, tagsTable -> {
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

        schematicsTable.label(() -> selectedCount + "/" + Vars.schematics.all().size).row();

        selectedCount = 0;
        int i = 0;
        for(Schematic schematic : Vars.schematics.all()){
            if(selectedTags.isEmpty() || schematic.labels.containsAll(selectedTags)){
                selectedCount++;

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
                    if(Vars.state.rules.schematicsAllowed){
                        Vars.control.input.useSchematic(schematic);
                    }
                });

                if(++i % 3 == 0) schematicsTable.row();
            }
        }
    }

    @Override
    public void onSelected(){
        super.onSelected();

        if(!isSetup){
            setup();
            isSetup = true;
        }
    }
}
