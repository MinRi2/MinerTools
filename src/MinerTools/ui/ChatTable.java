package MinerTools.ui;

import arc.*;
import arc.input.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.ScrollPane.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;

import static MinerTools.MinerVars.desktop;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class ChatTable extends Table{
    private Interval timer = new Interval();

    private Seq<String> messages = new Seq<>();
    private Seq<String> history = new Seq<>();

    private int historyIndex;

    private Table messageTable = new Table();
    private ScrollPane pane;
    private TextField textField;

    private boolean lastIsBottomEdge;

    public ChatTable(){
        Events.on(EventType.WorldLoadEvent.class, e -> {
            history.clear();
            historyIndex = -1;
        });

        TextFieldStyle style = new TextFieldStyle(areaField){{
            background = black6;
        }};

        ScrollPaneStyle paneStyle = new ScrollPaneStyle(nonePane){{
            background = black3;
        }};

        pane = pane(paneStyle, messageTable).minWidth(350f).maxHeight(235f).scrollX(false).get();

        row();

        table(table -> {
            textField = table.field("", style, s -> {
            }).padTop(15f).grow().get();

            textField.setMessageText("Send Message");
            textField.removeInputDialog();

            if(mobile){
                textField.keyDown(KeyCode.enter, this::sendMessage);
                textField.keyDown(KeyCode.up, () -> historyShift(1));
                textField.keyDown(KeyCode.down, () -> historyShift(-1));
            }else{
                table.button(Icon.modeAttack, clearTransi, this::sendMessage).padLeft(5f).growY();
                table.button(Icon.up, clearTransi, () -> historyShift(1)).padLeft(1f).growY();
                table.button(Icon.down, clearTransi, () -> historyShift(-1)).padLeft(1f).growY();
            }
        }).growX();

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
        if(!textField.getText().equals("")){
            history.insert(0, textField.getText());
            historyIndex = -1;

            Call.sendChatMessage(textField.getText());
            textField.clearText();
        }
    }

    private void historyShift(int shift) {
        historyIndex = Mathf.clamp(historyIndex + shift, -1, history.size - 1);
        if (historyIndex < 0) {
            textField.setText("");
            return;
        }
        textField.setText(history.get(historyIndex));
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
