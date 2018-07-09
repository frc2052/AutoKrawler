package com.team2052.autokrawler;


import com.team2052.autokrawler.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

    private static DriveTrain driveTrain = DriveTrain.getInstance();
    private static Controls controls = Controls.getInstance();

    @Override
    public void robotInit() {
    }

    @Override
    public void disabledInit() { }

    @Override
    public void autonomousInit() { }

    @Override
    public void teleopInit() {}

    @Override
    public void testInit() { }


    @Override
    public void disabledPeriodic() { }
    
    @Override
    public void autonomousPeriodic() { }

    @Override
    public void teleopPeriodic() {
        driveTrain.drive(controls.getTankJoy1(), controls.getTurnJoy1(), controls.getTankJoy2(), controls.getTurnJoy2(), DriveTrain.DriveModeVariableType.SPLIT);
    }

    @Override
    public void testPeriodic() { }



}

