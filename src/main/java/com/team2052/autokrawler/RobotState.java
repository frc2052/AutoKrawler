package com.team2052.autokrawler;


import com.team2052.lib.Autonomous.Position2d;

public class RobotState {

    static double pastLeftInches;
    static double pastRightInches;

    private Position2d latestPosition = new Position2d();

    private static RobotState singleRobotStateInstance = new RobotState();
    public static RobotState getInstance() { return singleRobotStateInstance; }

    public void Init(){
        latestPosition.Reset();
    }
    //send the change in position from the last point in inches and radians and add that to the previous position
    public void estimatePositionAverageHeading(double leftInches, double rightInches, double radians) {

        double deltaDistance = ((leftInches-pastLeftInches) + (rightInches-pastRightInches)) / 2;
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

    public Position2d forwardKinematics(double left_wheel_delta, double right_wheel_delta,
                                            double delta_rotation_rads) {
        final double dx = (left_wheel_delta + right_wheel_delta) / 2.0;
        return new Position2d(0.0, 0.0, 0.0);
    }

    /**
     * For convenience, forward kinematic with an absolute rotation and previous rotation.
     */
    public Position2d integrateForwardKinematics(Position2d current_pose,
                                                              Position2d forward_kinematics) {
        //return current_pose.transformBy(Position2d.exp(forward_kinematics));
        return null;
    }
}



