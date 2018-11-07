package com.team2052.autokrawler;


import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.ILoopable;

public class RobotState implements ILoopable{

    static double pastLeftInches;
    static double pastRightInches;
    private double deltaDistance;

    private double deltaLeftInches;
    private double deltaRightInches;

    private Position2d latestPosition = new Position2d();

    private DriveTrain driveTrain = DriveTrain.getInstance();

    private static RobotState singleRobotStateInstance = new RobotState();
    public static RobotState getInstance() { return singleRobotStateInstance; }

    public void Init(){
        latestPosition.Reset();
    }
    //send the change in position from the last point in inches and radians and add that to the previous position
    public void estimatePositionAverageHeading(double leftInches, double rightInches, double radians) {

        deltaLeftInches = leftInches-pastLeftInches;
        deltaRightInches = rightInches-pastRightInches;
        deltaDistance = ((deltaLeftInches) + (deltaRightInches)) / 2;
        double averageHeading = (radians + latestPosition.heading) / 2;
        pastRightInches = rightInches;
        pastLeftInches = leftInches;

        latestPosition.forward = deltaDistance * Math.cos(averageHeading) + latestPosition.forward;
        latestPosition.lateral = deltaDistance * Math.sin(averageHeading) + latestPosition.lateral;
        latestPosition.heading = radians;

        System.out.println("forward" + latestPosition.forward + "encoderInch: " + rightInches);
        System.out.println("lateral " + latestPosition.lateral);
        System.out.println("radians" + latestPosition.heading);
        System.out.println("degrees " + latestPosition.heading / 0.017453);
    }

    public void estimatePositionWithEncoders(double leftInches, double rightInches){

    }


    public Position2d getLatestPosition(){
        return latestPosition;
    }

    public double getVelocityInches(){
        return deltaDistance/Constants.Autonomous.kloopPeriod;
    }

    public double getLeftVelocityInch(){
        return deltaLeftInches/Constants.Autonomous.kloopPeriod;
    }

    public double getRightVelocityInch(){
        return deltaRightInches/Constants.Autonomous.kloopPeriod;
    }


    /**
     * For convenience, forward kinematic with an absolute rotation and previous rotation.
     */
    public Position2d integrateForwardKinematics(Position2d current_pose,
                                                              Position2d forward_kinematics) {
        //return current_pose.transformBy(Position2d.exp(forward_kinematics));
        return null;
    }

    @Override
    public void update() {
        estimatePositionAverageHeading((driveTrain.getLeftEncoder() / Constants.DriveTrain.kkTicksPerRot) * Constants.DriveTrain.kDriveWheelCircumferenceInches, (driveTrain.getRightEncoder() / Constants.DriveTrain.kkTicksPerRot) * Constants.DriveTrain.kDriveWheelCircumferenceInches, driveTrain.getGyroAngleRadians());
    }

    @Override
    public void onStart() {
        Init();
    }

    @Override
    public void onStop() {

    }
}



