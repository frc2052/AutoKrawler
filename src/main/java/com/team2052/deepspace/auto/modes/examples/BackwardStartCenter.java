package com.team2052.deepspace.auto.modes.examples;

import com.team2052.deepspace.auto.AutoMode;
import com.team2052.deepspace.auto.actions.*;
import com.team2052.deepspace.auto.paths.Path;
import com.team2052.deepspace.auto.paths.TestBackPath01;
import com.team2052.deepspace.subsystems.ExampleLoopableSubsystemController;
import com.team2052.lib.Autonomous.Position2d;

import java.util.Arrays;

public class BackwardStartCenter extends AutoMode {

    public BackwardStartCenter(Position2d startPos) {
        super(startPos);
        setStartDirection(StartDirection.BACKWARD);
    }

    @Override
    protected void init() {
        setAction(new SeriesAction(Arrays.asList(
                //Starting path starts going backwards
                new FollowPathAction(new TestBackPath01(startingPos, Path.Direction.BACKWARD)),
                //Vision
                new ParallelAction(Arrays.asList(
                        new ExampleAction(.5,2.5),
                        new ExampleEnumAction(ExampleLoopableSubsystemController.State.UPOUT)
                ))
        )));
    }
}
