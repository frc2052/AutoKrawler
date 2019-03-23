package com.team2052.deepspace.auto.actions;

public interface Action { //this class is a base for alll autonomous actions such as spinning the intake or moving on a path
    void done();
    boolean isFinished();
    void start();
    void update();
}
