package com.team2052.autokrawler.auto.actions;

import com.team2052.autokrawler.auto.PurePursuitPathFollower;
import com.team2052.lib.Autonomous.Path;

public class FollowPathAction implements Action{

    private PurePursuitPathFollower pathFollower = PurePursuitPathFollower.getInstance();

    private Path path;

    public FollowPathAction(Path path, Direction direction){
        this.path = path;
    }

    @Override
    public void done() {

    }

    @Override
    public boolean isFinished() {
        return pathFollower.isPathComplete();
    }

    @Override
    public void start() {
        pathFollower.resetPathFollower();
        pathFollower.setPath(path);
    }

    @Override
    public void update() {
        pathFollower.update();
    }

    public enum Direction{
        FORWARD(false),
        BACKWARD(true)
        ;

        public final boolean dir;

        Direction(boolean dir){
            this.dir = dir;
        }
    }
}