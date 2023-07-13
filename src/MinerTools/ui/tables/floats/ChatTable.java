package MinerTools.ui.tables.floats;

import MinerTools.ui.*;
import MinerTools.ui.settings.*;
import arc.*;
import arc.flabel.*;
import arc.graphics.*;
import arc.input.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.Timer;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.dialogs.*;

import java.text.*;
import java.util.*;

import static MinerTools.MinerVars.desktop;
import static MinerTools.ui.MUI.setClipboardText;
import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.ui.Styles.*;

public class ChatTable extends FloatTable{
    private static final String messageStart = ":[white] ";

    private final Interval timer = new Interval();

    private final Seq<MessageStack> messageStacks = new Seq<>();
    private final Seq<String> selectMessages = new Seq<>();
    private final Seq<String> history = new Seq<>();

    private int historyIndex;
    /* For mobile */
    private boolean copyMode;

    private Dialog messageDialog;
    private Table messageTable;
    private ScrollPane pane;
    private TextField textField;

    private TextFieldStyle fstyle;

    private int lastMessageSize;

    public ChatTable(){
        super("chat");

        Events.on(EventType.WorldLoadEvent.class, e -> {
            history.clear();
            historyIndex = -1;
            messageStacks.add(new MessageStack());

            resetMessages();
            Timer.schedule(this::scrollToBottom, 1f);
        });

        Timer.schedule(() -> {
            if(Vars.state.isGame()){
                messageStacks.add(new MessageStack());
            }
        }, 0f, 60f);
        
        update(() -> {
            if(timer.get(60f)){
                resetMessages();
            }
        });
    }

    @Override
    protected void init(){
        super.init();

        messageDialog = new BaseDialog("Messages");
        messageTable = new Table(black3);
        pane = new ScrollPane(messageTable, noBarPane);

        fstyle = new TextFieldStyle(areaField){{
            background = black6;
        }};
    }

    @Override
    protected void addSettings(MSettingTable uiSettings){
        super.addSettings(uiSettings);
    }

    @Override
    protected void setupCont(Table cont){
        cont.add(pane).grow().minWidth(350f).maxHeight(170f).scrollX(false);

        cont.row();

        cont.table(table -> {
            textField = table.field("", fstyle, s -> {
            }).padTop(15f).height(32f).grow().get();

            textField.setMessageText("Send Message");
            textField.setMaxLength(maxTextLength);
            textField.removeInputDialog();

            if(desktop){
                textField.keyDown(KeyCode.enter, this::sendMessage);
                textField.keyDown(KeyCode.up, this::historyShiftUp);
                textField.keyDown(KeyCode.down, this::historyShiftDown);
            }else{
                table.table(buttons -> {
                    buttons.defaults().size(40f).growY();

                    buttons.button(Icon.modeAttackSmall, clearNonei, this::sendMessage);
                    buttons.button(Icon.copySmall, clearNoneTogglei, this::toggleCopyMode).checked(b -> copyMode);
                    buttons.button(Icon.upSmall, clearNonei, this::historyShiftUp);
                    buttons.button(Icon.downSmall, clearNonei, this::historyShiftDown);
                }).growY();
            }
        }).growX();

        setupDialog();

        MUI.panes.add(pane);
    }

    @Override
    protected void setupButtons(Table buttons){
        buttons.button(Icon.chatSmall, clearNonei, () -> {
            rebuildDialog();
            messageDialog.show();
        });
    }

    private void setupDialog(){
        messageDialog.addCloseButton();
    }

    private void rebuildDialog(){
        Table cont = messageDialog.cont;
        cont.clearChildren();

        cont.pane(noBarPane, table -> {
            for(MessageStack messageStack : messageStacks){
                if(!messageStack.hasMessage()){
                    continue;
                }

                table.add(messageStack.getTime()).growX().color(Color.gray).padTop(7f);

                table.row();

                table.table(MStyles.clearFlatOver, messages -> {
                    for(String msg : messageStack.messages){
                        messages.button(msg, MStyles.clearToggleTranst, () -> addSelectMessage(msg))
                        .checked(b -> selectMessages.contains(msg, true)).growX().left().padTop(2f)
                        .get().getLabel().setAlignment(Align.left);

                        messages.row();
                    }
                }).fillX().padTop(5f);

                table.row();
            }
        }).minWidth(graphics.getWidth() / 2.4f).fillY().scrollX(false);

        cont.table(t -> {
            t.table(buttons -> {
                buttons.defaults().minWidth(115f).growX().top();

                buttons.button("Clear", Icon.refreshSmall, MStyles.clearPartial2t, 45, selectMessages::clear);
                buttons.button("Copy", Icon.copySmall, MStyles.clearPartial2t, 45, () -> setClipboardText(selectMessages.toString("\n")));
            }).top();

            t.row();

            t.table(MStyles.clearFlatOver, s -> {}).update(selectsTable -> {
                selectsTable.clearChildren();

                if(selectMessages.isEmpty()) return;

                for(String msg : selectMessages){
                    selectsTable.labelWrap(msg).growX().left();
                    selectsTable.row();
                }
            }).grow();
        }).width(graphics.getWidth() / 4.5f).fillY();
    }

    private void resetMessages(){
        var messages = Reflect.<Seq<String>>get(ui.chatfrag, "messages");
        int messageSize = messages.size;

        boolean lastIsBottomEdge = pane.isBottomEdge();

        if(lastMessageSize != messageSize){
            if(messageStacks.isEmpty()){
                return;
            }

            MessageStack stack = messageStacks.peek();

            int n = messageSize - lastMessageSize;
            for(int i = 0; i < n; i++){
                stack.addMessage(messages.get(i));
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

    private void addMessage(String msg){
        Label label = new FLabel(msg);

        messageTable.add(label).labelAlign(Align.left).wrap().growX();

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

    private String catchSendMessage(String message){
        // [coral][[[#FEEB2CFF]miner[coral]]:[white] this is a test
        if(message.contains(messageStart)){
            int startIndex = message.indexOf(messageStart);
            return message.substring(startIndex + messageStart.length());
        }
        return "";
    }

    private void addSelectMessage(String message){
        if(selectMessages.contains(message, true)){
            selectMessages.remove(message);
        }else{
            selectMessages.add(message);
        }
    }

    private void historyShift(int shift){
        historyIndex = Mathf.clamp(historyIndex + shift, -1, history.size - 1);
        if(historyIndex < 0){
            textField.setText("");
            return;
        }
        textField.setText(history.get(historyIndex));
    }

    private void historyShiftUp(){
        historyShift(1);
    }

    private void historyShiftDown(){
        historyShift(-1);
    }

    private void scrollToBottom(){
        pane.setScrollY(Float.MAX_VALUE);
    }

    private void toggleCopyMode(){
        copyMode = !copyMode;
    }

    public static class MessageStack{
        public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        private Date date = new Date();
        public Seq<String> messages = new Seq<>();

        public int addMessage(String msg){
            messages.add(msg);
            return messages.size;
        }

        public boolean hasMessage(){
            return messages.any();
        }

        public String getTime(){
            return format.format(date);
        }
    }
}
