package com.team2052.autokrawler.auto.actions;

import com.team2052.autokrawler.auto.PurePursuitPathFollower;
import com.team2052.autokrawler.auto.paths.Path;

public class FollowPathAction implements Action{

    private PurePursuitPathFollower pathFollower = PurePursuitPathFollower.getInstance();

    private Path path;

    public FollowPathAction(Path path){
        this.path = path;
    }

    @Override
    public void done() {
        pathFollower.stopPathFollower();
        System.out.println("Done With Path");

    }

    @Override
    public boolean isFinished() {
        return pathFollower.isPathComplete();
    }

    @Override
    public void start() {
        System.out.println("starting auto");
        pathFollower.start(path);
    }

    @Override
    public void update() {
        pathFollower.update();
    }


}
