package com.team2052.deepspace.auto.actions;
import com.team2052.deepspace.auto.PurePursuitPathFollower;

public class WaitForPathFlagAction implements Action{

    private PurePursuitPathFollower purePursuitPathFollower = null;
    private String flag;

    public WaitForPathFlagAction(String flag){
        this.flag = flag;
    }

    @Override
    public void done() {

    }

    @Override
    public boolean isFinished() {
        return purePursuitPathFollower.currentFlag().equals(flag);
    }

    @Override
    public void start() {
        purePursuitPathFollower = PurePursuitPathFollower.getInstance();
    }

    @Override
    public void update() {

    }
}
