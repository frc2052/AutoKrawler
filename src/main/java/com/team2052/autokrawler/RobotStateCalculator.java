package com.team2052.autokrawler;

import com.team2052.autokrawler.subsystems.DriveTrain;
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

    private double lastVels[] = new double[3];
    private DriveTrain driveTrain = DriveTrain.getInstance();
    private RobotState robotState = RobotState.getInstance();

    private static RobotStateCalculator singleRobotStateCalculatorInstance = new RobotStateCalculator();
    public static RobotStateCalculator getInstance() { return singleRobotStateCalculatorInstance; }

    private void estimatePositionAverageHeading(double leftInches, double rightInches, double radians) {

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
        System.out.println("forward" + latestPosition.forward + "encoderInch: " + rightInches);
        System.out.println("lateral " + latestPosition.lateral);
        System.out.println("radians" + latestPosition.heading);
        System.out.println("degrees " + latestPosition.heading / 0.017453);
*/
    }

    public void resetRobotState(){
        latestPosition.reset();
        driveTrain.resetEncoders();
        pastLeftInches = 0;
        pastRightInches = 0;
        timeSinceReset = 0;

    }

    @Override
    public void update() {
        estimatePositionAverageHeading((driveTrain.getLeftEncoder() / Constants.DriveTrain.kkTicksPerRot) * Constants.DriveTrain.kDriveWheelCircumferenceInches, (driveTrain.getRightEncoder() / Constants.DriveTrain.kkTicksPerRot) * Constants.DriveTrain.kDriveWheelCircumferenceInches, driveTrain.getGyroAngleRadians());
        robotState.setVelocityInch((lastVels[0] + lastVels[1] + lastVels[2])/3);
        robotState.setLeftVelocityInch(deltaLeftInches);
        robotState.setRightVelocityInch(deltaRightInches);
        robotState.setLatestPosition(latestPosition);
        timeSinceReset+= Constants.Autonomous.kloopPeriodSec; //todo: test if accurate
        robotState.setTimeSinceReset(timeSinceReset);
    }

    @Override
    public void onStart() {
        latestPosition.reset();
    }

    @Override
    public void onStop() {

    }
}
