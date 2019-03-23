package com.team2052.deepspace.auto.paths;

import com.team2052.deepspace.Constants;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;

import java.util.List;

public class TestBackPath01 extends Path{

    public TestBackPath01(Position2d startPos, Direction direction) {
        setDirection(direction);
        addWaypoint(new Waypoint(startPos, Constants.Autonomous.kAutoVelocity));
        addWaypoint(new Waypoint(new Position2d(48,0),Constants.Autonomous.kAutoVelocity));
        OptimizePath();
    }

    @Override
    public List<Waypoint> getWaypoints() {
        return wayPoints;
    }
}
