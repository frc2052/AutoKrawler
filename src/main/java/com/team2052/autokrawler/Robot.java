package com.team2052.autokrawler;


import com.team2052.autokrawler.auto.PathFollower;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Position2d;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

    private static DriveTrain driveTrain = DriveTrain.getInstance();
    private static Controls controls = Controls.getInstance();
    private static RobotState robotstate = RobotState.getInstance();
    private static PathFollower pathFollower = PathFollower.getInstance();

    @Override
    public void robotInit() {

    }

    @Override
    public void disabledInit() { }

    @Override
    public void autonomousInit() {
        driveTrain.zeroGyro();
        robotstate.Init();
        pathFollower.simpleFollower(new Position2d(36,36,0.0));
    }

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
    public void autonomousPeriodic() {
        if (pathFollower.update()){
            System.out.println("REACHED POINT");
        }
    }

    @Override
    public void teleopPeriodic() {
        driveTrain.drive(controls.getTankJoy1() , controls.getTurnJoy1());

        if(controls.reset()){
            driveTrain.zeroGyro();
            robotstate.Init();
        }
        robotstate.estimatePositionAverageHeading((driveTrain.getLeftEncoder() / Constants.DriveTrain.kkTicksPerRot) * Constants.DriveTrain.kDriveWheelCircumferenceInches, (driveTrain.getRightEncoder() / Constants.DriveTrain.kkTicksPerRot) * Constants.DriveTrain.kDriveWheelCircumferenceInches, driveTrain.getGyroAngleDegrees()*0.017453);

    }

    @Override
    public void testPeriodic() { }



}

