package com.team2052.deepspace.auto.paths;

import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;

import java.util.List;

public class TestPath02 extends Path{

    public TestPath02() {
        setDirection(Direction.FORWARD);
        addWaypoint(new Waypoint(new Position2d(0,0), 25));
        addWaypoint(new Waypoint(new Position2d(48,0), 25));
        addWaypoint(new Waypoint(new Position2d(48,-48), 25));
        addWaypoint(new Waypoint(new Position2d(96,-48), 25));
        addWaypoint(new Waypoint(new Position2d(96,0), 25));
        addWaypoint(new Waypoint(new Position2d(144,0), 25));
        OptimizePath();
    }

    @Override
    public List<Waypoint> getWaypoints() {
        return wayPoints;
    }
}
