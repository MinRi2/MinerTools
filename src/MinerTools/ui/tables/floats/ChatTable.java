package MinerTools.ui.tables.floats;

import MinerTools.core.*;
import arc.input.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

import static MinerTools.MinerVars.*;
import static MinerTools.core.MUI.*;
import static MinerTools.ui.MStyles.chatb;
import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class ChatTable extends FloatTable{
    private static final String messageStart = ":[white] ";
    public static final Seq<String> quickWords = Seq.with("Test", "gg", "Hello");
    public static Table quickWordTable = new Table();

    private Interval timer = new Interval();

    private Seq<String> history = new Seq<>();

    private int historyIndex;
    /* For mobile */
    private boolean copyMode;

    private Table messageTable;
    private ScrollPane pane;
    private TextField textField;

    private TextFieldStyle fstyle;

    private int lastMessageSize;
    private boolean lastIsBottomEdge;

    public ChatTable(){
        super("chat");
    }

    @Override
    protected void init(){
        super.init();

        messageTable = new Table(black3);
        pane = new ScrollPane(messageTable, nonePane);

        fstyle = new TextFieldStyle(areaField){{
            background = black6;
        }};
    }

    @Override
    protected void setupCont(Table cont){
        cont.add(pane).minWidth(350f).maxHeight(170f).scrollX(false);

        cont.row();

        cont.table(table -> {
            textField = table.field("", fstyle, s -> {
            }).padTop(15f).grow().get();

            textField.setMessageText("Send Message");
            textField.setMaxLength(maxTextLength);
            textField.removeInputDialog();

            if(desktop){
                textField.keyDown(KeyCode.enter, this::sendMessage);
                textField.keyDown(KeyCode.up, this::historyShiftUp);
                textField.keyDown(KeyCode.down, this::historyShiftDown);
            }else{
                table.table(buttons -> {
                    buttons.defaults().width(25f).growY();

                    buttons.button(Icon.modeAttackSmall, clearTransi, this::sendMessage);
                    buttons.button(Icon.copySmall, clearToggleTransi, this::toggleCopyMode).checked(b -> copyMode);
                    buttons.button(Icon.upSmall, clearTransi, this::historyShiftUp);
                    buttons.button(Icon.downSmall, clearTransi, this::historyShiftDown);
                }).growY();
            }
        }).growX();

//        setupQuickWordTable();

        MUI.panes.add(pane);
    }

    @Override
    public void addUI(){
        super.addUI();

        messageTable.clear();
        history.clear();
        historyIndex = -1;
        scrollToBottom();
    }

    private void setupQuickWordTable(){
        Table table = quickWordTable;

        table.update(() -> {
            Element result = scene.hit(input.mouseX(), input.mouseY(), true);
            if(result == null || !result.isDescendantOf(table)){
                table.remove();
            }
        });
    }

    private void showQuickTable(){
        Table table = quickWordTable;

        rebuildQuickWordTable();

        table.setPosition(input.mouseX(), input.mouseY() + 10f, Align.top);

        scene.add(table);
    }

    private void resetMessages(){
        var messages = Reflect.<Seq<String>>get(ui.chatfrag, "messages");
        int messageSize = messages.size;

        lastIsBottomEdge = pane.isBottomEdge();

        if(lastMessageSize != messageSize){
            int n = messageSize - lastMessageSize;
            for(int i = 0; i < n; i++){
                addMessage(messages.get(i));
            }

            if(lastIsBottomEdge){
                app.post(this::scrollToBottom);
            }
        }

        lastMessageSize = messageSize;
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

    private void historyShift(int shift){
        historyIndex = Mathf.clamp(historyIndex + shift, -1, history.size - 1);
        if(historyIndex < 0){
            textField.setText("");
            return;
        }
        textField.setText(history.get(historyIndex));
    }

    private void scrollToBottom(){
        pane.setScrollY(Float.MAX_VALUE);
    }

    private void toggleCopyMode(){
        copyMode = !copyMode;
    }

    private void addMessage(String msg){
        Label label = messageTable.labelWrap(msg).growX().left().get();

        if(desktop){
            /* Ctrl + MouseLeft --> copy the message */
            label.clicked(KeyCode.mouseLeft, () -> {
                if(input.ctrl()) setClipboardText(catchSendMessage(msg));
            });
        }else{
            /* click --> copy the message */
            label.clicked(() -> {
                if(copyMode) setClipboardText(catchSendMessage(msg));
            });
        }

        messageTable.row();
    }

    private void rebuildQuickWordTable(){
        Table table = quickWordTable;

        table.clear();

        table.table(black5, t -> {
            TextField field = t.field("", text -> {
            }).growX().height(45f).get();
            t.button(Icon.saveSmall, clearPartiali, () -> {
                quickWords.add(field.getText());
                rebuildQuickWordTable();
            }).fillY();
        }).fill();

        table.row();

        table.pane(nonePane, t -> {
            for(String quickWord : quickWords){
                table.button(b -> {
                    b.labelWrap(quickWord).growX().left();
                }, chatb, () -> {
                    Call.sendChatMessage(quickWord);
                    resetMessages();
                    table.remove();
                }).minSize(250f, 45f);
                table.row();
            }
        }).maxHeight(45f * 7);

        table.pack();
    }

    @Override
    protected void update(){
        super.update();

        if(timer.get(60f)){
            resetMessages();
        }
//        if(input.alt() && input.keyTap(KeyCode.b)){
//            showQuickTable();
//        }
    }
}
