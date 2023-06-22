package MinerTools.input;

import arc.KeyBinds.*;
import arc.input.InputDevice.*;
import arc.input.*;

public enum ModBinding implements KeyBind{
    updateConveyor(KeyCode.altLeft, "MinerTools")
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
