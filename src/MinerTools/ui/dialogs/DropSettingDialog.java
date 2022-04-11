package MinerTools.ui.dialogs;

import MinerTools.io.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.defense.turrets.*;

public class DropSettingDialog extends BaseDialog{
    public static ObjectMap<ItemTurret, Seq<Item>> settings = new ObjectMap<>();

    public DropSettingDialog(){
        super("Setting", Styles.defaultDialog);

        DropManager.init();
        DropManager.loadSetting(settings);

        hidden(() -> DropManager.save(settings));
        shown(() -> {
            settings.clear();
            DropManager.loadSetting(settings);

            rebuild();
        });

        addCloseButton();
    }

    private void rebuild(){
        cont.clear();

        Table table = new Table();
        for(Entry<ItemTurret, Seq<Item>> entry : settings){
            ItemTurret turret = entry.key;
            Seq<Item> items = entry.value;

            table.table(Tex.buttonOver, t -> {
                Table[] itemsTable = new Table[1];

                t.image(turret.uiIcon).size(48).left();

                t.button("R", () -> {
                    items.clear();
                    items.addAll(turret.ammoTypes.keys().toSeq());

                    itemsRebuild(itemsTable[0], items);
                }).size(48).padLeft(3);

                t.add().width(-1).growX();
                t.table(Styles.none, tt -> {
                    itemsTable[0] = tt;
                    itemsRebuild(itemsTable[0], items);
                }).pad(0);
            }).pad(3).fillX();

            table.row();
        }

        cont.pane(table).maxHeight(48 * 15).fill().scrollX(false);
    }

    private void itemsRebuild(Table itemsT, Seq<Item> items){
        itemsT.clear();

        for(Item item : items){
            itemsT.table(itemT -> {
                itemT.image(item.uiIcon).size(32).left();

                itemT.button(Icon.cancelSmall, Styles.emptyi, () -> {
                    if(items.remove(item, true)){
                        itemsRebuild(itemsT, items);
                    }
                }).width(24).fillY().padLeft(5);

                itemT.button(Icon.upSmall, Styles.emptyi, () -> {
                    int index = items.indexOf(item);
                    if(index != 0){
                        items.swap(index, index - 1);
                        itemsRebuild(itemsT, items);
                    }
                }).width(24).fillY().padLeft(5);
            }).pad(3).growX();
        }
    }
}
