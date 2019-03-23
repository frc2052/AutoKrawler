package com.team2052.deepspace.auto;

import com.team2052.deepspace.auto.actions.SeriesAction;
import edu.wpi.first.wpilibj.Timer;


public class AutoModeRunner {
    private Timer timer = new Timer();
    private Thread thread = null;
    private AutoMode autoMode;
    private boolean hasStarted = false;

    private static AutoModeRunner instance = null;
    public static AutoModeRunner getInstance() {
        if (instance == null) {
            try {
                instance = new AutoModeRunner();
            } catch (Exception exc) {
                System.out.println("DANGER: Failed to create AutoodeRunner: " + exc.getMessage());
                exc.printStackTrace();
            }
        }
        return instance;
    }


    public void start(AutoMode autoMode) {//Initializes auto mode
        stop();
        this.autoMode = autoMode;
        //System.out.println("is action ! null: " + (action != null));
        if(autoMode != null) {
            //TODO: REVIEW - what is this timer for?
            timer.reset();
            timer.start();
            //In java, a thread will run only as long as the Runnable it is created with is running
            //as soon as the runnable exits is run() method, the thread dies
            thread = new Thread(autoMode);
            thread.start();
        }
    }

    public void stop() {//Stops auto mode
        if(autoMode != null) {
            autoMode.stop();
        }
        autoMode = null;
        thread = null;
    }

      public boolean isFinished(){
        try {
            return !autoMode.isRunning();
        }catch(Exception e){
            return true;
        }
    }
}
