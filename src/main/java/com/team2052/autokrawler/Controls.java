package com.team2052.autokrawler;

import com.team2052.autokrawler.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.Joystick;

public class Controls {

    private static Controls instance = new Controls();

    private Controls() {
    }

    public static Controls getInstance() {
        return instance;
    }

    private Joystick primaryStick1 = new Joystick(0); //left joystick
    private Joystick primaryStick2 = new Joystick(1);//right joystick
    private Joystick secondaryStick = new Joystick(2);

    public double getTankJoy1() {
        return primaryStick1.getY();
    } //these return the values for the joysticks for other classes

    public double getTurnJoy1() {
        return primaryStick1.getX();
    }

    public double getTankJoy2() {
        return primaryStick2.getY();
    }

    public double getTurnJoy2() {
        return primaryStick2.getX();
    }

    public Boolean getDriveToggleButton() {
        return primaryStick2.getRawButton(Constants.Controls.kToggleDriveTrainButton);
    }

    public boolean getIntakeButton () {return secondaryStick.getRawButton(Constants.Controls.kIntakeIntakeButton);}

    public boolean getOutakeButton () {return secondaryStick.getRawButton(Constants.Controls.kIntakeOutakeButton);}


    private DriveTrain.DriveModeVariableType driveModeState = DriveTrain.DriveModeVariableType.MECANUM; //this holds our current drive mode
    private Boolean DriveTrainBool = false; //if this button in pressed in the previous cycle, it will save to make changes in the next cycle

    public DriveTrain.DriveModeVariableType getDriveTrainState() { //this changes the drive mode once when the button is pressed

        if (getDriveToggleButton() && DriveTrainBool == false) {
            DriveTrainBool = true;
            switch (driveModeState) {
                case MECANUM:
                    driveModeState = DriveTrain.DriveModeVariableType.ARCADE;
                    return DriveTrain.DriveModeVariableType.ARCADE;

                case ARCADE:
                    driveModeState = DriveTrain.DriveModeVariableType.SPLIT;
                    return DriveTrain.DriveModeVariableType.SPLIT;

                case SPLIT:
                    driveModeState = DriveTrain.DriveModeVariableType.TANK;
                    return DriveTrain.DriveModeVariableType.TANK;

                case TANK:
                    driveModeState = DriveTrain.DriveModeVariableType.MECANUM;
                    return DriveTrain.DriveModeVariableType.MECANUM;
            }
        } else if (!getDriveToggleButton()) {
            DriveTrainBool = false;
        }
        return driveModeState;
    }
}
