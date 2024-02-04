package MinerTools.utils;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;

public class CameraUtils{
    private static final Seq<Request> tasks = new Seq<>();
    private static final Vec2 lastPos = new Vec2();
    private static Request currentTask;

    static{
        Events.run(Trigger.update, CameraUtils::updateTask);

        Events.on(ResetEvent.class, e -> clear());
    }

    private static void updateTask(){
        if(currentTask == null && tasks.any()){
            currentTask = tasks.remove(0);
            currentTask.onStarted();
        }

        if(currentTask != null && currentTask.update()){
            currentTask.onFinished();
            currentTask = null;

            // Remove the empty frame.
            updateTask();
        }
    }

    public static void clear(){
        tasks.clear();
        currentTask = null;
        lastPos.setZero();
    }

    public static void addTask(Request task){
        tasks.add(task);
    }

    public static void wait(float seconds){
        PanWait wait = new PanWait(seconds);
        addTask(wait);
    }

    public static PanRequest pan(Position pos, float speed, Interp interp){
        PanRequest request = new PanRequest(pos, speed, interp);
        addTask(request);
        return request;
    }

    public static PanRequest pan(Position pos){
        PanRequest request = new PanRequest(pos);
        addTask(request);
        return request;
    }

    public static PanRequest panWait(Vec2 pos, float seconds){
        PanRequest request = pan(pos);
        wait(seconds);
        return request;
    }

    public static void mark(){
        lastPos.set(Core.camera.position);
    }

    public static PanRequest panToLastMark(){
        return pan(lastPos);
    }

    public interface RequestCons{
        void cons(PanRequest request);
    }

    public static abstract class Request{
        protected void onStarted(){
        }

        protected void onFinished(){
        }

        protected abstract boolean update();
    }

    public static class PanWait extends Request{
        private final Timekeeper keeper;

        public PanWait(float seconds){
            keeper = new Timekeeper(seconds);
        }

        @Override
        public void onStarted(){
            keeper.reset();
        }

        @Override
        public boolean update(){
            return keeper.get();
        }
    }

    public static class PanRequest extends Request{
        private final Boolp inputLocker = () -> true;

        private final Vec2 pos = new Vec2();
        public float speed;
        public Interp interp;

        private @Nullable RequestCons requestUpdater;
        private @Nullable Runnable finished;

        public PanRequest(Position pos){
            set(pos);
        }

        public PanRequest(Position pos, float speed, Interp interp){
            set(pos, speed, interp);
        }

        @Override
        public void onStarted(){
            super.onStarted();

            Vars.control.input.addLock(inputLocker);
        }

        @Override
        public boolean update(){
            Core.camera.position.interpolate(pos, speed, interp);

            if(requestUpdater != null){
                requestUpdater.cons(this);
            }

            return Core.camera.position.epsilonEquals(pos, 1f);
        }

        @Override
        public void onFinished(){
            if(finished != null){
                finished.run();
            }

            Vars.control.input.inputLocks.remove(inputLocker, true);
        }

        public PanRequest onUpdated(RequestCons requestCons){
            this.requestUpdater = requestCons;
            return this;
        }

        public PanRequest onFinished(Runnable finished){
            this.finished = finished;
            return this;
        }

        public PanRequest set(Position pos){
            return set(pos, 0.02f, Interp.pow3Out);
        }

        public PanRequest set(Position pos, float speed, Interp interp){
            this.pos.set(pos);
            this.speed = speed;
            this.interp = interp;

            return this;
        }

    }

}
