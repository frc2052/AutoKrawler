package com.team2052.deepspace.auto.actions;

public class PrintAction implements Action{

    private String s;

    public PrintAction(String s){
        this.s = s;
    }

    @Override
    public void done() {
        System.out.println(s);
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
}
