package com.team2052.lib.Autonomous;

/**
 * Created by KnightKrawler on 6/27/2018.
 */
public class Position2d {
    public double forward; //same as x position on graph where the robot starts facing positive x
    public double lateral; //same as y position on graph where right is negative y
    public double heading; //degrees
//    public double deltaDistance;

    public void Reset(){
        forward = 0;
        lateral = 0;
        heading = 0;
    }
}
