package com.team2052.lib.Autonomous;

/**
 * Created by KnightKrawler on 10/24/2018.
 */
public class Waypoint {

    private Position2d position;
    private double velocity;
    private double curvature; //1/radius of circle created by point ahead and behind
    private double distance;

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

    public Position2d getPosition() {
        return position;
    }

    public void setPosition(Position2d position) {
        this.position = position;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getCurvature() {
        return curvature;
    }

    public void setCurvature(double curvature) {
        this.curvature = curvature;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}

