package com.team2052.deepspace.auto.paths;

import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;

import java.util.List;

public class TestPath03 extends Path{


    public TestPath03() {
        setDirection(Direction.FORWARD);
        addWaypoint(new Waypoint(new Position2d(0,0),50));
        addWaypoint(new Waypoint(new Position2d(48,0),50));
        addWaypoint(new Waypoint(new Position2d(48,36),50));
        addWaypoint(new Waypoint(new Position2d(72,36),50));
        OptimizePath();
    }

    @Override
    public List<Waypoint> getWaypoints() {
        return wayPoints;
    }
}
