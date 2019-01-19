package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.Constants;
import com.team2052.autokrawler.RobotState;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.autokrawler.auto.paths.Path;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.RateLimiter;
import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class takes a path and has the robot follow it via pure persuit path following.
 * this means the robot has a point on the path that is a set distance away from the center of the robot
 * that it will try to curve into.
 *
 * @see #start(Path, boolean)
 * @see #update()
 *
 * @author Ian
 */
public class PurePursuitPathFollower{

    private static PurePursuitPathFollower instance = new PurePursuitPathFollower();
    private PurePursuitPathFollower() {}
    public static PurePursuitPathFollower getInstance() {
        return instance;
    }

    private Path path;

    private RateLimiter rateLimiter = new RateLimiter();
    private DriveTrain driveTrain = DriveTrain.getInstance();
    private RobotState robotState = RobotState.getInstance();

    private int closestPointIndex;
    private Position2d lookaheadPoint;
    private Position2d currentPos;
    private boolean isForward;

    private boolean ranOutOfPath = false;
    private double curvature;
    private double leftWheelVel;
    private double rightWheelVel;
    int loops = 0; //todo: find a better way of printing once a second

    /**
     * Update: runs all code for driving a path and sends updates to smartDashboard and the console
     *  @see #updateClosestPointIndex()
     *  @see #findLookAheadPoint()
     *  @see #findDriveCurvature()
     *  @see #driveWheels()
     */
    public void update() {
        if (path != null) {
            currentPos = robotState.getLatestPosition();
            updateClosestPointIndex();
            findLookAheadPoint();
            findDriveCurvature();
            driveWheels();
            loops++;
            if(loops >=10){ //run every fifth a second. this should be looped 50 times a second
                printAnUpdate();
                loops = 0;
            }
            pushToSmartDashboard();
        }else{
            System.out.println("NO PATH");
        }
    }

    /**
     * Start takes a path and runs it through the path creator class to set it up for following
     * @see #resetPathFollower()
     * @param path is a path created in an automode class
     */
    public void start(Path path) {
        this.path = path;
        resetPathFollower();
    }

    /**
     * resets all class level variables
     */
    private void resetPathFollower(){
        ranOutOfPath = false;
        lookaheadPoint = null;
        currentPos = null;
        curvature = 0;
        closestPointIndex = 0;
    }

    /**
     * find the point on the path that is the closest to the robot by checking distances from all points.
     */
    private void updateClosestPointIndex(){
        double distance;
        double closestDistance = Position2d.distanceFormula(path.getWaypoints().get(closestPointIndex).getPosition(), currentPos);

        for(int i = closestPointIndex; i < path.getWaypoints().size(); i++){
            distance = Position2d.distanceFormula(path.getWaypoints().get(i).getPosition(), currentPos);
            //System.out.println("DISTANCE: " + distance);
            if (distance <= closestDistance){
                closestDistance = distance;
            }else{
                closestPointIndex = i-1;
                //System.out.println("closest = " + (i-1));
                break;
            }
        }

    }

    /**
     * find a point that is both intersecting a circle radius kLookaheadDistance on the robot and the path
     *
     */
    private void findLookAheadPoint(){

        int i;
        double t = 0;

        Vector2d lineSegment = new Vector2d();

        for( i = closestPointIndex; t == 0 && i < path.getWaypoints().size()-1; i++) {
            // x = NextclosestPointX - closestPointX y = NextclosestPointY - closestPointY //todo: remove
            lineSegment = new Vector2d(path.getWaypoints().get(i + 1).getPosition().getLateral() - path.getWaypoints().get(i).getPosition().getLateral(), path.getWaypoints().get(i + 1).getPosition().getForward() - path.getWaypoints().get(i).getPosition().getForward());

            Vector2d robotToStartPoint = new Vector2d(path.getWaypoints().get(i).getPosition().getLateral() - currentPos.getLateral(), path.getWaypoints().get(i).getPosition().getForward() - currentPos.getForward());

            double a = dotProduct(lineSegment.x,lineSegment.y,lineSegment.x,lineSegment.y);
            double b = 2 * dotProduct(robotToStartPoint.x,robotToStartPoint.y,lineSegment.x,lineSegment.y);
            double c = dotProduct(robotToStartPoint.x,robotToStartPoint.y,robotToStartPoint.x,robotToStartPoint.y) - Constants.Autonomous.kLookaheadDistance * Constants.Autonomous.kLookaheadDistance;
            double discriminent = b*b - 4*a*c;

            if (discriminent < 0){
                System.out.println("NO INTERSECTION");
                ranOutOfPath = true;
            }else{
                discriminent = Math.sqrt(discriminent);
                double t1 = (-b - discriminent)/(2*a);
                double t2 = (-b + discriminent)/(2*a);

                if (t2 >= 0 && t2 <=1){
                    t = t2;
                }else if (t1 >= 0 && t1 <=1){
                    t = t1;
                }
                //System.out.println("% between 2 points lookahead pt is/ T: " + t);
            }
        }
        lookaheadPoint = new Position2d(path.getWaypoints().get(i).getPosition().getForward() + lineSegment.y * t, path.getWaypoints().get(i).getPosition().getLateral() + lineSegment.x * t);
    }

    /**
     * find the curvature of the circle that the robot must follow to get to the look ahead point
     */
    private void findDriveCurvature(){

        double a = -Math.tan(currentPos.getHeading());
        double b = 1;
        double c = Math.tan(currentPos.getHeading()) * currentPos.getForward() - currentPos.getLateral();

        double x = Math.abs(a * lookaheadPoint.getForward() + b * lookaheadPoint.getLateral() + c)/ Math.sqrt(a*a + b*b);


        double side = -Math.signum(Math.sin(currentPos.getHeading()) * (lookaheadPoint.getForward() - currentPos.getForward()) - Math.cos(currentPos.getHeading()) * (lookaheadPoint.getLateral() - currentPos.getLateral()));

        curvature = side * ((2*x)/ (Constants.Autonomous.kLookaheadDistance * Constants.Autonomous.kLookaheadDistance));
    }

    /**
     * calculate the velocity in inches for the left and right wheels then turn it into percent
     */
    private void driveWheels(){ //changed velocity to the closest+2
        //if we are moving at higher velocitys, at the end look father ahead to stop
        double deltaVelocity = rateLimiter.constrain(path.getWaypoints().get(closestPointIndex+((closestPointIndex >= path.getWaypoints().size()-5)?1:0)).getVelocity() - robotState.getVelocityInch(), -Constants.Autonomous.kMaxAccel * Constants.Autonomous.kloopPeriodMs, Constants.Autonomous.kMaxAccel * Constants.Autonomous.kloopPeriodMs);
        double velocity = robotState.getVelocityInch() +  deltaVelocity;
        leftWheelVel = velocity * (2 - curvature * Constants.Autonomous.kTrackWidth)/2;
        rightWheelVel = velocity * (2 + curvature * Constants.Autonomous.kTrackWidth)/2;

        //if velocity gets to fast scale it down so a wheel is not told to drive faster then 100%
        double highestVel = 0.0;

        highestVel = Math.max(highestVel, leftWheelVel);
        highestVel = Math.max(highestVel,rightWheelVel);
        if(highestVel > Constants.Autonomous.kMaxVelocity){
            double scaling = Constants.Autonomous.kMaxVelocity / highestVel;
            System.out.println("SCALING OUTPUTS DOWN");
            leftWheelVel*=scaling;
            rightWheelVel*=scaling;
        }

        double leftFeedForward = Constants.Autonomous.kV * leftWheelVel + Constants.Autonomous.kA * deltaVelocity;
        double rightFeedForward = Constants.Autonomous.kV * rightWheelVel + Constants.Autonomous.kA * deltaVelocity ;
        double leftFeedBack = Constants.Autonomous.kP * (leftWheelVel - robotState.getLeftVelocityInch());
        double rightFeedBack = Constants.Autonomous.kP * (rightWheelVel - robotState.getRightVelocityInch());

        double leftSpeed = leftFeedForward + leftFeedBack;
        double rightSpeed = rightFeedForward + rightFeedBack;

        //System.out.println("leftvel: " + leftWheelVel + "rightvel: " + rightWheelVel + "vel: " + velocity + "dv:" + deltaVelocity + "tarVel: " + path.getWaypoints().get(closestPointIndex).getVelocity());

        driveTrain.driveTank(leftSpeed, rightSpeed);
    }

    /**
     * find the dot product of  two vectors
     *
     * @param x1 vector1 x
     * @param y1 vector1 y
     * @param x2 vector2 x
     * @param y2 vector2 y
     * @return the dot product
     */
    public double dotProduct(double x1, double y1, double x2, double y2){
        return x1 * x2 + y1 * y2;
    }

    /**
     * Check if the robot is done with the path
     * @return true if
     * we are closer then the requiered distance,
     * or we are off the path,
     * or our closest point is after the end point
     */
    public boolean isPathComplete(){
        if(currentPos != null && path != null) {
            return getDistanceFromEnd() < Constants.Autonomous.kRequiredDistanceFromEnd || ranOutOfPath || closestPointIndex == path.getWaypoints().size()- Constants.Autonomous.kNumOfFakePts; //check if we are 6 inches from last point and we are done with the path
        }else {
            return false;
        }
    }

    /**
     * Stops the path following and sets path to null
     */
    public void stopPathFollower(){
        System.out.println("Stopping and deleting path");
        driveTrain.stop();
        path = null;
        ranOutOfPath = true;
    }

    /**
     * Checks the distance from the robot to the last point
     * @return distance in inches from last point
     */
    private double getDistanceFromEnd(){
        return Position2d.distanceFormula(path.getWaypoints().get(path.getWaypoints().size() - 4).getPosition(), currentPos); //todo: make sure -2 is correct
    }

    /**
     * put values to smartdashboard
     */
    private void pushToSmartDashboard(){
        SmartDashboard.putNumber("DistanceFromEnd", getDistanceFromEnd());
        SmartDashboard.putNumber("Curvature", curvature);
        SmartDashboard.putNumber("SetLeftVel", leftWheelVel);
        SmartDashboard.putNumber("SetRightVel", rightWheelVel);
        SmartDashboard.putNumber("SetRobotVel", (rightWheelVel + leftWheelVel)/2);
    }

    /**
     * prints and update to the console
     */
    private void printAnUpdate(){
        System.out.println("L-Vel: " + leftWheelVel + " R-Vel " + rightWheelVel + " curv: " + curvature + " in. Left: " + getDistanceFromEnd() + " P. Vel: " + path.getWaypoints().get(closestPointIndex).getVelocity());
    }
}
