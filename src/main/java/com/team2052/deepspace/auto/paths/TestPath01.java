package com.team2052.deepspace.auto.paths;

import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;

import java.util.List;

public class TestPath01 extends Path{

    public TestPath01() {
        setDirection(Direction.FORWARD);
        addWaypoint(new Waypoint(new Position2d(0,0), 60));
        addWaypoint(new Waypoint(new Position2d(90,0), 60));
        addWaypoint(new Waypoint(new Position2d(94,-2), 60));
        addWaypoint(new Waypoint(new Position2d(96,-6), 60));
        addWaypoint(new Waypoint(new Position2d(96,-54), 60));
        addWaypoint(new Waypoint(new Position2d(98,-58), 60));
        addWaypoint(new Waypoint(new Position2d(102,-60), 60));
        addWaypoint(new Waypoint(new Position2d(138,-58), 60));
        addWaypoint(new Waypoint(new Position2d(142,-56), 60));
        addWaypoint(new Waypoint(new Position2d(144,-52), 60));
        addWaypoint(new Waypoint(new Position2d(144,-6), 60));
        addWaypoint(new Waypoint(new Position2d(146,-2), 60));
        addWaypoint(new Waypoint(new Position2d(150,0), 60));
        addWaypoint(new Waypoint(new Position2d(200,0), 60));
        OptimizePath();
    }

    @Override
    public List<Waypoint> getWaypoints() {
        return wayPoints;
    }
}
