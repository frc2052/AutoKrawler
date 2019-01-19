package com.team2052.autokrawler.auto.actions;

public class HatchAction implements Action{

    public HatchAction(Mode mode){
        
    }
    @Override
    public void done() {

    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }

    public enum Mode{
        IN,
        OUT
    }
}
