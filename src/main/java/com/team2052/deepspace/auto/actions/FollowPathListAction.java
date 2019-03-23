package com.team2052.deepspace.auto.actions;

import com.team2052.deepspace.auto.PurePursuitPathFollower;
import com.team2052.deepspace.auto.paths.Path;

import java.util.List;

public class FollowPathListAction implements Action{

    private PurePursuitPathFollower pathFollower = PurePursuitPathFollower.getInstance();

    private Path currentPath;
    private Path finalPath;
    private List<Path> paths;
    private int position = 0;

    public FollowPathListAction(List<Path> paths){
        this.paths = paths;
        this.currentPath = paths.get(0);
        this.finalPath = paths.get(paths.size()-1);
    }

    @Override
    public void done() {
        pathFollower.stopPathFollower();
        System.out.println("Done With Path");
    }

    @Override
    public boolean isFinished() {
        //We're on our last path and it's complete
        return pathFollower.isPathComplete() && currentPath == finalPath;
    }

    @Override
    public void start() {
        System.out.println("starting auto");
        pathFollower.start(currentPath);
    }

    @Override
    public void update() {
        //current path is complete, so it finds the next path to follow
        if (pathFollower.isPathComplete()) {
            //checks to see if there are more paths to follow
            if (!this.isFinished()) {
                position++;
                //gets next path in list
                currentPath = paths.get(position);
                pathFollower.start(currentPath);
            }
        } else {
            pathFollower.update();
        }
    }
}
