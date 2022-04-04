package MinerTools.ai.types;

import MinerTools.ai.*;
import MinerTools.ui.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class PlayerFollowAI extends PlayerAI{
    private int lastPlayerSize;

    private Player follow;

    /* display */
    private Table playerSelect;
    private ScrollPane pane;

    public PlayerFollowAI(){
        super(new TextureRegionDrawable(UnitTypes.oct.uiIcon));

        pane = new ScrollPane(playerSelect = new Table(), Styles.nonePane);

        MUI.panes.add(pane);

        setupPlayersTable();
    }

    @Override
    protected void update(){
        super.update();

        if(follow != null && follow.unit().isNull()){
            follow = null;
        }
    }

    @Override
    public void updateMovement(){
        if(follow == null) return;

        if(!Vars.player.within(follow, follow.unit().hitSize() + 10 * Vars.tilesize)){
            moveTo(follow, follow.unit().hitSize() + 10 * Vars.tilesize, 20f);
        }else{
            if(unit.mineTile != null){
                circle(unit.mineTile, unit.type.miningRange / 2f, 0.5f);
            }else{
                circle(follow, follow.unit().hitSize() + 10 * Vars.tilesize, 0.5f);
            }
        }

        if(follow.unit().isBuilding()){
            Vars.player.unit().plans().clear();
            Vars.player.unit().plans().addFirst(follow.unit().plans.first());
        }

        if(follow.unit().mining()){
            Vars.player.unit().mineTile(follow.unit().mineTile());
        }
    }

    @Override
    public void display(Table table){
        table.add(pane).maxHeight(Vars.iconXLarge * 5).growX().get();
    }

    private void setupPlayersTable(){
        playerSelect.update(() -> {
            if(lastPlayerSize != Groups.player.size()){
                lastPlayerSize = Groups.player.size();
                rebuildPlayersTable();
            }
        });
    }

    private void rebuildPlayersTable(){
        playerSelect.clear();

        int index = 0;
        for(Player player : Groups.player){
            if(player.team() != Vars.player.team()) continue;

            playerSelect.image().update(i -> i.setColor(player.team().color)).growY();

            playerSelect.button(b -> {
                b.table(Tex.pane, i -> i.image(player::icon).size(Vars.iconXLarge).grow()).left();
                b.labelWrap(player.coloredName()).padLeft(5f).growX();
            }, MStyles.clearToggleAccentb, () -> resetFollow(player)).growX().minWidth(225f).checked(b -> follow == player);

            if(++index % 2 == 0) playerSelect.row();
        }
    }

    private void resetFollow(Player player){
        if(follow == player){
            follow = null;
        }else{
            follow = player;
        }
    }
}
