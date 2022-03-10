package MinerTools.ui;

import arc.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;

import static mindustry.Vars.ui;
import static mindustry.ui.Styles.*;

public class ChatTable extends Table{
    private Interval timer = new Interval();

    private Seq<String> messages;

    private Table messageTable = new Table();
    private ScrollPane pane;

    public ChatTable(){
        background(black3);

        Events.on(EventType.WorldLoadEvent.class, e -> resetMessages());

        pane = pane(nonePane, messageTable).minWidth(350f).maxHeight(235f).scrollX(false).get();
        MinerToolsTable.panes.add(pane);

        update(() -> {
            if(timer.get(60f)){
                resetMessages();
            }
        });
    }

    void resetMessages(){
        messages = Reflect.get(ui.chatfrag, "messages");

        rebuild();
    }

    void rebuild(){
        messageTable.clear();

        if(messages.isEmpty()) return;

        for(int i = messages.size - 1; i > 0; i--){
            messageTable.labelWrap(messages.get(i)).growX().left();
            messageTable.row();
        }
    }
}
