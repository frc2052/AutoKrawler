package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.Constants;
import com.team2052.autokrawler.RobotState;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.Autonomous.PathCreator;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.RateLimiter;
import com.team2052.lib.ILoopable;
import edu.wpi.first.wpilibj.drive.Vector2d;

/**
 * Created by KnightKrawler on 9/12/2018.
 */
public class PurePursuitPathFollower implements ILoopable{

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

    public void setPath(Path path){
        this.path = new Path(pathCreator.createPath(path.getWaypoints()));
    }


    @Override
    public void update() {
        if (path != null) {
            System.out.println("PATH IS NOT NULL");
            currentPos = robotState.getLatestPosition();
            checkDistances();
            findLookAheadPoint();
            findCurvature();
            driveWheels();
        }
    }

    @Override
    public void onStart() {
        resetPathFollower();

    }

    @Override
    public void onStop() {

    }

    /**
     * find the point on the path that is the closest to the robot.
     */
    private void checkDistances(){
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

        for( i = closestPointIndex; t == 0 && i < path.getWaypoints().size(); i++) {
            // x = NextclosestPointX - closestPointX y = NextclosestPointY - closestPointY
            lineSegment = new Vector2d(path.getWaypoints().get(i + 1).position.lateral - path.getWaypoints().get(i).position.lateral, path.getWaypoints().get(i + 1).position.forward - path.getWaypoints().get(i).position.forward);

            Vector2d robotToStartPoint = new Vector2d(path.getWaypoints().get(i).position.lateral - currentPos.lateral, path.getWaypoints().get(i).position.forward - currentPos.forward);

            double a = dotProduct(lineSegment.x,lineSegment.x,lineSegment.y,lineSegment.y);
            double b = 2 * dotProduct(robotToStartPoint.x,lineSegment.x,robotToStartPoint.y,lineSegment.y);
            double c = dotProduct(robotToStartPoint.x,robotToStartPoint.x,robotToStartPoint.y,robotToStartPoint.y) - Constants.Autonomous.kLookaheadDistance * Constants.Autonomous.kLookaheadDistance;
            double discriminent = b*b - 4*a*c;

            if (discriminent < 0){
                //no intersection
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
        }else {
            lookaheadPoint = new Position2d(path.getWaypoints().get(i).position.forward + lineSegment.y * t, path.getWaypoints().get(i).position.lateral + lineSegment.x * t);
        }
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
    private void driveWheels(){
        double deltaVelocity = rateLimiter.constrain(path.getWaypoints().get(closestPointIndex).velocity - robotState.getVelocityInches(), -Constants.Autonomous.kMaxAccel, Constants.Autonomous.kMaxAccel);
        double velocity = robotState.getVelocityInches() +  deltaVelocity;
        double leftWheelVel = velocity * (2 + curvature * Constants.Autonomous.kTrackWidth)/2;
        double rightWheelVel = velocity * (2 - curvature * Constants.Autonomous.kTrackWidth)/2;

        double leftFeedForward = Constants.Autonomous.kV * leftWheelVel + Constants.Autonomous.kA * deltaVelocity ;
        double rightFeedForward = Constants.Autonomous.kV * rightWheelVel + Constants.Autonomous.kA * deltaVelocity ;
        double leftFeedBack = Constants.Autonomous.kP * (leftWheelVel - robotState.getLeftVelocityInch());
        double rightFeedBack = Constants.Autonomous.kP * (rightWheelVel - robotState.getRightVelocityInch());

        double leftSpeed = leftFeedForward + leftFeedBack;
        double rightSpeed = rightFeedForward + rightFeedBack;

        driveTrain.driveTank(leftSpeed, rightSpeed);
    }

    public double dotProduct(double x1, double y1, double x2, double y2){
        return x1 * x2 + y1 * y2;
    }

    public boolean isPathComplete(){
        return false;
    }

    public void resetPathFollower(){
        closestPointIndex = 0;
    }
}
