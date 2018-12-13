package com.team2052.autokrawler.auto.actions;

import com.team2052.autokrawler.auto.PurePursuitPathFollower;
import com.team2052.lib.Autonomous.Path;

public class FollowPathAction implements Action{

    private PurePursuitPathFollower pathFollower = PurePursuitPathFollower.getInstance();

    private Path path;
    private Direction direction;

    public FollowPathAction(Path path, Direction direction){
        this.path = path;
        this.direction = direction;
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
        pathFollower.start(path, direction.isForward);
    }

    @Override
    public void update() {
        pathFollower.update();
    }

    public enum Direction{
        FORWARD(true),
        BACKWARD(false)
        ;

        public final boolean isForward;

        Direction(boolean isForward){
            this.isForward = isForward;
        }
    }
}
