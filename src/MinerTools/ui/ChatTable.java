package MinerTools.ui;

import arc.*;
import arc.input.*;
import arc.scene.ui.*;
import arc.scene.ui.ScrollPane.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;

import static MinerTools.MinerVars.desktop;
import static mindustry.Vars.ui;
import static mindustry.ui.Styles.*;

public class ChatTable extends Table{
    private Interval timer = new Interval();

    private Seq<String> messages = new Seq<>();

    private Table messageTable = new Table();
    private ScrollPane pane;
    private TextArea area;

    private boolean lastIsBottomEdge;

    public ChatTable(){
        Events.on(EventType.WorldLoadEvent.class, e -> resetMessages());

        TextFieldStyle style = new TextFieldStyle(areaField){{
            background = black6;
        }};

        ScrollPaneStyle paneStyle = new ScrollPaneStyle(nonePane){{
            background = black3;
        }};

        pane = pane(paneStyle, messageTable).minWidth(350f).maxHeight(235f).scrollX(false).get();

        row();

        table(table -> {
            area = table.area("", style, s -> {}).padTop(15f).grow().get();

            area.setMessageText("Send Message");

            if(desktop){
                area.keyDown(KeyCode.enter, this::sendMessage);
            }else{
                table.button(Icon.modeAttack, clearTransi, this::sendMessage).padLeft(5f).growY();
                table.button(Icon.refresh1, clearTransi, this::clearText).growY();
            }
        }).grow();

        update(() -> {
            if(timer.get(60f)){
                resetMessages();
            }
        });

        MinerToolsTable.panes.add(pane);
    }

    private void resetMessages(){
        messages.set(Reflect.<Seq<String>>get(ui.chatfrag, "messages"));

        boolean isBottomEdge = pane.isBottomEdge();

        rebuild();

        if(lastIsBottomEdge && !isBottomEdge){
            pane.setScrollY(Float.MAX_VALUE);
        }

        lastIsBottomEdge = isBottomEdge;
    }

    private void sendMessage(){
        if(!area.getText().equals("")){
            Call.sendChatMessage(area.getText());
            area.clearText();
        }
    }

    private void clearText(){
        area.clearText();
    }

    void rebuild(){
        messageTable.clear();


        if(messages.isEmpty()) return;

        for(int i = messages.size - 1; i >= 0; i--){
            messageTable.labelWrap(messages.get(i)).growX().left();
            messageTable.row();
        }
    }
}
