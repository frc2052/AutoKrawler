package com.team2052.deepspace.auto.actions;

import com.team2052.deepspace.subsystems.ExampleLoopableSubsystemController;

public class ExampleEnumAction implements Action{
    private ExampleLoopableSubsystemController.State wantState;
    private ExampleLoopableSubsystemController exampleLoopableSubsystemController = ExampleLoopableSubsystemController.getInstance();

    public ExampleEnumAction(ExampleLoopableSubsystemController.State state){
        wantState = state;
    }
    @Override
    public void done() {

    }

    @Override
    public boolean isFinished() {
        return exampleLoopableSubsystemController.getCurrentState() == wantState;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
    }
}
