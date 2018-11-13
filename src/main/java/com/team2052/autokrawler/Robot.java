package com.team2052.autokrawler;


import com.team2052.autokrawler.auto.AutoModeRunner;
import com.team2052.autokrawler.auto.AutoModeSelector;
import com.team2052.autokrawler.auto.PurePursuitPathFollower;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.ControlLoop;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

    private DriveTrain driveTrain = DriveTrain.getInstance();
    private Controls controls = Controls.getInstance();
    private RobotState robotstate = RobotState.getInstance();
    private PurePursuitPathFollower purePursuitPathFollower = PurePursuitPathFollower.getInstance();
    private AutoModeRunner autoModeRunner = new AutoModeRunner();
    private ControlLoop controlLoop = new ControlLoop(Constants.Autonomous.kloopPeriod);


    private Path testPath;

    @Override
    public void robotInit() {
        controlLoop.addLoopable(robotstate);

        AutoModeSelector.putToSmartDashboard();
    }

    @Override
    public void disabledInit() {
        autoModeRunner.stop();
        controlLoop.stop();
        driveTrain.driveTank(0,0);
    }

    @Override
    public void autonomousInit() {
        System.out.println("1");
        controlLoop.start();
        System.out.println("2");
        driveTrain.zeroGyro();
        System.out.println("3");
        robotstate.Init();
        System.out.println("4");
        AutoModeSelector.AutoModeDefinition currentAutoMode = AutoModeSelector.getAutoDefinition();
        System.out.println("5");
        autoModeRunner.start(currentAutoMode.getInstance());


    }

    @Override
    public void teleopInit() {
        controlLoop.start();
        driveTrain.zeroGyro();
        robotstate.Init();


    }

    @Override
    public void testInit() { }


    @Override
    public void disabledPeriodic() { }
    
    @Override
    public void autonomousPeriodic() {
        purePursuitPathFollower.update();
    }

    @Override
    public void teleopPeriodic() {
        driveTrain.drive(controls.getTankJoy1() , controls.getTurnJoy1());

        if(controls.reset()){
            driveTrain.zeroGyro();
            robotstate.Init();
        }

        System.out.println("VELOCITY: "+ robotstate.getVelocityInches());
    }

    @Override
    public void testPeriodic() { }


}

