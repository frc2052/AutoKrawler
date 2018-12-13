package com.team2052.autokrawler;

import com.team2052.autokrawler.auto.PathCreator;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;

public class FakeRobot {

    public static void main(String args[]){

        Path testPath = new Path();

        testPath.addWaypoint(new Waypoint(new Position2d(0,0), 25));
        testPath.addWaypoint(new Waypoint(new Position2d(48,0), 25));
        testPath.addWaypoint(new Waypoint(new Position2d(52,4), 25));
        testPath.addWaypoint(new Waypoint(new Position2d(54,9), 25));
        testPath.addWaypoint(new Waypoint(new Position2d(54,48), 25));
        testPath.addWaypoint(new Waypoint(new Position2d(54,0), 25));

        PathCreator pathCreator = new PathCreator();
        Path path = new Path(pathCreator.createPath(testPath.getWaypoints(), true));
    }
}
