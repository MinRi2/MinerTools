package MinerTools.graphics.draw.player;

import MinerTools.*;
import MinerTools.graphics.draw.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.Turret.*;

public class PlayerRange extends PlayerDrawer{

    @Override
    public boolean enabled(){
        return MinerVars.settings.getBool("playerRange");
    }

    @Override
    protected void draw(Player player){
        Draw.z(Layer.flyingUnitLow - 1f);

        Unit unit = player.unit();

        float range = unit.range();

        if(unit instanceof BlockUnitUnit blockUnit){
            Building building = blockUnit.tile();

            if(building instanceof TurretBuild turret){
                range = turret.range();
            }
        }

        Drawf.dashCircle(player.x, player.y, range, player.team().color);

        Draw.reset();
    }

}
