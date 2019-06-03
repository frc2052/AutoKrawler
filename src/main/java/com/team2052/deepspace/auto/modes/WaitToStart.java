package com.team2052.deepspace.auto.modes;

import com.team2052.deepspace.auto.AutoMode;
import com.team2052.deepspace.auto.actions.DriverButtonAction;
import com.team2052.deepspace.auto.actions.SeriesAction;
import com.team2052.lib.Autonomous.Position2d;

import java.util.Arrays;

public class WaitToStart extends AutoMode {

    public WaitToStart(Position2d startPos) {
        super(startPos);
    }

    @Override
    protected void init() {
        setAction(new SeriesAction(Arrays.asList(
                new DriverButtonAction()
        )));
    }
}
