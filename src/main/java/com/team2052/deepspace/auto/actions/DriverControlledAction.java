package com.team2052.deepspace.auto.actions;

import com.team2052.deepspace.Controls;
import com.team2052.deepspace.RobotState;
import com.team2052.deepspace.subsystems.DriveTrainController;
import com.team2052.lib.DriveHelper;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriverControlledAction implements Action{

    private DriveTrainController driveTrain;
    private Controls controls;
    private DriveHelper driveHelper;
    private RobotState robotstate;
    private boolean wasPressed = false;
    private boolean finished = false;

    private boolean intakeToggle;

    public DriverControlledAction(boolean startToggle){
        driveTrain = DriveTrainController.getInstance();
        controls = Controls.getInstance();
        driveHelper = new DriveHelper();
        robotstate = RobotState.getInstance();
        intakeToggle = startToggle;
    }


    @Override
    public void done() {

    }

    @Override
    //TODO: Make a button and ask Wat wat he likes
    public boolean isFinished() {
        return controls.getHatchOuttake();
    }

    @Override
    public void start() {

    }

    @Override
    public void update() { driverControlled(); }

    private void driverControlled(){

        driveTrain.drive(driveHelper.drive(controls.getDriveTank(), controls.getDriveTurn(), controls.getQuickTurn()));

        robotstate.outputToSmartDashboard();
        driveTrain.setHighGear(controls.getShift());
        //legClimberController.printEncoder();

    }
}
