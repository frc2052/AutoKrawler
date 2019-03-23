package com.team2052.deepspace;

import com.team2052.deepspace.auto.AutoMode;
import com.team2052.deepspace.auto.AutoModeRunner;
import com.team2052.deepspace.auto.AutoModeSelector;
import com.team2052.deepspace.auto.PurePursuitPathFollower;
import com.team2052.deepspace.subsystems.*;
import com.team2052.lib.ControlLoop;
import com.team2052.lib.DriveHelper;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.TimedRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private DriveHelper driveHelper = null;
    private Controls controls = null;
    private DriveTrainController driveTrain = null;
    private RobotState robotstate = RobotState.getInstance();
    private RobotStateCalculator robotStateCalculator = RobotStateCalculator.getInstance();
    private AutoModeRunner autoModeRunner = null;
    private ControlLoop controlLoop = new ControlLoop(Constants.Autonomous.kloopPeriodSec);
    private Compressor compressor = null;



    @Override
    public void robotInit() {
        driveHelper = new DriveHelper();
        controls = Controls.getInstance();
        driveTrain = DriveTrainController.getInstance();
        //add the calculator to the control loop
        controlLoop.addLoopable(robotStateCalculator);

        autoModeRunner = AutoModeRunner.getInstance();
        try {
            compressor = new Compressor();
            compressor.setClosedLoopControl(true);
        } catch (Exception exc) {
            System.out.println("DANGER: No compressor!");
        }
        AutoModeSelector.putToShuffleBoard();

        AutoModeSelector.nullSelectedAutoMode();
    }

    /**
     * This function is called every robot packet, no matter the mode. Use
     * this for items like diagnostics that you want ran during disabled,
     * autonomous, teleoperated and test.
     *
     * <p>This runs after the mode specific periodic functions, but before
     * LiveWindow and SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {

    }

    /**
        This function called once when autonomous starts
     */
    @Override
    public void autonomousInit() {
        //start the control loop
        controlLoop.start();
        driveTrain.zeroGyro();

        AutoMode currentMode = AutoModeSelector.getSelectedAutoMode();
        System.out.println("selected :" + currentMode.getClass().getName());
        //use the instance to get direction and position
        //todo: make one direction enum
        robotStateCalculator.setStartDirection(currentMode.getStartDirection().isForward);
        robotStateCalculator.resetRobotState(AutoModeSelector.getStartingPos());
        System.out.println("starting x: " + robotstate.getLatestPosition().getLateral() + " y: "+ robotstate.getLatestPosition().getForward());
        //start running the auto mode
        autoModeRunner.start(currentMode);
    }

    /**
     * This function is called periodically during autonomous.
     */

    @Override
    public void autonomousPeriodic() {
        robotstate.outputToSmartDashboard();
        if(controls.getAutoOverride()){
            autoModeRunner.stop();
            driveTrain.stop();
        }
        //System.out.println("is auto done: " + autoModeRunner.isFinished());

        if(autoModeRunner.isFinished()){
            driverControlled();
        }
    }

    /**
     This function called once when teleoperated starts
     */
    @Override
    public void teleopInit(){
        AutoModeSelector.nullSelectedAutoMode();
        robotStateCalculator.resetRobotState();
        controlLoop.start();
        driveTrain.zeroGyro();
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        driverControlled();
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
    }

    @Override
    public void disabledPeriodic(){
        autoModeRunner.stop();
        controlLoop.stop();
        driveTrain.stop();
        AutoModeSelector.checkSelectedAutoMode();
        PurePursuitPathFollower.getInstance().resetPathFollower();
    }

    private void driverControlled(){

        driveTrain.drive(driveHelper.drive(controls.getDriveTank(), controls.getDriveTurn(), controls.getQuickTurn()));
        robotstate.outputToSmartDashboard();
        driveTrain.setHighGear(controls.getShift());
    }
}
