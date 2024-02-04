package MinerTools.utils;

import arc.util.*;
import arc.util.Timer.*;

/**
 * 函数防抖
 * 一段时间后执行，若期间再次执行，重置倒计时
 * @author minri2
 */
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
