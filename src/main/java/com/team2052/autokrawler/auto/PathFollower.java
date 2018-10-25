package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Path;

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

    private DriveTrain driveTrain = DriveTrain.getInstance();


    public void setPath(Path path){
        this.path = path;
    }

    public void update() {

    }
}
