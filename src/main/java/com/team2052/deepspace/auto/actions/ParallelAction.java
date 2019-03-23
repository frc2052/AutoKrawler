package com.team2052.deepspace.auto.actions;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite action, running all sub-actions at the same mStartTime All actions are
 * started then updated until all actions report being done.
 *
 */
public class ParallelAction implements Action {

    private final ArrayList<Action> mActions;

    public ParallelAction(List<Action> actions) {
        //We don't want a reference of the list, we a copy
        //Just in case we have to use the list elsewhere in auto
        mActions = new ArrayList<>(actions.size());
        for (Action action : actions) {
            mActions.add(action);
        }
    }

    @Override
    public boolean isFinished() {
        boolean all_finished = true;
        for (Action action : mActions) {
            if (!action.isFinished()) {
                all_finished = false;
            }
        }
        return all_finished;
    }

    @Override
    public void update() {
        for (Action action : mActions) {
            action.update();
        }
    }

    @Override
    public void done() {
        for (Action action : mActions) {
            action.done();
        }
    }

    @Override
    public void start() {
        for (Action action : mActions) {
            action.start();
        }
    }
}