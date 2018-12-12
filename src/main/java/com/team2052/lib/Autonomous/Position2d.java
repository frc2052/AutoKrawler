package com.team2052.lib.Autonomous;

/**
 * Created by KnightKrawler on 6/27/2018.
 */
public class Position2d {

    private double forward; //same as x position on graph where the robot starts facing positive x
    private double lateral; //same as y position on graph where right is negative y
    private double heading; //radians


    /**
     * create a position2d with an x,y and theta
     * @param forward y value
     * @param lateral x value
     * @param heading angle
     */
    public Position2d(double forward, double lateral, double heading){
        this.forward = forward;
        this.lateral = lateral;
        this.heading = heading;
    }

    /**
     * create a position2d with an x and y only
     * @param forward y value
     * @param lateral x value
     */
    public Position2d(double forward, double lateral){
        this.forward = forward;
        this.lateral = lateral;
    }

    /**
     * create a position2d with an angle
     * @param heading angle
     */
    public Position2d(double heading){
        this.heading = heading;
    }

    /**
     * create a position2d with x, y and theta equal to 0
     */
    public Position2d(){
        forward = 0.0;
        lateral = 0.0;
        heading = 0.0;
    }

    public double getForward() {
        return forward;
    }

    public void setForward(double forward) {
        this.forward = forward;
    }

    public double getLateral() {
        return lateral;
    }

    public void setLateral(double lateral) {
        this.lateral = lateral;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public void reset(){
        forward = 0;
        lateral = 0;
        heading = 0;
    }

    /**
     * get the hypotenuse of a triangle with sides forward and lateral
     * @return hypotenuse of the triangle
     */
    public double getHype(){
        return Math.sqrt(forward * forward + lateral * lateral);
    }

    /**
     * rotate a position 2d
     * @param rotation an agle to rate by
     * @return a rotated position2d
     */
    public Position2d rotateBy(Position2d rotation) {
        return new Position2d(forward * rotation.cos() - lateral * rotation.sin(), forward* rotation.sin() + lateral * rotation.cos());
    }

    /**
     * translate a position2d
     * @param other the amount to translate by
     * @return a translated position2d
     */
    public Position2d translateBy(Position2d other){
        return new Position2d(this.forward + other.forward, this.lateral + other.lateral);
    }

    public double cos(){
        return Math.cos(heading);
    }

    public double sin(){
        return Math.sin(heading);}


    public void setAngleFromTrig(){
        heading = Math.atan(forward/lateral);
    }

    public static double distanceFormula(Position2d first, Position2d second){
        return Math.sqrt(Math.pow(second.lateral - first.lateral,2) + Math.pow(second.forward - first.forward,2));
    }
}
