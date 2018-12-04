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

            if(numOfPts<1){
                numOfPts = 1;
            }
            dir.x = (dir.x / mag) * (mag / numOfPts);
            dir.y = (dir.y / mag) * (mag / numOfPts);


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

        //set curvature
        for(int i = 1; i < pathPoints.size()-1; i++) {

            double thisx = pathPoints.get(i).position.lateral; //todo: xys
            double thisy = pathPoints.get(i).position.forward;
            double lastx = pathPoints.get(i - 1).position.lateral;
            double lasty = pathPoints.get(i - 1).position.forward;
            double nextx = pathPoints.get(i + 1).position.lateral;
            double nexty = pathPoints.get(i + 1).position.forward;

/*
            double k1 = 0.5 * (thisx * thisx + thisy * thisy - lastx * lastx - lasty * lasty) / (thisx - lastx);
            double k2 = (thisy - lasty) / (thisx - lastx);
            double b = 0.5 * (lastx * lastx - 2 * lastx * k1 + lasty * lasty - nextx * nextx + 2 * nextx * k1 - nexty * nexty) / (nextx * k2 - nexty + lasty - lastx * k2);
            double a = k1 - k2 * b;
            double r = Math.sqrt(Math.pow(thisx - a, 2) + Math.pow(thisy - b, 2));

            if (r == Double.NaN || r == Double.POSITIVE_INFINITY || r == Double.NEGATIVE_INFINITY || r == 0) { //can get 1/infinity which returns NaN and means its a straight line
                pathPoints.get(i).curvature = 0;
            } else {
                pathPoints.get(i).curvature = 1 / r;
            }
            */

            double bi1x = (lastx + thisx)/2;
            double bi1y = (lasty + thisy)/2;
            double bi2x = (nextx + lastx)/2;
            double bi2y = (nexty + lasty)/2;

            double cx = 0;
            double cy = 0;

            double r;

            //when two lines of opposite directions are put in a slower speed is not calculated. ex: spd:25 pts: (0,0)(0,6)(0,0) speed is 25 for all three.
            System.out.println("(x:" + thisx + "," + thisy + ")");
            if(lasty == thisy){
                if(nexty == thisy){
                    System.out.println("straight both lines horizontal");
                    pathPoints.get(i).curvature = 0;
                }else{

                    System.out.println("curved with 1st line horizontal");

                    cx = bi1x;
                    cy = -((nextx -thisx)/(nexty-thisy)) * (bi2x - bi1x) + bi2y;

                    r = Math.sqrt(Math.pow(cx - thisx, 2) + Math.pow(cy - thisy, 2));

                    System.out.println("R: " + r + "cx: " + cx + "thisx: " +  thisx + "cy: " + cy +"thisy: " + thisy);
                    pathPoints.get(i).curvature = 1 / r;

                }
            }else{
                if(nexty == thisy){
                    System.out.println("curved with 2nd line horizontal");
                    cx = bi2x;
                    cy = -((thisx-lastx)/(thisy-lasty)) * (bi1x - bi2x) + bi1y;

                    r = Math.sqrt(Math.pow(cx - thisx, 2) + Math.pow(cy - thisy, 2));
                    System.out.println("R: " + r + "cx: " + cx + "thisx: " +  thisx + "cy: " + cy +"thisy: " + thisy);
                    pathPoints.get(i).curvature = 1 / r;

                }else {
                    if ((thisy - lasty)/(thisx - lastx) == (nexty - thisy)/(nextx - thisx)){
                        System.out.println("straight both lines equal slopes");
                        pathPoints.get(i).curvature = 0;
                    } else {
                        System.out.println("Curved with both lines real slopes");

                        //point slope form of perpindicular bisector y-yb = m(x - xd)

                        double m1 = -(thisx - lastx)/(thisy - lasty);
                        double xd1 = bi1x;
                        double yb1 = bi1y;

                        double m2 = -(nextx - thisx)/(nexty - thisy);
                        double xd2 = bi2x;
                        double yb2 = bi2y;


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

                        r = Math.sqrt(Math.pow(cx - thisx, 2) + Math.pow(cy - thisy, 2));
                        System.out.println("R: " + r + "cx: " + cx + "thisx: " +  thisx + "cy: " + cy +"thisy: " + thisy);
                        pathPoints.get(i).curvature = 1 / r;
                    }
                }
            }

            if (pathPoints.get(i).curvature == 0){
                pathPoints.get(i).velocity = pathPoints.get(i).velocity;
            }else {
                System.out.println("Curvature: " + pathPoints.get(i).curvature);
                System.out.println("MINIMUM OF: " + (Constants.Autonomous.kturnSpeed / pathPoints.get(i).curvature) + " , " + pathPoints.get(i).velocity);
                pathPoints.get(i).velocity = Math.min(Constants.Autonomous.kturnSpeed / pathPoints.get(i).curvature, pathPoints.get(i).velocity);
            }

            if (pathPoints.get(i).velocity > Constants.Autonomous.kMaxVelocity){
                pathPoints.get(i).velocity = Constants.Autonomous.kMaxVelocity;
            }
        }

        //pushPathToSmartDashboard(pathPoints);
        //use kinematics to go backward through the path to calculate deceleration and set velocity accordingly
        pathPoints.get(pathPoints.size()-3).velocity = 0;

        //i=4 because we extended the path by 2 points and we want size()-i to be equal to the second to last point
        for (int i = 4; i < pathPoints.size()+1; i++){
            double d = Position2d.distanceFormula(pathPoints.get(pathPoints.size()-i+1).position,pathPoints.get(pathPoints.size()-i).position);
            pathPoints.get(pathPoints.size()-i).velocity = Math.min(pathPoints.get(pathPoints.size()-i).velocity, Math.sqrt(Math.pow(pathPoints.get(pathPoints.size()-i+1).velocity,2) + 2 * Constants.Autonomous.kMaxAccel * d));
        }
        System.out.println("CREATED PATH");

        for(int i = 0; i < pathPoints.size(); i++){
            System.out.println("path points: x: " + pathPoints.get(i).position.lateral + "y: " + pathPoints.get(i).position.forward + " vel: " + pathPoints.get(i).velocity); //todo: xys
        }
        pushPathToSmartDashboard(pathPoints);
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
