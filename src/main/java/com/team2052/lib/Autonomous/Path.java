package com.team2052.lib.Autonomous;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KnightKrawler on 10/24/2018.
 */
public class Path {


    private List<Waypoint> waypoints = new ArrayList<Waypoint>();

    /**
     * create an empty path
     */
    public Path(){

    }

    /**
     * create a path from an existing list of waypoints
     * @param PathPoints the new waypoints for this path
     */
    public Path(List<Waypoint> PathPoints){
        waypoints = PathPoints;
    }

    /**
     *adds a waypoint to the end of the path
     * @param waypoint the waypoint to be added to the end of the path
     */
    public void addWaypoint(Waypoint waypoint){

        waypoints.add(waypoints.size(),waypoint);
    }

    /**
     * getter for waypoints
     * @return return the list of waypoints
     */
    public List<Waypoint> getWaypoints(){
        return waypoints;
    } //todo: should this be copy or stay a pointer?
}
