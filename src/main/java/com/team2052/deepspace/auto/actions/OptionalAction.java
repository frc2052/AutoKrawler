package com.team2052.deepspace.auto.actions;

public class OptionalAction implements Action {

    private Action interuptAction; //if true, run the optional Action
    private Action primaryAction; //if tr, finish this action unless the test action was triggered
    private Action failoverAction;
    private boolean activated = false;

    /**
     * ONLY USE IF YOU WANT TO ONLY CHECK TEST ACTION ONCE
     *
     * Most useful in a series action
     * @param interuptAction see interrupt Action
     * @param failoverAction see failoverAction
     */
    public OptionalAction(Action interuptAction, Action failoverAction){
        this.interuptAction = interuptAction;
        this.failoverAction = failoverAction;
    }

    /**
     * This is used to test test action until control action is true
     *
     * Most useful in a parallel action
     * @param interuptAction see interruptAction
     * @param failoverAction see failoverAction
     * @param primaryAction see primaryAction
     */
    public OptionalAction(Action interuptAction, Action failoverAction, Action primaryAction){
        this.interuptAction = interuptAction;
        this.failoverAction = failoverAction;
        this.primaryAction = primaryAction;
    }

    @Override
    public void done() {
        if(!activated){
            interuptAction.done();
            primaryAction.done();
        }else {
            failoverAction.done();
        }

    }

    /**
     * if test action has not been activated finish if control action is done
     * if it HAS been activated finish if our optional action is done
     *
     * if there is no control action, check test action. if we are not activated finish
     * @return true if we are done
     */
    @Override
    public boolean isFinished() {
        if(!activated) {
            if (primaryAction != null) {
                return (primaryAction.isFinished() && !interuptAction.isFinished());
            } else {
                update(); //check test action. if we are not activated finish
                return !activated;
            }
        }else{
            return failoverAction.isFinished();
        }
    }

    @Override
    public void start() {
        interuptAction.start();
        if(primaryAction != null) {
            primaryAction.start();
        }
    }

    /**
     * if we are not activated update our test and control action.
     * then if test action has been activated, start our optional action
     * then only update our failover action
     */
    @Override
    public void update() {
        if(!activated){
            interuptAction.update();
            if(primaryAction != null) {
                primaryAction.update();
            }
            activated = interuptAction.isFinished();
            if(activated){
                failoverAction.start();
                interuptAction.done();
                if(primaryAction != null) {
                    primaryAction.done();
                }
            }
        }else{
            failoverAction.update();
        }
    }
}
