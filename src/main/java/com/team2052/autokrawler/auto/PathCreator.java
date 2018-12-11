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

        /*
        for(int i = 0; i < wayPoints.size(); i++){
            System.out.println("path points: x: " + wayPoints.get(i).getPosition().getLateral() + "y: " + wayPoints.get(i).getPosition().getForward());
        }*/
        List<Waypoint> pathPoints = new ArrayList<Waypoint>();

        wayPoints.get(wayPoints.size()-1).setVelocity(0); //set the final point to 0 velocity

        //extend path 1.5 lookahead distance away
        Vector2d finalDir = new Vector2d();
        finalDir.x = wayPoints.get(wayPoints.size()-1).getPosition().getLateral() - wayPoints.get(wayPoints.size()-2).getPosition().getLateral();
        finalDir.y = wayPoints.get(wayPoints.size()-1).getPosition().getForward() - wayPoints.get(wayPoints.size()-2).getPosition().getForward();
        double finMag = finalDir.magnitude();
        finalDir.x = (finalDir.x/finMag) * Constants.Autonomous.kLookaheadDistance * 1.5;
        finalDir.y = (finalDir.y/finMag) * Constants.Autonomous.kLookaheadDistance * 1.5;

        wayPoints.add(wayPoints.size(), new Waypoint(new Position2d(wayPoints.get(wayPoints.size()-1).getPosition().getForward() + finalDir.y, wayPoints.get(wayPoints.size()-1).getPosition().getLateral() + finalDir.x), 0));

        /*System.out.println("ADDED LAST POINT");
        for(int i = 0; i < wayPoints.size(); i++){
            System.out.println("path points: x: " + wayPoints.get(i).getPosition().getLateral() + "y: " + wayPoints.get(i).getPosition().getForward());
        }*/

        //System.out.println("adding more points");
        //create more points
        pathPoints.add(pathPoints.size(), wayPoints.get(0));
        for(int i = 1; i < wayPoints.size(); i++){
            Vector2d dir = new Vector2d();
            dir.x = wayPoints.get(i).getPosition().getLateral() - wayPoints.get(i-1).getPosition().getLateral(); //it should be the farthest minus the closest which is i - (i-1)
            dir.y = wayPoints.get(i).getPosition().getForward() - wayPoints.get(i-1).getPosition().getForward();
            double mag = dir.magnitude();
            //System.out.println("Vector x: " + dir.x + "Vector y: " + dir.y);
            int numOfPts = (int)(mag/Constants.Autonomous.kMinPointSpacing);

            if(numOfPts<1){
                numOfPts = 1;
            }
            dir.x = (dir.x / mag) * (mag / numOfPts);
            dir.y = (dir.y / mag) * (mag / numOfPts);


            //System.out.println("Vector x2: " + dir.x + "Vector y2: " + dir.y);

            //System.out.println("added point: x: " + pathPoints.get(pathPoints.size()-1).getPosition().getLateral() + "y: " + pathPoints.get(pathPoints.size()-1).getPosition().getForward());
            for(int j = 0; j < numOfPts; j++){

                pathPoints.add(pathPoints.size(), new Waypoint(pathPoints.get(pathPoints.size()-1).getPosition().translateBy(new Position2d(dir.y, dir.x)), wayPoints.get(i-1).getVelocity())); //forward is x todo: check velocity math
               //System.out.println("added point: x: " + pathPoints.get(pathPoints.size()-1).getPosition().getLateral() + "y: " + pathPoints.get(pathPoints.size()-1).getPosition().getForward() + " vel: " + wayPoints.get(i-1).getVelocity());
            }
        }

        //set distances
        for(int i = 1; i < pathPoints.size(); i++){
            double segmentDistance = Position2d.distanceFormula(pathPoints.get(i-1).getPosition(),pathPoints.get(i).getPosition());
            pathPoints.get(i).setDistance(pathPoints.get(i-1).getDistance() + segmentDistance);
        }

        //set curvature
        for(int i = 1; i < pathPoints.size()-1; i++) {

            double thisx = pathPoints.get(i).getPosition().getLateral(); //todo: xys
            double thisy = pathPoints.get(i).getPosition().getForward();
            double lastx = pathPoints.get(i - 1).getPosition().getLateral();
            double lasty = pathPoints.get(i - 1).getPosition().getForward();
            double nextx = pathPoints.get(i + 1).getPosition().getLateral();
            double nexty = pathPoints.get(i + 1).getPosition().getForward();


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
                    //System.out.println("straight both lines horizontal");
                    pathPoints.get(i).setCurvature(0);
                }else{

                    //System.out.println("curved with 1st line horizontal");

                    cx = bi1x;
                    cy = -((nextx -thisx)/(nexty-thisy)) * (bi2x - bi1x) + bi2y;

                    r = Math.sqrt(Math.pow(cx - thisx, 2) + Math.pow(cy - thisy, 2));

                    //System.out.println("R: " + r + "cx: " + cx + "thisx: " +  thisx + "cy: " + cy +"thisy: " + thisy);
                    pathPoints.get(i).setCurvature(1 / r);

                }
            }else{
                if(nexty == thisy){
                    //System.out.println("curved with 2nd line horizontal");
                    cx = bi2x;
                    cy = -((thisx-lastx)/(thisy-lasty)) * (bi1x - bi2x) + bi1y;

                    r = Math.sqrt(Math.pow(cx - thisx, 2) + Math.pow(cy - thisy, 2));
                    //System.out.println("R: " + r + "cx: " + cx + "thisx: " +  thisx + "cy: " + cy +"thisy: " + thisy);
                    pathPoints.get(i).setCurvature(1 / r);

                }else {
                    if ((thisy - lasty)/(thisx - lastx) == (nexty - thisy)/(nextx - thisx)){
                        //System.out.println("straight both lines equal slopes");
                        pathPoints.get(i).setCurvature(0);
                    } else {
                        //System.out.println("Curved with both lines real slopes");

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
                        //System.out.println("R: " + r + "cx: " + cx + "thisx: " +  thisx + "cy: " + cy +"thisy: " + thisy);
                        pathPoints.get(i).setCurvature(1 / r);
                    }
                }
            }

            if (pathPoints.get(i).getCurvature() != 0){
                //System.out.println("Curvature: " + pathPoints.get(i).getCurvature());
                //System.out.println("MINIMUM OF: " + (Constants.Autonomous.kturnSpeed / pathPoints.get(i).getCurvature()) + " , " + pathPoints.get(i).getVelocity());
                pathPoints.get(i).setVelocity(Math.min(Constants.Autonomous.kturnSpeed / pathPoints.get(i).getCurvature(), pathPoints.get(i).getVelocity()));
            }

            if (pathPoints.get(i).getVelocity() > Constants.Autonomous.kMaxVelocity){
                pathPoints.get(i).setVelocity(Constants.Autonomous.kMaxVelocity);
            }
        }

        //use kinematics to go backward through the path to calculate deceleration and set velocity accordingly
        pathPoints.get(pathPoints.size()-3).setVelocity(0);

        //i=4 because we extended the path by 2 points and we want size()-i to be equal to the second to last point
        for (int i = 4; i < pathPoints.size()+1; i++){
            double d = Position2d.distanceFormula(pathPoints.get(pathPoints.size()-i+1).getPosition(),pathPoints.get(pathPoints.size()-i).getPosition());
            pathPoints.get(pathPoints.size()-i).setVelocity(Math.min(pathPoints.get(pathPoints.size()-i).getVelocity(), Math.sqrt(Math.pow(pathPoints.get(pathPoints.size()-i+1).getVelocity(),2) + 2 * Constants.Autonomous.kMaxAccel * d)));
        }
        //System.out.println("CREATED PATH");
        /*
        for(int i = 0; i < pathPoints.size(); i++){
            System.out.println("path points: x: " + pathPoints.get(i).getPosition().getLateral() + "y: " + pathPoints.get(i).getPosition().getForward() + " vel: " + pathPoints.get(i).getVelocity()); //todo: xys
        }*/
        pushPathToSmartDashboard(pathPoints);
        return pathPoints;
    }


    private void pushPathToSmartDashboard(List<Waypoint> waypoints){ //todo: xys
        double xs[] = new double[waypoints.size()];
        for(int i = 0; i< waypoints.size(); i++){
            xs[i] = waypoints.get(i).getPosition().getLateral();
        }
        SmartDashboard.putNumberArray("Path X's", xs);

        double ys[] = new double[waypoints.size()];
        for(int i = 0; i< waypoints.size(); i++){
            ys[i] = waypoints.get(i).getPosition().getForward();
        }
        SmartDashboard.putNumberArray("Path Y's", ys);

        double vels[] = new double[waypoints.size()];
        for(int i = 0; i< waypoints.size(); i++){
            vels[i] = waypoints.get(i).getVelocity();
        }
        SmartDashboard.putNumberArray("Path Vel's", vels);
    }

}
