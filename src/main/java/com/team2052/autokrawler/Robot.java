package com.team2052.autokrawler;


import com.team2052.autokrawler.auto.AutoModeRunner;
import com.team2052.autokrawler.auto.AutoModeSelector;
import com.team2052.autokrawler.auto.PurePursuitPathFollower;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;
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
        driveTrain.zeroGyro();
        robotstate.Init();
        AutoModeSelector.AutoModeDefinition currentAutoMode = AutoModeSelector.getAutoDefinition();
        autoModeRunner.setAutomode(currentAutoMode.getInstance());
        autoModeRunner.start();


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
        purePursuitPathFollower.update();
    }

    @Override
    public void teleopPeriodic() {
        driveTrain.drive(controls.getTankJoy1() , controls.getTurnJoy1());

        if(controls.reset()){
            driveTrain.zeroGyro();
            robotstate.Init();
        }
    }

    @Override
    public void testPeriodic() { }


}

