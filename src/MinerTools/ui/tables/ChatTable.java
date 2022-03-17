package MinerTools.ui.tables;

import MinerTools.ui.*;
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

import static MinerTools.MinerVars.mui;
import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class ChatTable extends Table{
    public static final String playerNameStart = "[coral][", playerNameEnd ="[coral]]";
    public static final String messageStart = ":[white] ";

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
            textField.setMaxLength(maxTextLength);
            textField.removeInputDialog();

            if(!mobile){
                textField.keyDown(KeyCode.enter, this::sendMessage);
                textField.keyDown(KeyCode.up, this::historyShiftUp);
                textField.keyDown(KeyCode.down, this::historyShiftDown);
            }else{
                table.button(Icon.modeAttack, clearTransi, this::sendMessage).padLeft(5f).growY();
                table.button(Icon.up, clearTransi, this::historyShiftUp).padLeft(1f).growY();
                table.button(Icon.down, clearTransi, this::historyShiftDown).padLeft(1f).growY();
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
        messages.reverse();

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

    private void historyShiftUp(){
        historyShift(1);
    }

    private void historyShiftDown(){
        historyShift(-1);
    }

    private void historyShift(int shift) {
        historyIndex = Mathf.clamp(historyIndex + shift, -1, history.size - 1);
        if (historyIndex < 0) {
            textField.setText("");
            return;
        }
        textField.setText(history.get(historyIndex));
    }

    private void rebuild(){
        messageTable.clear();

        if(messages.isEmpty()) return;

        for(String message : messages){
            Label label = messageTable.labelWrap(message).growX().left().get();

            /* Ctrl+MouseLeft --> copy the player's name */
            label.clicked(KeyCode.mouseLeft, () -> {
                if(input.ctrl()) mui.setClipboardText(catchPlayerName(message));
            });
            /* Ctrl+MouseRight --> copy the message */
            label.clicked(KeyCode.mouseRight, () -> {
                if(input.ctrl()) mui.setClipboardText(catchSendMessage(message));
            });

            messageTable.row();
        }
    }

    private String catchPlayerName(String message){
        // [coral][[[#FEEB2CFF][white]'PlayerName'[coral]]:[white] 'Message'
        if(message.contains(playerNameStart) && message.contains(messageStart)){
            int startIndex = message.indexOf(playerNameStart);
            int endIndex = message.indexOf(messageStart);
            return message.substring(startIndex + 1 + playerNameStart.length(), endIndex - playerNameEnd.length());
        }
        return "";
    }

    private String catchSendMessage(String message){
        // [coral][[[#FEEB2CFF][white]'PlayerName'[coral]]:[white] 'Message'
        if(message.contains(messageStart)){
            int startIndex = message.indexOf(messageStart);
            return message.substring(startIndex + 1);
        }
        return "";
    }
}
