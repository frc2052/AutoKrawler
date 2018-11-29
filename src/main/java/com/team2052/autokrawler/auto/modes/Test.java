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

        System.out.println("init");
        testPath.addWaypoint(new Waypoint(new Position2d(0,0), 25));
        testPath.addWaypoint(new Waypoint(new Position2d(48,0), 25));

        runAction(new FollowPathAction(testPath, FollowPathAction.Direction.FORWARD));
    }
}
