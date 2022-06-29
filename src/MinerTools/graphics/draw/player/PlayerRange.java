package MinerTools.graphics.draw.player;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class PlayerRange extends PlayerDrawer{

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("playerRange");
    }

    @Override
    protected void draw(Player player){
        Draw.z(Layer.flyingUnitLow - 1f);

        Drawf.dashCircle(player.x, player.y, player.unit().range(), player.team().color);

        Draw.reset();
    }

}
