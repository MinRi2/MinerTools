package MinerTools.ui;

import arc.graphics.*;
import arc.math.*;
import arc.scene.actions.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class BorderColorImage extends BorderImage{

    public BorderColorImage(){
        super(Tex.whiteui);
    }

    public void colorAction(Color color){
        colorAction(color, 0.5f, Interp.smooth);
    }

    public void colorAction(Color color, float duration, Interp interp){
        addAction(Actions.color(color, duration, interp));
    }
}
