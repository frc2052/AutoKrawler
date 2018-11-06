package com.team2052.autokrawler.auto;

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


    public void setPath(Path path){
        this.path = new Path(pathCreator.createPath(path.getWaypoints()));
    }

    public void update() {

    }

    /**
     *
     */
    public void checkDistances(){
        double distance;
        double closestDistance = 1000;//todo: find better way

        for(int i = closestPointIndex; i < path.getWaypoints().size(); i++){
            distance = Position2d.distanceFormula(path.getWaypoints().get(i).position, robotState.getLatestPosition());
            if (distance < closestDistance){
                closestDistance = distance;
            }else{
                closestPointIndex = i;
                break;
            }
        }

    }

    public void findLookAheadPoint(){
        while(1 > 0) {
            // x = NextclosestPointX - closestPointX y = NextclosestPointY - closestPointY
            Vector2d lineSegment = new Vector2d(path.getWaypoints().get(closestPointIndex + 1).position.lateral - path.getWaypoints().get(closestPointIndex).position.lateral, path.getWaypoints().get(closestPointIndex + 1).position.forward - path.getWaypoints().get(closestPointIndex).position.forward);

            Vector2d robotToStartPoint = new Vector2d(path.getWaypoints().get(closestPointIndex).position.lateral - robotState.getLatestPosition().lateral, path.getWaypoints().get(closestPointIndex).position.forward - robotState.getLatestPosition().forward);

            double lookaheadDistance = 10;
            double a = dotProduct(lineSegment.x,lineSegment.x,lineSegment.y,lineSegment.y);
            double b = 2 * dotProduct(robotToStartPoint.x,lineSegment.x,robotToStartPoint.y,lineSegment.y);
            double c = dotProduct(robotToStartPoint.x,robotToStartPoint.x,robotToStartPoint.y,robotToStartPoint.y) - lookaheadDistance* lookaheadDistance;
            double discrimiminent = b*b - 4*a*c;
        }
    }

    public double dotProduct(double x1, double y1, double x2, double y2){
        return x1 * x2 + y1 * y2;
    }
}
