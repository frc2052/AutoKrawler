package com.team2052.deepspace.auto.modes.examples;

import com.team2052.deepspace.auto.AutoMode;
import com.team2052.deepspace.auto.actions.ExampleAction;
import com.team2052.deepspace.auto.actions.FollowPathAction;
import com.team2052.deepspace.auto.actions.ParallelAction;
import com.team2052.deepspace.auto.actions.SeriesAction;
import com.team2052.deepspace.auto.paths.ForwardPath;
import com.team2052.deepspace.auto.paths.Path;
import com.team2052.deepspace.auto.paths.TestBackPath02;
import com.team2052.deepspace.auto.paths.TestPath01;
import com.team2052.lib.Autonomous.Position2d;

import java.util.Arrays;

public class ForwardStartCenter extends AutoMode {

    public ForwardStartCenter(Position2d startPos){
        super(startPos);
        setStartDirection(StartDirection.FORWARD);

    }
    @Override
    protected void init() {
        setAction(new SeriesAction(Arrays.asList(
                new ParallelAction(Arrays.asList(
                        new ExampleAction(-.5,.5),
                        new FollowPathAction(new ForwardPath(startingPos, Path.Direction.FORWARD))
                )),
                new FollowPathAction(new TestBackPath02(startingPos, Path.Direction.BACKWARD)),
                new FollowPathAction(new TestPath01())
        )));
    }
}
