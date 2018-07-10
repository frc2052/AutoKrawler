package com.team2052.autokrawler;


import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.RobotState;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

    private static DriveTrain driveTrain = DriveTrain.getInstance();
    private static Controls controls = Controls.getInstance();
    private static RobotState robotstate = RobotState.getInstance();

    @Override
    public void robotInit() {

    }

    @Override
    public void disabledInit() { }

    @Override
    public void autonomousInit() { }

    @Override
    public void teleopInit() {
        driveTrain.zeroGyro();
        robotstate.Init();
    }

    @Override
    public void testInit() { }


    @Override
    public void disabledPeriodic() { }
    
    @Override
    public void autonomousPeriodic() { }

    @Override
    public void teleopPeriodic() {
        driveTrain.drive(controls.getTankJoy1() , controls.getTurnJoy2());

        if(controls.reset()){
            driveTrain.zeroGyro();
            robotstate.Init();
        }
        robotstate.estimatePosition((driveTrain.getLeftEncoder() / Constants.DriveTrain.kkTicksPerRot) * Constants.DriveTrain.kDriveWheelDiameterInches, (driveTrain.getRightEncoder() / Constants.DriveTrain.kkTicksPerRot) * Constants.DriveTrain.kDriveWheelDiameterInches, driveTrain.getGyroAngleDegrees()*0.017453);

    }

    @Override
    public void testPeriodic() { }



}

