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
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;

import static MinerTools.MinerVars.*;
import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class ChatTable extends MemberTable{
    public static final String messageStart = ":[white] ";

    private Interval timer = new Interval();

    private Seq<String> messages = new Seq<>();
    private Seq<String> history = new Seq<>();

    private int historyIndex;
    /* For mobile */
    private boolean copyMode;

    private Table messageTable = new Table();
    private ScrollPane pane;
    private TextField textField;

    private int lastSize;
    private boolean lastIsBottomEdge;

    public ChatTable(){
        icon = Icon.chat;

        Events.on(EventType.WorldLoadEvent.class, e -> {
            history.clear();
            historyIndex = -1;
            pane.setScrollY(Float.MAX_VALUE);
        });

        setup();
    }

    private void setup(){
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

            if(desktop){
                textField.keyDown(KeyCode.enter, this::sendMessage);
                textField.keyDown(KeyCode.up, this::historyShiftUp);
                textField.keyDown(KeyCode.down, this::historyShiftDown);
            }else{
                table.button(Icon.modeAttack, clearTransi, this::sendMessage).growY();
                table.button(Icon.copy, clearToggleTransi, this::toggleCopyMode).growY().checked(b -> copyMode);
                table.button(Icon.up, clearTransi, this::historyShiftUp).growY();
                table.button(Icon.down, clearTransi, this::historyShiftDown).growY();
            }
        }).growX();

        update(() -> {
            if(timer.get(60f)){
                resetMessages();
            }
        });

        Events.on(EventType.PlayerChatEvent.class, e -> Log.info("PlayerChatting!!!"));

        MinerToolsTable.panes.add(pane);
    }

    private void resetMessages(){
        lastSize = messages.size;

        messages.set(Reflect.<Seq<String>>get(ui.chatfrag, "messages"));
        messages.reverse();

        lastIsBottomEdge = pane.isBottomEdge();

        rebuild();

        if(lastIsBottomEdge && messages.size != lastSize){
            app.post(this::scrollBottom);
        }
    }

    private void scrollBottom(){
        pane.setScrollY(Float.MAX_VALUE);
    }

    private void sendMessage(){
        if(!textField.getText().equals("")){
            history.insert(0, textField.getText());
            historyIndex = -1;

            Call.sendChatMessage(textField.getText());
            textField.clearText();
        }
    }

    private String catchSendMessage(String message){
        // [coral][['PlayerColor'[white]'PlayerName'[coral]]:[white] 'Message'
        if(message.contains(messageStart)){
            int startIndex = message.indexOf(messageStart);
            return message.substring(startIndex + 1);
        }
        return "";
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

    private void toggleCopyMode(){
        copyMode = !copyMode;
    }

    private void rebuild(){
        messageTable.clear();

        if(messages.isEmpty()) return;

        for(String message : messages){
            Label label = messageTable.labelWrap(message).growX().left().get();

            if(desktop){
                /* Ctrl + MouseLeft --> copy the message */
                label.clicked(KeyCode.mouseLeft, () -> {
                    if(input.ctrl()) mui.setClipboardText(catchSendMessage(message));
                });
            }else{
                /* click --> copy the message */
                label.clicked(() -> {
                    if(copyMode) mui.setClipboardText(catchSendMessage(message));
                });
            }

            messageTable.row();
        }
    }

    @Override
    public void memberRebuild(){
        pane.setScrollY(Float.MAX_VALUE);
    }
}
