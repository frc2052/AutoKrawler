package com.team2052.lib.Autonomous;

import com.team2052.autokrawler.Constants;
import edu.wpi.first.wpilibj.drive.Vector2d;

import java.util.List;

/**
 * Created by KnightKrawler on 10/24/2018.
 */
public class Path {


    protected List<Waypoint> waypoints;

    /**
     *
     * @param waypoint
     */
    public void addWaypoint(Waypoint waypoint){
        waypoints.add(waypoints.size(),waypoint);
    }
}
