package com.team2052.lib.Autonomous;


public class RobotState {

    int pastLeftInches;
    int pastRightInches;

    private Position2d latestPosition = new Position2d();

    //send the change in position from the last point in inches and radians and add that to the previous position
    public void estimatePosition (int leftInches, int rightInches, double radians) {

        double deltaDistance = ((leftInches-pastLeftInches) + (rightInches-pastRightInches)) / 2;
        double averageHeading = (radians + latestPosition.heading) / 2;
        pastRightInches = rightInches;
        pastLeftInches = leftInches;

        latestPosition.forward = deltaDistance * Math.cos(averageHeading) + latestPosition.forward;
        latestPosition.lateral = deltaDistance * Math.sin(averageHeading) + latestPosition.lateral;
        latestPosition.heading = radians;

    }


    public Position2d getLatestPosition(){
        return latestPosition;
    }
}
