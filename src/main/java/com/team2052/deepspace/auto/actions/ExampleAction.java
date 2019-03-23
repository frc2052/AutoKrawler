package com.team2052.deepspace.auto.actions;

import com.team2052.deepspace.subsystems.ExampleSubsystemController;
import edu.wpi.first.wpilibj.Timer;

public class ExampleAction implements Action{

    private ExampleSubsystemController exampleSubsystemController = ExampleSubsystemController.getInstance();
    private double speed;
    private double time;
    private Timer timer;
    private double startValue = 0;

    public ExampleAction(double speed, double time){
        this.speed = speed;
        this.time = time;
        timer  = new Timer();
    }

    @Override
    public void done() {

    }

    @Override
    public boolean isFinished() {
        //run for time seconds
        return timer.get()-startValue > time;
    }

    @Override
    public void start() {
        timer.start();
        startValue = timer.get();
    }

    @Override
    public void update() {
        exampleSubsystemController.setSpeed(speed);
    }
}
