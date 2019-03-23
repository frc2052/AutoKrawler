package com.team2052.deepspace.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class ExampleSubsystemController {
    private TalonSRX motor;

    private static ExampleSubsystemController exampleSubsystemControllerInstance = new ExampleSubsystemController();
    public static ExampleSubsystemController getInstance() { return exampleSubsystemControllerInstance; }


    public ExampleSubsystemController(){
        motor = new TalonSRX(5);
    }

    public void setSpeed(double speed){
        motor.set(ControlMode.PercentOutput, speed);
    }

    public void stop(){
        setSpeed(0);
    }
}
