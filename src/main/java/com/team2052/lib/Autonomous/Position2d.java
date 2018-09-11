package com.team2052.lib.Autonomous;

/**
 * Created by KnightKrawler on 6/27/2018.
 */
public class Position2d {
    public double forward; //same as x position on graph where the robot starts facing positive x
    public double lateral; //same as y position on graph where right is negative y
    public double heading; //radians
//    public double deltaDistance;

    public Position2d(double forward, double lateral, double heading){
        this.forward = forward;
        this.lateral = lateral;
        this.heading = heading;
    }

    public Position2d(double heading){
        this.heading = heading;
    }

    public Position2d(double forward, double lateral){
        this.forward = forward;
        this.lateral = lateral;
    }

    public Position2d(){
        forward = 0.0;
        lateral = 0.0;
        heading = 0.0;
    }




    public void Reset(){
        forward = 0;
        lateral = 0;
        heading = 0;
    }

    /**
     * Transforming this RigidTransform2d means first translating by other.translation and then rotating by
     * other.rotation
     *
     * @param other
     *            The other transform.
     * @return This transform * other
     */
    public Position2d transformBy(Position2d other) {
//        return new Position2d(translation_.translateBy(other.translation_.rotateBy(rotation_)),
//                rotation_.rotateBy(other.rotation_));
        return null;
    }

    /**
     * The inverse of this transform "undoes" the effect of translating by this transform.
     *
     * @return The opposite of this transform.
     */
    public Position2d inverse() {
//        Position2d rotation_inverted = rotation_.inverse();
//        return new Position2d(translation_.inverse().rotateBy(rotation_inverted), rotation_inverted);
        return null;
    }

    /**
     * We can compose Translation2d's by adding together the x and y shifts.
     *
     * @param other
     *            The other translation to add.
     * @return The combined effect of translating by this object and the other.
     */
//    public Position2d translateBy(Position2d other) {
//        return new Position2d(x_ + other.x_, y_ + other.y_);
//    }

    /**
     * We can also rotate Translation2d's. See: https://en.wikipedia.org/wiki/Rotation_matrix
     *
     * @param rotation
     *            The rotation to apply.
     * @return This translation rotated by rotation.
     */
    public Position2d rotateBy(Position2d rotation) {
        return new Position2d(forward * rotation.cos() - lateral * rotation.sin(), forward* rotation.sin() + lateral * rotation.cos());
    }

    public double cos(){
        return Math.cos(heading);
    }

    public double sin(){
        return Math.sin(heading);
    }
}
