package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Path;
import com.team2052.lib.Autonomous.PathCreator;
import com.team2052.lib.Autonomous.RateLimiter;

/**
 * Created by KnightKrawler on 9/12/2018.
 */
public class PathFollower {

    private static PathFollower instance = new PathFollower();
    private PathFollower() {}
    public static PathFollower getInstance() {
        return instance;
    }

    private Path path;

    private RateLimiter rateLimiter = new RateLimiter();
    private PathCreator pathCreator = new PathCreator();
    private DriveTrain driveTrain = DriveTrain.getInstance();

    public void setPath(Path path){
        this.path = path;
    }

    public void update() {

    }
}
