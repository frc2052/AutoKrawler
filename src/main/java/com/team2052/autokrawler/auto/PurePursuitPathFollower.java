package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.Constants;
import com.team2052.autokrawler.RobotState;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.RateLimiter;
import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by KnightKrawler on 9/12/2018.
 */
public class PurePursuitPathFollower{

    private static PurePursuitPathFollower instance = new PurePursuitPathFollower();
    private PurePursuitPathFollower() {}
    public static PurePursuitPathFollower getInstance() {
        return instance;
    }

    private Path path;

    private RateLimiter rateLimiter = new RateLimiter();
    private PathCreator pathCreator = new PathCreator();
    private DriveTrain driveTrain = DriveTrain.getInstance();
    private RobotState robotState = RobotState.getInstance();

    private int closestPointIndex;
    private Position2d lookaheadPoint;
    private Position2d currentPos;

    private boolean ranOutOfPath = false;
    private double curvature;
    private double leftWheelVel;
    private double rightWheelVel;

    public void update() {
        if (path != null) {
            currentPos = robotState.getLatestPosition();//where I actually am
            updateClosestPointIndex();
            findLookAheadPoint();
            findCurvature();
            driveWheels();
        }else{
            System.out.println("NO PATH");
        }
    }

    public void start(Path path) {
        ranOutOfPath = false;
        lookaheadPoint = null;
        currentPos = null;
        curvature = 0;
        closestPointIndex = 0;
        System.out.println("creating path");
        this.path = new Path(pathCreator.createPath(path.getWaypoints())); //more detailed path from smaller path
        System.out.println("created path");
    }


    /**
     * find the point on the path that is the closest to the robot.
     */
    private void updateClosestPointIndex(){
        double distance;
        double closestDistance = Position2d.distanceFormula(path.getWaypoints().get(closestPointIndex).position, currentPos);

        for(int i = closestPointIndex; i < path.getWaypoints().size(); i++){
            distance = Position2d.distanceFormula(path.getWaypoints().get(i).position, currentPos);
            System.out.println("DISTANCE: " + distance);
            if (distance <= closestDistance){
                closestDistance = distance;
            }else{
                closestPointIndex = i-1;
                System.out.println("closest = " + (i-1));
                break;
            }
        }

    }

    /**
     * find a point that is both intersecting a circle radius kLookaheadDistance on the robot and the path
     */
    private void findLookAheadPoint(){

        int i;
        double t = 0;

        Vector2d lineSegment = new Vector2d();

        for( i = closestPointIndex; t == 0 && i < path.getWaypoints().size()-1; i++) {
            // x = NextclosestPointX - closestPointX y = NextclosestPointY - closestPointY
            lineSegment = new Vector2d(path.getWaypoints().get(i + 1).position.lateral - path.getWaypoints().get(i).position.lateral, path.getWaypoints().get(i + 1).position.forward - path.getWaypoints().get(i).position.forward);

            Vector2d robotToStartPoint = new Vector2d(path.getWaypoints().get(i).position.lateral - currentPos.lateral, path.getWaypoints().get(i).position.forward - currentPos.forward);

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
                    System.out.println("T IS T2");
                    SmartDashboard.putString("T was not set","false");
                    t = t2;
                }else if (t1 >= 0 && t1 <=1){
                    System.out.println("T IS T1");
                    SmartDashboard.putString("T was not set","false");
                    t = t1;
                }

                System.out.println("% between 2 points lookahead pt is/ T: " + t);
            }
        }
        lookaheadPoint = new Position2d(path.getWaypoints().get(i).position.forward + lineSegment.y * t, path.getWaypoints().get(i).position.lateral + lineSegment.x * t);
    }

    /**
     * find the curvature of the circle that the robot must follow to get to the look ahead point
     */
    private void findCurvature(){ //todo: does this treat forward like an x axis swapped x &ys

        double a = -Math.tan(currentPos.heading);
        double b = 1;
        double c = Math.tan(currentPos.heading) * currentPos.forward - currentPos.lateral;

        double x = Math.abs(a * lookaheadPoint.forward + b * lookaheadPoint.lateral + c)/ Math.sqrt(a*a + b*b);


        double side = -Math.signum(Math.sin(currentPos.heading) * (lookaheadPoint.forward - currentPos.forward) - Math.cos(currentPos.heading) * (lookaheadPoint.lateral - currentPos.lateral)); //todo swapped forawrd/lateral

        curvature = side * ((2*x)/ (Constants.Autonomous.kLookaheadDistance * Constants.Autonomous.kLookaheadDistance));
        System.out.println("curvature: " + curvature + " X: " + x);
    }

    /**
     * calculate the velocit in percent of the left and right wheels
     */
    private void driveWheels(){
        System.out.println("Closest V: " + path.getWaypoints().get(closestPointIndex).velocity );
        System.out.println("Vel" + robotState.getVelocityInch());
        double deltaVelocity = rateLimiter.constrain(path.getWaypoints().get(closestPointIndex).velocity - robotState.getVelocityInch(), -Constants.Autonomous.kMaxAccel * Constants.Autonomous.kloopPeriodMs, Constants.Autonomous.kMaxAccel * Constants.Autonomous.kloopPeriodMs);
        double velocity = robotState.getVelocityInch() +  deltaVelocity;
        leftWheelVel = velocity * (2 - curvature * Constants.Autonomous.kTrackWidth)/2;
        rightWheelVel = velocity * (2 + curvature * Constants.Autonomous.kTrackWidth)/2;

        //scale to stop wheels from driving to fast
        double highestVel = 0.0;

        highestVel = Math.max(highestVel, leftWheelVel);
        highestVel = Math.max(highestVel,rightWheelVel);
        if(highestVel > Constants.Autonomous.kMaxVelocity){
            System.out.println(highestVel + " is Greater then " + Constants.Autonomous.kMaxVelocity);
            double scaling = Constants.Autonomous.kMaxVelocity / highestVel;
            leftWheelVel*=scaling;
            rightWheelVel*=scaling;
        }

        double leftFeedForward = Constants.Autonomous.kV * leftWheelVel + Constants.Autonomous.kA * deltaVelocity;
        double rightFeedForward = Constants.Autonomous.kV * rightWheelVel + Constants.Autonomous.kA * deltaVelocity ;
        double leftFeedBack = Constants.Autonomous.kP * (leftWheelVel - robotState.getLeftVelocityInch());
        double rightFeedBack = Constants.Autonomous.kP * (rightWheelVel - robotState.getRightVelocityInch());

        double leftSpeed = leftFeedForward + leftFeedBack;
        double rightSpeed = rightFeedForward + rightFeedBack;

        System.out.println("leftvel: " + leftWheelVel + "rightvel: " + rightWheelVel + "vel: " + velocity + "dv:" + deltaVelocity + "tarVel: " + path.getWaypoints().get(closestPointIndex).velocity);

        driveTrain.driveTank(leftSpeed, rightSpeed);
    }

    public double dotProduct(double x1, double y1, double x2, double y2){
        return x1 * x2 + y1 * y2;
    }

    public boolean isPathComplete(){
        if(currentPos != null && path != null) {
            System.out.println("distence from end: " + distanceFromEnd());
            return distanceFromEnd() < 9 || ranOutOfPath || closestPointIndex == path.getWaypoints().size()- Constants.Autonomous.kNumOfFakePts; //check if we are 6 inches from last point and we are done with the path
        }else {
            return false;
        }
    }

    public void deletePath(){
        //System.out.println("Stopping and deleting path");
        driveTrain.stop();
        path = null;
    }

    private double distanceFromEnd(){
        return Position2d.distanceFormula(path.getWaypoints().get(path.getWaypoints().size() - 2).position, currentPos);
    }

    private void pushToSmartDashboard(){
        SmartDashboard.putNumber("DistanceFromEnd", distanceFromEnd());
        SmartDashboard.putNumber("Curvature", curvature);
        SmartDashboard.putNumber("LeftWheelVel", leftWheelVel);
        SmartDashboard.putNumber("RightWheelVel", rightWheelVel);
    }

    private void printAnUpdate(){
        System.out.println("L-Vel: ");
    }
}
