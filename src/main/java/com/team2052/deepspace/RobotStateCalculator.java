package com.team2052.deepspace;

import com.team2052.deepspace.subsystems.DriveTrainController;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.ILoopable;

public class RobotStateCalculator implements ILoopable{

    private double pastLeftInches = 0;
    private double pastRightInches = 0;
    private double deltaDistance = 0;

    private double deltaLeftInches = 0;
    private double deltaRightInches = 0;

    private Position2d latestPosition = new Position2d();

    private double timeSinceReset = 0;
    private boolean startForward = true;

    private double lastVels[] = new double[3];
    private DriveTrainController driveTrain = DriveTrainController.getInstance();
    private RobotState robotState = RobotState.getInstance();

    private static RobotStateCalculator singleRobotStateCalculatorInstance = new RobotStateCalculator();
    public static RobotStateCalculator getInstance() { return singleRobotStateCalculatorInstance; }

    public void setStartDirection(boolean startForward){
        this.startForward = startForward;
    }

    private void estimatePositionAverageHeading(double leftInches, double rightInches, double radians) {

        if(!startForward){
            radians = radians + Math.PI;
        }

        deltaLeftInches = leftInches-pastLeftInches;
        deltaRightInches = rightInches-pastRightInches;
        deltaDistance = (deltaLeftInches + deltaRightInches) / 2;
        double averageHeading = (radians + latestPosition.getHeading()) / 2;
        pastRightInches = rightInches;
        pastLeftInches = leftInches;
        lastVels[0] = lastVels[1];
        lastVels[1] = lastVels[2];
        lastVels[2] = deltaDistance;

        latestPosition.setForward(deltaDistance * Math.cos(averageHeading) + latestPosition.getForward());
        latestPosition.setLateral(deltaDistance * Math.sin(averageHeading) + latestPosition.getLateral());
        latestPosition.setHeading(radians);
/*
        System.out.println("forward" + latestPosition.getForward() + "encoderInch: " + rightInches);
        System.out.println("lateral " + latestPosition.getLateral());
        System.out.println("radians" + latestPosition.getHeading());
        System.out.println("degrees " + latestPosition.getHeading() / 0.017453);
*/
    }

    public void resetRobotState(){
        latestPosition.reset();
        driveTrain.resetEncoders();
        pastLeftInches = 0;
        pastRightInches = 0;
        timeSinceReset = 0;

    }

    public void resetRobotState(Position2d pos){
        //latestPosition.reset();
        latestPosition.setForward(pos.getForward());
        latestPosition.setLateral(pos.getLateral());
        latestPosition.setHeading(0);
        driveTrain.resetEncoders();
        pastLeftInches = 0;
        pastRightInches = 0;
        timeSinceReset = 0;
        robotState.setLatestPosition(latestPosition);
    }
    @Override
    public void update() {
        estimatePositionAverageHeading((driveTrain.getLeftEncoder() / Constants.DriveTrain.kTicksPerRot) * Constants.DriveTrain.kDriveWheelCircumferenceInches * Constants.DriveTrain.kEncoderGearRatio, (driveTrain.getRightEncoder() / Constants.DriveTrain.kTicksPerRot) * Constants.DriveTrain.kDriveWheelCircumferenceInches * Constants.DriveTrain.kEncoderGearRatio, driveTrain.getGyroAngleRadians());
        robotState.setVelocityInch((lastVels[0] + lastVels[1] + lastVels[2])/3);
        robotState.setLeftVelocityInch(deltaLeftInches);
        robotState.setRightVelocityInch(deltaRightInches);
        robotState.setLatestPosition(latestPosition);
        timeSinceReset+= Constants.Autonomous.kloopPeriodSec; //todo: test if accurate
        robotState.setTimeSinceReset(timeSinceReset);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {

    }
}
