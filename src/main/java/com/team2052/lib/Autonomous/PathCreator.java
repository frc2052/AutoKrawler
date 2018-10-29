package com.team2052.lib.Autonomous;

import com.team2052.autokrawler.Constants;
import edu.wpi.first.wpilibj.drive.Vector2d;

import java.util.List;

public class PathCreator {

    double spacing = 6; //in inches//todo constant


    /**
     * from added waypoints:
     *
     * 1.add more points between existing points
     *
     * 2. calculate distances fora ll points
     *
     * 3 calcualte curvature at each point
     */
    public void createPath(List<Waypoint> waypoints){

        List<Waypoint> pathPoints = null;
        //create more points
        for(int i = 1; i < waypoints.size(); i++){
            Vector2d dir = new Vector2d();
            dir.x = waypoints.get(i-1).position.lateral - waypoints.get(i).position.lateral;
            dir.y = waypoints.get(i-1).position.forward - waypoints.get(i).position.forward;
            double mag = dir.magnitude();
            int numOfPts = (int)(mag/spacing);

            dir.x = (dir.x/mag) * spacing;
            dir.y = (dir.y/mag) * spacing;

            for(int j = 0; j < numOfPts; j++){
                pathPoints.add(pathPoints.size(), new Waypoint(waypoints.get(i-1).position.translateBy(new Position2d(-dir.x, -dir.y)),waypoints.get(i).velocity)); //forward is x todo: check velocity math
            }
        }

        //set distances
        for(int i = 1; i < pathPoints.size(); i++){
            double segmentDistance = distanceFormula(pathPoints.get(i-1).position,pathPoints.get(i).position);
            pathPoints.get(i).distance = pathPoints.get(i-1).distance + segmentDistance;
        }

        //set curvature
        for(int i = 1; i < pathPoints.size()-1; i++) {

            double x1 = 0.0001 + pathPoints.get(i).position.forward; //add a small number to stop a division by 0
            double y1 = pathPoints.get(i).position.lateral;
            double x2 = pathPoints.get(i - 1).position.forward;
            double y2 = pathPoints.get(i - 1).position.lateral;
            double x3 = pathPoints.get(i + 1).position.forward;
            double y3 = pathPoints.get(i + 1).position.lateral;


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

            pathPoints.get(i).velocity = Math.min(Constants.Autonomous.turnSpeed/r, pathPoints.get(i).velocity);
        }

        //use kinematics to go backward through the path to calculate deceleration and set velocity accordingly
        pathPoints.get(pathPoints.size()-1).velocity = 0;

        for (int i = 2; i < pathPoints.size()+1; i++){
            double d = distanceFormula(pathPoints.get(pathPoints.size()-i+1).position,pathPoints.get(pathPoints.size()-i).position);
            pathPoints.get(pathPoints.size()-i).velocity = Math.min(pathPoints.get(pathPoints.size()-i).velocity, Math.sqrt(Math.pow(pathPoints.get(pathPoints.size()-i+1).velocity,2) + 2 * Constants.Autonomous.maxAccel * d));
        }

    }


    private double distanceFormula(Position2d first, Position2d second){
        return Math.sqrt(Math.pow(second.lateral - first.lateral,2) + Math.pow(second.forward - first.forward,2));
    }
}
