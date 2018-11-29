package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.Constants;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;
import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

                pathPoints.add(pathPoints.size(), new Waypoint(pathPoints.get(pathPoints.size()-1).position.translateBy(new Position2d(dir.y, dir.x)), wayPoints.get(i-1).velocity)); //forward is x todo: check velocity math
                System.out.println("added point: x: " + pathPoints.get(pathPoints.size()-1).position.lateral + "y: " + pathPoints.get(pathPoints.size()-1).position.forward + " vel: " + wayPoints.get(i-1).velocity);
            }
        }

        //set distances
        for(int i = 1; i < pathPoints.size(); i++){
            double segmentDistance = Position2d.distanceFormula(pathPoints.get(i-1).position,pathPoints.get(i).position);
            pathPoints.get(i).distance = pathPoints.get(i-1).distance + segmentDistance;
        }

        System.out.println("WAYPOINY VEL:" + pathPoints.get(1).velocity);
        //set curvature
        for(int i = 1; i < pathPoints.size()-1; i++) {

            double x1 = 0.0001 + pathPoints.get(i).position.lateral; //add a small number to stop a division by 0 //todo: xys
            double y1 = pathPoints.get(i).position.forward;
            double x2 = pathPoints.get(i - 1).position.lateral;
            double y2 = pathPoints.get(i - 1).position.forward;
            double x3 = pathPoints.get(i + 1).position.lateral;
            double y3 = pathPoints.get(i + 1).position.forward;

/*
            double k1 = 0.5 * (x1 * x1 + y1 * y1 - x2 * x2 - y2 * y2) / (x1 - x2);
            double k2 = (y1 - y2) / (x1 - x2);
            double b = 0.5 * (x2 * x2 - 2 * x2 * k1 + y2 * y2 - x3 * x3 + 2 * x3 * k1 - y3 * y3) / (x3 * k2 - y3 + y2 - x2 * k2);
            double a = k1 - k2 * b;
            double r = Math.sqrt(Math.pow(x1 - a, 2) + Math.pow(y1 - b, 2));

            if (r == Double.NaN || r == Double.POSITIVE_INFINITY || r == Double.NEGATIVE_INFINITY || r == 0) { //can get 1/infinity which returns NaN and means its a straight line
                pathPoints.get(i).curvature = 0;
            } else {
                pathPoints.get(i).curvature = 1 / r;
            }
            */

            double bi1x = (x2 + x1)/2;
            double bi1y = (y2 + y1)/2;
            double bi2x = (x3 + x2)/2;
            double bi2y = (y3 + y2)/2;

            double cx = 0;
            double cy = 0;

            double r;
            if(x2-x1 == 0){
                if(x3-x2 == 0){
                    pathPoints.get(i).curvature = 0;
                }else{

                    cx = bi1x;
                    cy = -((x3 -x2)/y3-y2) * (bi1x - bi2x) + bi2y;

                    r = Math.sqrt(Math.pow(cx - x1, 2) + Math.pow(cy - y1, 2));
                    pathPoints.get(i).curvature = 1 / r;

                }
            }else{
                if(x3-x2 == 0){
                    cx = bi2x;
                    cy = -((x2 -x1)/y2-y1) * (bi2x - bi1x) + bi1y;

                    r = Math.sqrt(Math.pow(cx - x1, 2) + Math.pow(cy - y1, 2));
                    pathPoints.get(i).curvature = 1 / r;
                }else {
                    if ((y2 - y1)/(x2 - x1) == (y3 - y2)/(x3 - x2)){
                        pathPoints.get(i).curvature = 0;
                    } else {

                        //point slope form of perindicular bisector y-yb = m(x - xd)

                        double m1 = -(x2 - x1)/(y2 - y1);
                        double xd1 = bi1x;
                        double yb1 = bi1y;

                        double m2 = -(x2 - x1)/(y2 - y1);
                        double xd2 = bi1x;
                        double yb2 = bi1y;


                        //standard form of the same bisectors ax + by = c
                        double a1 = -m1;
                        double b1 = 1;
                        double c1 = -m1 * xd1 + yb1;

                        double a2 = -m2;
                        double b2 = 1;
                        double c2 = -m2 * xd2 + yb2;


                        double delta = a1 * b2 - a2 * b1;

                        if (delta == 0){
                            //parallel
                        }


                        cx = (b2 * c1 - b1 * c2) / delta;
                        cy = (a1 * c2 - a2 * c1) / delta;

                        r = Math.sqrt(Math.pow(cx - x1, 2) + Math.pow(cy - y1, 2));
                        pathPoints.get(i).curvature = 1 / r;
                    }
                }
            }

            if (pathPoints.get(i).curvature == 0){
                pathPoints.get(i).velocity = pathPoints.get(i).velocity;
            }else {
                System.out.println("MINIMUM OF: " + (Constants.Autonomous.kturnSpeed * pathPoints.get(i).curvature) + " , " + pathPoints.get(i).velocity);
                pathPoints.get(i).velocity = Math.min(Constants.Autonomous.kturnSpeed * pathPoints.get(i).curvature, pathPoints.get(i).velocity);
            }

            if (pathPoints.get(i).velocity > Constants.Autonomous.kMaxVelocity){
                pathPoints.get(i).velocity = Constants.Autonomous.kMaxVelocity;
            }
        }

        pushPathToSmartDashboard(pathPoints);
        //use kinematics to go backward through the path to calculate deceleration and set velocity accordingly
        pathPoints.get(pathPoints.size()-1).velocity = 0;

        for (int i = 2; i < pathPoints.size()+1; i++){
            double d = Position2d.distanceFormula(pathPoints.get(pathPoints.size()-i+1).position,pathPoints.get(pathPoints.size()-i).position);
            pathPoints.get(pathPoints.size()-i).velocity = Math.min(pathPoints.get(pathPoints.size()-i).velocity, Math.sqrt(Math.pow(pathPoints.get(pathPoints.size()-i+1).velocity,2) + 2 * Constants.Autonomous.kMaxAccel * d));
        }
        System.out.println("CREATED PATH");

        for(int i = 0; i < pathPoints.size(); i++){
            System.out.println("path points: x: " + pathPoints.get(i).position.lateral + "y: " + pathPoints.get(i).position.forward + " vel: " + pathPoints.get(i).velocity); //todo: xys
        }
        //pushPathToSmartDashboard(pathPoints);
        return pathPoints;
    }


    private void pushPathToSmartDashboard(List<Waypoint> waypoints){ //todo: xys
        double xs[] = new double[waypoints.size()];
        for(int i = 0; i< waypoints.size(); i++){
            xs[i] = waypoints.get(i).position.lateral;
        }
        SmartDashboard.putNumberArray("Path X's", xs);

        double ys[] = new double[waypoints.size()];
        for(int i = 0; i< waypoints.size(); i++){
            ys[i] = waypoints.get(i).position.forward;
        }
        SmartDashboard.putNumberArray("Path Y's", ys);

        double vels[] = new double[waypoints.size()];
        for(int i = 0; i< waypoints.size(); i++){
            vels[i] = waypoints.get(i).velocity;
        }
        SmartDashboard.putNumberArray("Path Vel's", vels);
    }

}
