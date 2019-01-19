package com.team2052.autokrawler.auto.modes;

import com.team2052.autokrawler.auto.AutoMode;
import com.team2052.autokrawler.auto.actions.FollowPathAction;
import com.team2052.autokrawler.auto.actions.SeriesAction;
import com.team2052.autokrawler.auto.paths.Path;
import com.team2052.autokrawler.auto.paths.TestBackPath01;
import com.team2052.autokrawler.auto.paths.TestPath01;

import java.util.Arrays;

public class Test extends AutoMode{


    @Override
    protected void init() {
        Path forwardPath = new TestPath01();
        Path backwardPath = new TestBackPath01();

        System.out.println("init");

        runAction(new SeriesAction(Arrays.asList(
                new FollowPathAction(forwardPath),
                new FollowPathAction(backwardPath)
        )));

    }
}
