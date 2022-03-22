package MinerTools.input;

import arc.KeyBinds.*;
import arc.input.*;
import arc.input.InputDevice.*;

public enum ModBinding implements KeyBind{
    buildBlocks(KeyCode.l, "MinerTools"),
    dropItem(KeyCode.h),
    updateConveyor(KeyCode.altLeft)
    ;

    private final KeybindValue defaultValue;
    private final String category;

    ModBinding(KeybindValue defaultValue, String category){
        this.defaultValue = defaultValue;
        this.category = category;
    }

    ModBinding(KeybindValue defaultValue){
        this(defaultValue, null);
    }

    @Override
    public KeybindValue defaultValue(DeviceType type){
        return defaultValue;
    }

    @Override
    public String category(){
        return category;
    }
}
