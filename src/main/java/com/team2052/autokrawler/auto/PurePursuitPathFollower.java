package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.Constants;
import com.team2052.autokrawler.RobotState;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.Autonomous.PathCreator;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.RateLimiter;
import edu.wpi.first.wpilibj.drive.Vector2d;

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

    private double curvature;

    public void update() {
        if (path != null) {
            currentPos = robotState.getLatestPosition();//where I actually am
            updateClosestPointIndex();
            findLookAheadPoint();
            findCurvature();
            driveWheels();
        }else{
            System.out.println("NO PATH ERROR");
        }
    }

    public void start(Path path) {
        closestPointIndex = 0;
        this.path = new Path(pathCreator.createPath(path.getWaypoints())); //more detailed path from smaller path
    }


    /**
     * find the point on the path that is the closest to the robot.
     */
    private void updateClosestPointIndex(){
        double distance = 0;
        double closestDistance = Position2d.distanceFormula(path.getWaypoints().get(closestPointIndex).position, currentPos);

        for(int i = closestPointIndex; i < path.getWaypoints().size(); i++){
            distance = Position2d.distanceFormula(path.getWaypoints().get(i).position, currentPos);
            if (distance < closestDistance){
                closestDistance = distance;
            }else{
                closestPointIndex = i;
                System.out.println("LINE 77    closest = " + i);
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
            }else{
                discriminent = Math.sqrt(discriminent);
                double t1 = (-b - discriminent)/(2*a);
                double t2 = (-b + discriminent)/(2*a);

                if (t1 >= 0 && t2 <=1){
                    t = t1;
                }

                if (t2 >= 0 && t1 <=1){
                    t = t2;
                }
            }
        }
        if(t == 0){
            System.out.println("LINE 121   t = 0");
        }
        lookaheadPoint = new Position2d(path.getWaypoints().get(i).position.forward + lineSegment.y * t, path.getWaypoints().get(i).position.lateral + lineSegment.x * t);
    }

    /**
     * find the curvature of the circle that the robot must follow to get to the look ahead point
     */
    private void findCurvature(){

        double a = -Math.tan(currentPos.heading);
        double b = 1;
        double c = Math.tan(currentPos.heading) * currentPos.lateral - currentPos.forward;

        double x = Math.abs(a * lookaheadPoint.lateral + b * lookaheadPoint.forward + c)/ Math.sqrt(a*a + b*b);


        double side = Math.signum(Math.sin(currentPos.heading) * (lookaheadPoint.lateral - currentPos.lateral) - Math.cos(currentPos.heading) * (lookaheadPoint.forward - currentPos.forward));

        curvature = side * ((2*x)/ (Constants.Autonomous.kLookaheadDistance * Constants.Autonomous.kLookaheadDistance));

    }

    /**
     * calculate the velocit in percent of the left and right wheels
     */
    private void driveWheels(){ //todo: set a minimum power to wheels
        double deltaVelocity = rateLimiter.constrain(path.getWaypoints().get(closestPointIndex).velocity - robotState.getVelocityInches(), -Constants.Autonomous.kMaxAccel * Constants.Autonomous.kloopPeriodMs, Constants.Autonomous.kMaxAccel * Constants.Autonomous.kloopPeriodMs);
        double velocity = robotState.getVelocityInches() +  deltaVelocity;
        double leftWheelVel = velocity * (2 + curvature * Constants.Autonomous.kTrackWidth)/2; //todo: figure this out or redo
        double rightWheelVel = velocity * (2 + curvature * Constants.Autonomous.kTrackWidth)/2; //changed + to -

        double leftFeedForward = Constants.Autonomous.kV * leftWheelVel + Constants.Autonomous.kA * deltaVelocity; //todo: have to understand ka and kp
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
        if(currentPos == null) {
            return Position2d.distanceFormula(path.getWaypoints().get(path.getWaypoints().size() - 1).position, currentPos) < 6; //check if we are 6 inches from last point
        }else {
            return false;
        }
    }
}
