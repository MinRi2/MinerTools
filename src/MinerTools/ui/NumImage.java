package MinerTools.ui;

import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.ui.*;

public class NumImage extends Stack{

    public NumImage(TextureRegion region, int amount){
        add(new Table(null, i -> {
            i.left();
            i.image(region).size(32).scaling(Scaling.fit);
        }));

        if(amount != 0){
            add(new Table(null, t -> {
                t.left().bottom();
                t.add((amount >= 1000 ? UI.formatAmount(amount) : amount) + "").style(Styles.outlineLabel);
                t.pack();
            }));
        }
    }

}
