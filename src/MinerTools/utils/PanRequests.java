package MinerTools.utils;

import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;

public class PanRequests{
    private static final Seq<PanTask> tasks = new Seq<>();
    private static PanTask currentTask;
    
    private static final Vec2 lastPos = new Vec2();

    static{
        Events.run(Trigger.update, () -> {
            if(currentTask == null){
                if(tasks.any()){
                    currentTask = tasks.remove(0);
                    currentTask.start();
                }
            }else{
                currentTask.update();
                if(currentTask.shouldNext()){
                    currentTask.finished();
                    currentTask = null;
                }
            }
        });
        
        Events.on(ResetEvent.class, e -> {
            clear();
        });
    }

    public static void clear(){
        tasks.clear();
        currentTask = null;
        lastPos.setZero();
    }

    public static void addTask(PanTask task){
        tasks.add(task);
    }

    public static void wait(float seconds){
        PanWait wait = new PanWait(seconds);
        addTask(wait);
    }

    public static PanRequest panTo(Vec2 pos, float speed, Interp interp){
        PanRequest request = new PanRequest(pos, speed, interp);
        addTask(request);
        return request;
    }

    public static PanRequest panTo(Vec2 pos){
        PanRequest request = new PanRequest(pos);
        addTask(request);
        return request;
    }

    public static PanRequest panWait(Vec2 pos, float seconds){
        PanRequest request = panTo(pos);
        wait(seconds);
        return request;
    }
    
    public static void markCamera(){
        lastPos.set(Core.camera.position);
    }
    
    public static PanRequest panToLastMark(){
        return panTo(lastPos);
    }

    public static abstract class PanTask{
        public void start(){
        }

        public void finished(){
        }

        public  void update(){
        }

        public abstract boolean shouldNext();
    }

    public static class PanWait extends PanTask{
        private final Timekeeper keeper;

        public PanWait(float seconds){
            keeper = new Timekeeper(seconds);
        }

        @Override
        public void start(){
            keeper.reset();
        }

        @Override
        public boolean shouldNext(){
            return keeper.get();
        }
    }

    public static class PanRequest extends PanTask{
        private final Vec2 pos = new Vec2();

        private @Nullable RequestUpdater update;
        private @Nullable Runnable finished;

        public float speed;
        public Interp interp;

        public PanRequest(Vec2 pos){
            set(pos);
        }

        public PanRequest(Vec2 pos, float speed, Interp interp){
            set(pos, speed, interp);
        }
        
        public void update(){
            Core.camera.position.interpolate(pos, speed, interp);

            if(update != null){
                update.update(this);
            }
        }
        
        public void finished(){
            if(finished != null){
                finished.run();
            }
        }

        @Override
        public boolean shouldNext(){
            return Core.camera.position.epsilonEquals(pos, 1f);
        }

        public void setUpdate(RequestUpdater update){
            this.update = update;
        }

        public void setFinished(Runnable finished){
            this.finished = finished;
        }

        public PanRequest set(Vec2 pos){
            return set(pos, 0.02f, Interp.pow3Out);
        }

        public PanRequest set(Vec2 pos, float speed, Interp interp){
            this.pos.set(pos);
            this.speed = speed;
            this.interp = interp;

            return this;
        }
        
    }

    public interface RequestUpdater{
        void update(PanRequest request);
    }

}
