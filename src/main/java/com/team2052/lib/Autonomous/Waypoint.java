package com.team2052.lib.Autonomous;

/**
 * Created by KnightKrawler on 10/24/2018.
 */
public class Waypoint {

    public Position2d position;
    public double velocity;
    public double curvature; //1/radius of circle created by point ahead and behind
    public double distance; //todo: is this used

    /**
     * @param position in inches
     * @param velocity in inches per second
     */
    public Waypoint(Position2d position, double velocity) {
        this.position = position;
        this.velocity = velocity;
        distance = 0;
        curvature = 0;
    }
}

