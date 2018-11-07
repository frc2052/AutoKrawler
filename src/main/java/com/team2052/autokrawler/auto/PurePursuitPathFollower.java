package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.RobotState;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.Autonomous.PathCreator;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.RateLimiter;
import edu.wpi.first.wpilibj.drive.Vector2d;
import org.opencv.core.Mat;

/**
 * Created by KnightKrawler on 9/12/2018.
 */
public class PurePursuitPathFollower {

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

    private int closestPointIndex = 0;
    private Position2d lookaheadPoint;
    private Position2d currentPos;

    private double curvature;

    private double lookaheadDistance = 12; //12-25
    private double trackWidth = 10;
    private double kv;
    private double ka;
    private double kp;

    public void setPath(Path path){
        this.path = new Path(pathCreator.createPath(path.getWaypoints()));
    }

    public void update() {
        currentPos = robotState.getLatestPosition();
        checkDistances();
        findLookAheadPoint();
        findCurvature();
        driveWheels();
    }

    /**
     *
     */
    private void checkDistances(){
        double distance;
        double closestDistance = 1000;//todo: find better way

        for(int i = closestPointIndex; i < path.getWaypoints().size(); i++){
            distance = Position2d.distanceFormula(path.getWaypoints().get(i).position, currentPos);
            if (distance < closestDistance){
                closestDistance = distance;
            }else{
                closestPointIndex = i;
                break;
            }
        }

    }

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
            double c = dotProduct(robotToStartPoint.x,robotToStartPoint.x,robotToStartPoint.y,robotToStartPoint.y) - lookaheadDistance* lookaheadDistance;
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
            //todo: throw error
        }else {
            lookaheadPoint = new Position2d(path.getWaypoints().get(i).position.forward + lineSegment.y * t, path.getWaypoints().get(i).position.lateral + lineSegment.x * t);
        }
    }

    private void findCurvature(){

        double a = -Math.tan(currentPos.heading);
        double b = 1;
        double c = Math.tan(currentPos.heading) * currentPos.lateral - currentPos.forward;

        double x = Math.abs(a * lookaheadPoint.lateral + b * lookaheadPoint.forward + c)/ Math.sqrt(a*a + b*b);


        double side = Math.signum(Math.sin(currentPos.heading) * (lookaheadPoint.lateral - currentPos.lateral) - Math.cos(currentPos.heading) * (lookaheadPoint.forward - currentPos.forward));

        curvature = side * ((2*x)/ (lookaheadDistance * lookaheadDistance));

    }

    private void driveWheels(){
        double velocity = path.getWaypoints().get(closestPointIndex).velocity;   //todo: add rate limiter
        double leftWheelVel = velocity * (2 + curvature * trackWidth)/2;
        double rightWheelVel = velocity * (2 - curvature * trackWidth)/2;
        double angularVelocity;
        double leftFeedForward = kv * leftWheelVel + ka /*  * targetAccel */ ;
        double rightFeedForward = kv * rightWheelVel + ka /* * targetAccel */ ;
        double leftFeedBack = kp * (leftWheelVel /* measured veloocity*/);
        double rightFeedBack = kp * (rightWheelVel /* measured veloocity*/);

        double leftSpeed = leftFeedForward + leftFeedBack;
        double rightSpeed = rightFeedForward + rightFeedBack;
        driveTrain.driveTank(leftSpeed, rightSpeed);
    }

    public double dotProduct(double x1, double y1, double x2, double y2){
        return x1 * x2 + y1 * y2;
    }
}
