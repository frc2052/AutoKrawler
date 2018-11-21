package com.team2052.lib.Autonomous;

import com.team2052.autokrawler.Constants;
import edu.wpi.first.wpilibj.drive.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class PathCreator {


    /**
     * from added waypoints:
     *
     * 1.add more points between existing points
     *
     * 2. calculate distances for all points
     *
     * 3 calculate curvature at each point
     */
    public List<Waypoint> createPath(List<Waypoint> wayPoints){

        for(int i = 0; i < wayPoints.size(); i++){
            System.out.println("path points: x: " + wayPoints.get(i).position.lateral + "y: " + wayPoints.get(i).position.forward);
        }
        List<Waypoint> pathPoints = new ArrayList<Waypoint>();

        wayPoints.get(wayPoints.size()-1).velocity = 0; //set the final point to 0 velocity

        //extend path one lookahead distance away
        Vector2d finalDir = new Vector2d();
        finalDir.x = wayPoints.get(wayPoints.size()-1).position.lateral - wayPoints.get(wayPoints.size()-2).position.lateral;
        finalDir.y = wayPoints.get(wayPoints.size()-1).position.forward - wayPoints.get(wayPoints.size()-2).position.forward;
        double finMag = finalDir.magnitude();
        finalDir.x = (finalDir.x/finMag) * Constants.Autonomous.kLookaheadDistance;
        finalDir.y = (finalDir.y/finMag) * Constants.Autonomous.kLookaheadDistance;

        wayPoints.add(wayPoints.size(), new Waypoint(new Position2d(wayPoints.get(wayPoints.size()-1).position.forward + finalDir.y, wayPoints.get(wayPoints.size()-1).position.lateral + finalDir.x), 0));

        System.out.println("ADDED LAST POINT");
        for(int i = 0; i < wayPoints.size(); i++){

            System.out.println("path points: x: " + wayPoints.get(i).position.lateral + "y: " + wayPoints.get(i).position.forward);
        }

        System.out.println("adding more points");
        //create more points
        pathPoints.add(pathPoints.size(), wayPoints.get(0));
        for(int i = 1; i < wayPoints.size(); i++){
            Vector2d dir = new Vector2d();
            dir.x = wayPoints.get(i).position.lateral - wayPoints.get(i-1).position.lateral; //it should be the farthest minus the closest which is i - (i-1)
            dir.y = wayPoints.get(i).position.forward - wayPoints.get(i-1).position.forward;
            double mag = dir.magnitude();
            System.out.println("Vector x: " + dir.x + "Vector y: " + dir.y);
            int numOfPts = (int)(mag/Constants.Autonomous.kMinPointSpacing);

            dir.x = (dir.x/mag) * Constants.Autonomous.kMinPointSpacing;
            dir.y = (dir.y/mag) * Constants.Autonomous.kMinPointSpacing;

            System.out.println("Vector x2: " + dir.x + "Vector y2: " + dir.y);

            System.out.println("added point: x: " + pathPoints.get(pathPoints.size()-1).position.lateral + "y: " + pathPoints.get(pathPoints.size()-1).position.forward);
            for(int j = 0; j < numOfPts; j++){ 
                pathPoints.add(pathPoints.size(), new Waypoint(pathPoints.get(pathPoints.size()-1).position.translateBy(new Position2d(dir.y, dir.x)),wayPoints.get(i).velocity)); //forward is x todo: check velocity math
                System.out.println("added point: x: " + pathPoints.get(pathPoints.size()-1).position.lateral + "y: " + pathPoints.get(pathPoints.size()-1).position.forward);
            }
        }

        //set distances
        for(int i = 1; i < pathPoints.size(); i++){
            double segmentDistance = Position2d.distanceFormula(pathPoints.get(i-1).position,pathPoints.get(i).position);
            pathPoints.get(i).distance = pathPoints.get(i-1).distance + segmentDistance;
        }

        //set curvature
        for(int i = 1; i < pathPoints.size()-1; i++) {

            double x1 = 0.0001 + pathPoints.get(i).position.lateral; //add a small number to stop a division by 0
            double y1 = pathPoints.get(i).position.forward;
            double x2 = pathPoints.get(i - 1).position.lateral;
            double y2 = pathPoints.get(i - 1).position.forward;
            double x3 = pathPoints.get(i + 1).position.lateral;
            double y3 = pathPoints.get(i + 1).position.forward;


            double k1 = 0.5 * (x1 * x1 + y1 * y1 - x2 * x2 - y2 * y2) / (x1 - x2);
            double k2 = (y1 - y2) / (x1 - x2);
            double b = 0.5 * (x2 * x2 - 2 * x2 * k1 + y2 * y2 - x3 * x3 + 2 * x3 * k1 - y3 * y3) / (x3 * k2 - y3 + y2 - x2 * k2);
            double a = k1 - k2 * b;
            double r = Math.sqrt(Math.pow(x1 - a, 2) + Math.pow(y1 - b, 2));

            if (r == Double.NaN) { //can get 1/infinity which returns NaN and means its a straight line
                pathPoints.get(i).curvature = 0;
            } else {
                pathPoints.get(i).curvature = 1 / r;
            }

            pathPoints.get(i).velocity = Math.min(Constants.Autonomous.kturnSpeed /r, pathPoints.get(i).velocity);
        }

        //use kinematics to go backward through the path to calculate deceleration and set velocity accordingly
        pathPoints.get(pathPoints.size()-1).velocity = 0;

        for (int i = 2; i < pathPoints.size()+1; i++){
            double d = Position2d.distanceFormula(pathPoints.get(pathPoints.size()-i+1).position,pathPoints.get(pathPoints.size()-i).position);
            pathPoints.get(pathPoints.size()-i).velocity = Math.min(pathPoints.get(pathPoints.size()-i).velocity, Math.sqrt(Math.pow(pathPoints.get(pathPoints.size()-i+1).velocity,2) + 2 * Constants.Autonomous.kMaxAccel * d));
        }
        System.out.println("CREATED PATH");

        for(int i = 0; i < pathPoints.size(); i++){
            System.out.println("path points: x: " + pathPoints.get(i).position.lateral + "y: " + pathPoints.get(i).position.forward);
        }
        return pathPoints;
    }



}
