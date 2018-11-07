package com.team2052.autokrawler.auto.modes;

import com.team2052.autokrawler.auto.AutoMode;
import com.team2052.autokrawler.auto.actions.FollowPathAction;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Waypoint;

public class Test extends AutoMode{
    private static Path testPath;
    @Override
    protected void init() {

        testPath.addWaypoint(new Waypoint(new Position2d(0,0), 20));
        testPath.addWaypoint(new Waypoint(new Position2d(50,50), 20));

        runAction(new FollowPathAction(testPath, FollowPathAction.Direction.FORWARD));
    }
}
