package com.team2052.deepspace.auto.actions;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite action, running all sub-actions at the same mStartTime All actions are
 * started then updated until all actions report being done.
 *
 */
public class ParallelWaitAction implements Action {

    private final ArrayList<Action> mActions;
    private final Action testAction;

    public ParallelWaitAction(List<Action> actions, Action testAction) {
        //We don't want a reference of the list, we a copy
        //Just in case we have to use the list elsewhere in auto
        mActions = new ArrayList<>(actions.size());
        for (Action action : actions) {
            mActions.add(action);
        }
        this.testAction = testAction;
    }

    @Override
    public boolean isFinished() {
        return testAction.isFinished();
    }

    @Override
    public void update() {
        for (Action action : mActions) {
            action.update();
        }
        testAction.update();
    }

    @Override
    public void done() {
        for (Action action : mActions) {
            action.done();
        }
        testAction.done();
    }

    @Override
    public void start() {
        for (Action action : mActions) {
            action.start();
        }
        testAction.start();
    }
}