package com.team2052.lib.Autonomous;


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
    public void estimatePosition (double leftInches, double rightInches, double radians) {

        double deltaDistance = ((leftInches-pastLeftInches) + (rightInches-pastRightInches)) / 2;
        double averageHeading = (radians + latestPosition.heading) / 2;
        pastRightInches = rightInches;
        pastLeftInches = leftInches;

        latestPosition.forward = deltaDistance * Math.cos(averageHeading) + latestPosition.forward;
        latestPosition.lateral = deltaDistance * Math.sin(averageHeading) + latestPosition.lateral;
        latestPosition.heading = radians;

        System.out.println("forward" + latestPosition.forward);
        System.out.println("lateral " + latestPosition.lateral);
        System.out.println("radians" + latestPosition.heading);
        System.out.println("degrees " + latestPosition.heading / 0.017453);
    }


    public Position2d getLatestPosition(){
        return latestPosition;
    }
}
