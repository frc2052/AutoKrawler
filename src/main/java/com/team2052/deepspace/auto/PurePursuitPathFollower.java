package com.team2052.deepspace.auto;

import com.team2052.deepspace.Constants;
import com.team2052.deepspace.RobotState;
import com.team2052.deepspace.auto.paths.Path;
import com.team2052.deepspace.subsystems.DriveTrainController;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.RateLimiter;
import com.team2052.lib.DriveSignal;
import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class takes a path and has the robot follow it via pure persuit path following.
 * this means the robot has a point on the path that is a set distance away from the center of the robot
 * that it will try to curve into.
 *
 * @see #start(Path)
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
    private Path overridePath;
    private boolean pathOveridden = false;
    private boolean visionPathing = false;

    private RateLimiter rateLimiter = new RateLimiter();
    private DriveTrainController driveTrain = DriveTrainController.getInstance();
    private RobotState robotState = RobotState.getInstance();

    private int closestPointIndex;
    private Position2d lookaheadPoint;
    private Position2d currentPos;

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
            if(pathOveridden){
                path = overridePath;
                closestPointIndex = 0;
                pathOveridden = false;
            }
            driveTrain.setHighGear(path.getIsHighGear());
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
        resetPathFollower();
        this.path = path;
        driveTrain.setHighGear(path.getIsHighGear());
    }

    /**
     * resets all class level variables
     */
    public void resetPathFollower(){
        path = null;
        ranOutOfPath = false;
        lookaheadPoint = null;
        currentPos = null;
        curvature = 0;
        closestPointIndex = 0;
        visionPathing = false;
    }

    public void OverrideCurrentPath(Path path){
        overridePath = path;
        pathOveridden =true;
        visionPathing = true;
        System.out.println("OVERRIDING PATH");
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
     * 1.draw a circle around the robot
     * 2. find where it intersects the path
     * 3. find the one in front of the robot
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
                System.out.println("NO INTERSECTION" + closestPointIndex + " " + path.getWaypoints().get(closestPointIndex).getPosition().getForward() + " " + path.getWaypoints().get(closestPointIndex).getPosition().getLateral());
                if(!visionPathing) {
                    ranOutOfPath = true;
                }
            }else{
                discriminent = Math.sqrt(discriminent);
                double t1 = (-b - discriminent)/(2*a);
                double t2 = (-b + discriminent)/(2*a);

                if (t2 >= 0 && t2 <=1){
                    t = t2;
                }else if (t1 >= 0 && t1 <=1){
                    t = t1;
                }
                System.out.println(closestPointIndex + " " + path.getWaypoints().get(closestPointIndex).getPosition().getForward() + " " + path.getWaypoints().get(closestPointIndex).getPosition().getLateral());

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

        //curvature is 1/radius of the circle the robot must drive on
        curvature = side * ((2*x)/ (Constants.Autonomous.kLookaheadDistance * Constants.Autonomous.kLookaheadDistance));
        System.out.println("curvature " + curvature);
    }

    /**
     * calculate the velocity in inches for the left and right wheels then turn it into percent
     */
    private void driveWheels(){ //changed velocity to the closest+2
        //if we are moving at higher velocitys, at the end look father ahead to stop
        double deltaVelocity = rateLimiter.constrain(path.getWaypoints().get(closestPointIndex+((closestPointIndex >= path.getWaypoints().size()-5)?1:0)).getVelocity() - robotState.getVelocityInch(), -Constants.Autonomous.kMaxAccel * Constants.Autonomous.kloopPeriodMs, Constants.Autonomous.kMaxAccel * Constants.Autonomous.kloopPeriodMs);
        double velocity = robotState.getVelocityInch() +  deltaVelocity;
        leftWheelVel = velocity * (2 + curvature * Constants.Autonomous.kTrackWidth)/2;
        rightWheelVel = velocity * (2 - curvature * Constants.Autonomous.kTrackWidth)/2; //todo swap + & - if the robot turns away from the path
        System.out.println("left: " + leftWheelVel + " right: " + rightWheelVel + " velocity: " + velocity + " curv: " + curvature + " track: " + Constants.Autonomous.kTrackWidth);
        //if velocity gets to fast scale it down so a wheel is not told to drive faster then 100%
        double highestVel = 0.0;

        highestVel = Math.max(highestVel, Math.abs(leftWheelVel));
        highestVel = Math.max(highestVel, Math.abs(rightWheelVel));

        //scale speed down if the robot goes faster then the speed given at a point
        if(highestVel > path.getWaypoints().get(closestPointIndex).getVelocity()){
            double scaling = Math.abs(path.getWaypoints().get(closestPointIndex).getVelocity()) / highestVel;
            System.out.println("SCALING OUTPUTS DOWN");
            leftWheelVel*=scaling;
            rightWheelVel*=scaling;

            if(Math.abs(leftWheelVel) < .05 && Math.abs(rightWheelVel) < .05){
                leftWheelVel = Math.signum(path.getWaypoints().get(0).getVelocity()) * .05;
                rightWheelVel = Math.signum(path.getWaypoints().get(0).getVelocity()) * .05;
            }
            System.out.println("scaling: " + scaling + " closest point: " + closestPointIndex + " vel: " + path.getWaypoints().get(closestPointIndex).getVelocity() + " highest value: " + highestVel);
            System.out.println("left: " + leftWheelVel + " right: " + rightWheelVel);
        }

        double leftFeedForward = (path.getIsHighGear() ? Constants.Autonomous.kHighGearkV : Constants.Autonomous.kV) * leftWheelVel; //+ Constants.Autonomous.kA * deltaVelocity;
        double rightFeedForward = (path.getIsHighGear() ? Constants.Autonomous.kHighGearkV : Constants.Autonomous.kV) * rightWheelVel;// + Constants.Autonomous.kA * deltaVelocity ;
        double leftFeedBack =0;// Constants.Autonomous.kP * (leftWheelVel - robotState.getLeftVelocityInch());
        double rightFeedBack =0;// Constants.Autonomous.kP * (rightWheelVel - robotState.getRightVelocityInch());

        double leftSpeed = leftFeedForward + leftFeedBack;
        double rightSpeed = rightFeedForward + rightFeedBack;
        System.out.println("leftSpeed: " + leftSpeed + " rightSpeed: " + rightSpeed);

        //System.out.println("leftvel: " + leftWheelVel + "rightvel: " + rightWheelVel + "vel: " + velocity + "dv:" + deltaVelocity + "tarVel: " + path.getWaypoints().get(closestPointIndex).getVelocity());

        driveTrain.drive(new DriveSignal(leftSpeed, rightSpeed));
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
        if(!currentFlag().equals("")) {
            SmartDashboard.putString("current flag", currentFlag());
        }
    }

    /**
     * prints and update to the console
     */
    private void printAnUpdate(){
        System.out.println("L-Vel: " + leftWheelVel + " R-Vel " + rightWheelVel + " curv: " + curvature + " in. Left: " + getDistanceFromEnd() + " P. Vel: " + path.getWaypoints().get(closestPointIndex).getVelocity());
    }

    public String currentFlag(){
        return path.getWaypoints().get(closestPointIndex).getFlag();
    }
}
