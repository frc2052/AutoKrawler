package com.team2052.deepspace.auto;

import com.team2052.deepspace.Constants;
import com.team2052.deepspace.auto.actions.*;
import com.team2052.lib.Autonomous.Position2d;

import java.util.Arrays;

/**
 * This is for game specific code
 */
public abstract class AutoMode implements Runnable{

    protected Position2d startingPos;

    //TODO: REVIEW - Should a new "MasterAction" be created, with a HasBeenStarted property and the ability for it to cancel/abort?
    private SeriesAction action = null;

    public AutoMode(Position2d startPos)  {
        this.startingPos = startPos;
    }

    //This is where we create our action list using setAction
    protected abstract void init();

    protected void setAction(SeriesAction action){
        this.action = action;
    }

    public void appendAction(Action appendAction){
        if(action != null){
            action = new SeriesAction(Arrays.asList(
               action,
               appendAction
            ));
        }
    }

    public SeriesAction getAction(){
        return action;
    }

    //TODO: REVIEW - this seems dangerous. Should we force the developer to set a start direction, have a default be "invalid" to catch errors?
    //TODO: REVIEW - maybe we should use the same direction enum as Path
    private StartDirection startDirection = Constants.Autonomous.defaultStartDirection;

    protected void setStartDirection(StartDirection startDirection){this.startDirection = startDirection;}
    public StartDirection getStartDirection(){
        return startDirection;
    }


    //<editor-fold desc="Runnable Interface">

    private boolean running;

        @Override
    public void run () {
        if (action == null) {
            init();
            //System.out.println("AFTER INIT");
        }
        running = true;
        action.start();
        while (running && !action.isFinished()) { //while the action is not done and the automode is running
            action.update();
            try { //can throw an exception so you must check if it does so code doesn't crash
                Thread.sleep(Constants.Autonomous.kloopPeriodMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        action.done();
        running = false;
    }

    public void stop () {
        running = false;
        action.forceStop();
    }

    public boolean isRunning () {
        return running;
    }

    public boolean isActionFinished(){
        if(action != null) {
            return action.isFinished();
        }else{
            //not even started
            return false;
        }
    }
    //</editor-fold>



    //TODO: REVIEW - Consider using Path.Direction enum in place of this enum that has the exact same values
    public enum StartDirection{
        FORWARD(true),
        BACKWARD(false)
        ;

        public final boolean isForward;

        StartDirection(boolean isForward){
            this.isForward = isForward;
        }
    }


}
