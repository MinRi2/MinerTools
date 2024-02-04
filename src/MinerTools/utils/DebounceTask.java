package MinerTools.utils;

import arc.util.*;
import arc.util.Timer.*;

public class DebounceTask{
    private final float delay;
    private final Runnable runnable;
    private final Timer timer;

    public DebounceTask(float delay, Runnable runnable){
        this.timer = new Timer();

        this.delay = delay;
        this.runnable = runnable;
    }

    public void run(){
        if(!timer.isEmpty()){
            timer.clear();
        }

        timer.scheduleTask(new Task(){
            @Override
            public void run(){
                runnable.run();
            }
        }, delay);
    }
}
