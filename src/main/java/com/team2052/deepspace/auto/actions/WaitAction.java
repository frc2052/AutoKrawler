package com.team2052.deepspace.auto.actions;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 */
public class WaitAction implements Action{

    private double timeToWait;
    private double startTime;

    public WaitAction(double timeToWait){
        this.timeToWait = timeToWait;
    }

    @Override
    public void done() {

    }

    @Override
    public boolean isFinished() {
        if(Timer.getFPGATimestamp() - startTime >= timeToWait){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    public void update() {

    }
}
