package com.team2052.lib.Autonomous;

import java.util.List;

/**
 * Created by KnightKrawler on 10/24/2018.
 */
public class Path {

    double spacing = 6; //in inches
    protected List<Waypoint> waypoints;

    /**
     *
     * @param waypoint
     */
    public void addWaypoint(Waypoint waypoint){
        waypoints.add(waypoints.size(),waypoint);
    }

    public Path getPath(){
        for(int i = 1; i < waypoints.size(); i++){
            Position2d direction = waypoints.get(i - 1).position.translateBy(waypoints.get(i).position);
            direction.setAngleFromTrig();
            int numOfPts = (int)(direction.getHype()/spacing);
            Position2d addPos = new Position2d(spacing * Math.sin(direction.heading), spacing * Math.sin(direction.heading));
            for(int j=0; j < numOfPts; i++){

            }
        }
        return null;
    }
}
