package com.team2052.autokrawler.auto.paths;

import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;

import java.util.List;

public class TestBackPath01 extends Path{

    public TestBackPath01() {
        setDirection(Direction.FORWARD);
        addWaypoint(new Waypoint(new Position2d(48,0),50));
        addWaypoint(new Waypoint(new Position2d(0,0),50));
        OptimizePath();
    }

    @Override
    public List<Waypoint> getWaypoints() {
        return wayPoints;
    }
}
