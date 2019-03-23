package com.team2052.deepspace.subsystems;

import com.team2052.lib.ILoopable;
import edu.wpi.first.wpilibj.Solenoid;

public class ExampleLoopableSubsystemController implements ILoopable {
    private State currentState;
    private State targetState;
    private Solenoid upSolenoid; //up if true
    private Solenoid outSolenoid; //out if true

    private static ExampleLoopableSubsystemController ExampleLoopableInstance = new ExampleLoopableSubsystemController();
    public static ExampleLoopableSubsystemController getInstance() { return ExampleLoopableInstance; }

    public ExampleLoopableSubsystemController(){
        currentState = State.DOWNIN;
        upSolenoid = new Solenoid(0);
        outSolenoid = new Solenoid(0);
    }
    @Override
    public void update() {
        switch (currentState){
            case DOWNOUT:
                switch (targetState){
                    case DOWNIN:
                    case UPIN:
                    case UPOUT:
                        outSolenoid.set(false);
                        currentState = State.DOWNIN;
                        break;
                    default:
                        break;
                }
                break;
            case DOWNIN:
                switch (targetState){
                    case DOWNOUT:
                        outSolenoid.set(true);
                        currentState = State.DOWNOUT;
                        break;
                    case UPIN:
                    case UPOUT:
                        upSolenoid.set(true);
                        currentState = State.UPIN;
                        break;
                }
                break;
            case UPIN:
                switch (targetState){
                    case UPOUT:
                        outSolenoid.set(true);
                        currentState = State.UPOUT;
                        break;
                    case DOWNIN:
                    case DOWNOUT:
                        upSolenoid.set(false);
                        currentState = State.DOWNIN;
                        break;
                }
                break;
            case UPOUT:
                switch (targetState){
                    case UPIN:
                    case DOWNIN:
                    case DOWNOUT:
                        outSolenoid.set(false);
                        currentState = State.UPOUT;
                        break;
                }
                break;
        }
    }

    @Override
    public void onStart() {
        outSolenoid.set(false);
        upSolenoid.set(false);
        currentState = State.DOWNIN;
    }

    @Override
    public void onStop() {

    }

    public State getCurrentState() {
        return currentState;
    }

    public void setTargetState(State state){
        targetState = state;
    }

    public enum State{
        DOWNOUT,
        DOWNIN,
        UPIN,
        UPOUT
    }
}
