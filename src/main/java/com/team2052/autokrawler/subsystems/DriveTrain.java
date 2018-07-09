package com.team2052.autokrawler.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2052.autokrawler.Constants;

public class DriveTrain {

    // Instance of DriveTrain class to be created in Robot.java class by running get instance
    private static DriveTrain singleDriveTrainInstance = new DriveTrain();
    private DriveTrain() {}
    public static DriveTrain getInstance() { return singleDriveTrainInstance; }

    private TalonSRX leftMotorF = new TalonSRX(Constants.DriveTain.kLeftFrontMotor);
    private TalonSRX leftMotorB = new TalonSRX(Constants.DriveTain.kLeftBackMotor);
    private TalonSRX rightMotorF = new TalonSRX(Constants.DriveTain.kRightFrontMotor);
    private TalonSRX rightMotorB = new TalonSRX(Constants.DriveTain.kRightBackMotor);

    public enum DriveModeVariableType { //enum variable type declaration
        MECANUM,
        ARCADE,
        SPLIT,
        TANK
    }

    //public DriveTrain(DriveModeVariableType driveMode) {this.driveMode = driveMode;} probably not needed

    public void drive(double tankJoy1, double turnJoy1, double tankJoy2, double turnJoy2, DriveModeVariableType driveMode) { //drives the motors depending
        // on the joystick values and the drive mode

        double frontLeftSpeed = 0;
        double backLeftSpeed = 0;
        double frontRightSpeed = 0;
        double backRightSpeed = 0;

        switch (driveMode) {
            case MECANUM:
                //"frontLeft" refers to motor location
                //The adding and subtracting of the three joystick values is the logic associated with mecanum drive
                frontLeftSpeed = checkbounds(tankJoy1 + turnJoy2 + turnJoy1); //left joystick moves forward and backward
                backLeftSpeed = checkbounds(tankJoy1 + turnJoy2 - turnJoy1);//left joystick strafes left/right
                frontRightSpeed = checkbounds(tankJoy1 - turnJoy2 - turnJoy1);//right joystick turns left/right
                backRightSpeed = checkbounds(tankJoy1 - turnJoy2 + turnJoy1);
                break;

            case ARCADE:
                frontLeftSpeed = tankJoy2 - turnJoy2; //left joystick turns and moves forward backward
                backLeftSpeed = tankJoy2 - turnJoy2;
                frontRightSpeed = tankJoy2 + turnJoy2;
                backRightSpeed = tankJoy2 + turnJoy2;
                break;

            case SPLIT:
                frontLeftSpeed = tankJoy1 - turnJoy2;//left joystick moves forward and backward
                backLeftSpeed = tankJoy1 - turnJoy2;//right joystick turns
                frontRightSpeed = tankJoy1 + turnJoy2;
                backRightSpeed = tankJoy1 + turnJoy2;
                break;

            case TANK:
                frontLeftSpeed = tankJoy1; //left joystick moves left wheels
                backLeftSpeed = tankJoy1;
                frontRightSpeed = tankJoy2;//right joystick moves right wheels
                backRightSpeed = tankJoy2;
                break;


        }
        leftMotorF.set(ControlMode.PercentOutput, frontLeftSpeed);
        leftMotorB.set(ControlMode.PercentOutput,backLeftSpeed);
        rightMotorF.set(ControlMode.PercentOutput,frontRightSpeed);
        rightMotorB.set(ControlMode.PercentOutput, backRightSpeed);

    }

    private double checkbounds(double Speed){ //this checks to make sure the speed is between 1 & -1
        if (Speed > 1){
            return 1.0;
        }else if(Speed < -1){
            return -1.0;
        }else{
            return Speed;
        }

    }
}

