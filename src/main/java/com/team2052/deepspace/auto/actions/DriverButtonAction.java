package com.team2052.deepspace.auto.actions;

import com.team2052.deepspace.Controls;

public class DriverButtonAction implements Action{

    private Controls controls;

    public DriverButtonAction(){
        controls = Controls.getInstance();
    }

    @Override
    public void done() {

    }

    @Override
    public boolean isFinished() {
        return controls.getAutoInterrupt();
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        System.out.println("WAITING FOR BUTTON");
    }


}
