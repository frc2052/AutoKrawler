package com.team2052.autokrawler;


import com.team2052.autokrawler.auto.AutoModeRunner;
import com.team2052.autokrawler.auto.AutoModeSelector;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.ControlLoop;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

    private DriveTrain driveTrain = DriveTrain.getInstance();
    private Controls controls = Controls.getInstance();
    private RobotState robotstate = RobotState.getInstance();
    private RobotStateCalculator robotStateCalculator = RobotStateCalculator.getInstance();
    private AutoModeRunner autoModeRunner = new AutoModeRunner();
    private ControlLoop controlLoop = new ControlLoop(Constants.Autonomous.kloopPeriodSec);


    private Path testPath;

    @Override
    public void robotInit() {
        controlLoop.addLoopable(robotStateCalculator);

        AutoModeSelector.putToSmartDashboard();
    }

    @Override
    public void disabledInit() {
        System.out.println("DISABLE INIT");
        autoModeRunner.stop();
        controlLoop.stop();
        driveTrain.driveTank(0,0);
    }

    @Override
    public void autonomousInit() {
        controlLoop.start();
        driveTrain.zeroGyro();
        robotStateCalculator.resetRobotState();
        AutoModeSelector.AutoModeDefinition currentAutoMode = AutoModeSelector.getAutoDefinition();
        autoModeRunner.start(currentAutoMode.getInstance());


    }


    @Override
    public void teleopInit() {
        controlLoop.start();
        driveTrain.zeroGyro();
        robotStateCalculator.resetRobotState();


    }

    @Override
    public void testInit() { }


    @Override
    public void disabledPeriodic() { }
    
    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopPeriodic() {
        driveTrain.drive(controls.getTankJoy1() , controls.getTurnJoy1());

        if(controls.reset()){
            driveTrain.zeroGyro();
            robotStateCalculator.resetRobotState();
        }

        System.out.println("VELOCITY: "+ robotstate.getVelocityInch());
    }

    @Override
    public void testPeriodic() { }


}

