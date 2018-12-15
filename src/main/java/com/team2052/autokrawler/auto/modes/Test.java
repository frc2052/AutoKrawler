package com.team2052.autokrawler.auto.modes;

import com.team2052.autokrawler.auto.AutoMode;
import com.team2052.autokrawler.auto.actions.FollowPathAction;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;

public class Test extends AutoMode{


    @Override
    protected void init() {
        Path testPath = new Path();
        Path testBPath = new Path();

        System.out.println("init");
        testPath.addWaypoint(new Waypoint(new Position2d(0,0), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(90,0), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(94,-2), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(96,-6), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(96,-54), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(98,-58), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(102,-60), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(138,-58), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(142,-56), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(144,-52), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(144,-6), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(146,-2), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(150,0), 60));
        testPath.addWaypoint(new Waypoint(new Position2d(200,0), 60));
        runAction(new FollowPathAction(testPath, FollowPathAction.Direction.FORWARD));

        testBPath.addWaypoint(new Waypoint(new Position2d(48,0), 40));
        testBPath.addWaypoint(new Waypoint(new Position2d(0,0), 40));
        //runAction(new FollowPathAction(testBPath, FollowPathAction.Direction.BACKWARD));
    }
}
