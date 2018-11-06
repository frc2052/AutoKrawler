package com.team2052.lib.Autonomous;

import java.util.List;

/**
 * Created by KnightKrawler on 10/24/2018.
 */
public class Path {


    protected List<Waypoint> waypoints;



    public Path(){

    }

    public Path(List<Waypoint> PathPoints){
        waypoints = PathPoints;
    }
    /**
     *
     * @param waypoint
     */
    public void addWaypoint(Waypoint waypoint){
        waypoints.add(waypoints.size(),waypoint);
    }

    public List<Waypoint> getWaypoints(){
        return waypoints;
    }
}
