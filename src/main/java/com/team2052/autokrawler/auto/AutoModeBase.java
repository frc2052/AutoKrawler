package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.Constants;
import com.team2052.autokrawler.auto.actions.Action;
import edu.wpi.first.wpilibj.Timer;


public abstract class AutoModeBase { //an abstract class cannot be run, only its subclasses

    private boolean running = false;
    private Timer timer = new Timer(); //creates a wpilib timer not java util


    protected abstract void init(); //protected means it can only be used by subclasses

    public boolean isRunning(){return running;}

    protected void runAction(Action action){ //this is a method used by a subclass to run an action. you must send it an action from the actions package
        isRunning(); //wtf does this do
        action.start();
        while (!action.isFinished() && running){ //while the action is not done and the automode is running
            action.update();
            try { //can throw an exception so you must check if it does so code doesn't crash
                Thread.sleep(Constants.Autonomous.kActionLoopTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        action.done();
    }

    public void start(){ //starts the automode
        System.out.println("Base Start");
        running = true;
        timer.reset();
        timer.start();
        init();
    }

    public void stop(){//stops the automode
        running = false;
        System.out.println("stopping auto");
        timer.stop();
    }


}
