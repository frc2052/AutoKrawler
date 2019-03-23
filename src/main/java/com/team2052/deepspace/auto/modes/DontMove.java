package com.team2052.deepspace.auto.modes;

import com.team2052.deepspace.auto.AutoMode;
import com.team2052.deepspace.auto.actions.SeriesAction;
import com.team2052.deepspace.auto.actions.WaitAction;
import com.team2052.lib.Autonomous.Position2d;

import java.util.Arrays;

/**
 * Starts: Anywhere
 * Desc:
 * Ends: Where it starts
 */
public class DontMove extends AutoMode{
    public DontMove()
    {
        super(new Position2d(0,0));
    }

    @Override
    protected void init() {
        setAction(new SeriesAction(Arrays.asList(new WaitAction(0))));
    }
}
