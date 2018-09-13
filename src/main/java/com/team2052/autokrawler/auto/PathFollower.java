package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.RobotState;
import com.team2052.autokrawler.subsystems.DriveTrain;
import com.team2052.lib.Autonomous.Position2d;
import com.team2052.lib.Autonomous.Position2dMath;

/**
 * Created by KnightKrawler on 9/12/2018.
 */
public class PathFollower {

    private static PathFollower instance = new PathFollower();
    private PathFollower() {}
    public static PathFollower getInstance() {
        return instance;
    }

    private Position2d endPosition;
    private Mode mode;

    private DriveTrain driveTrain = DriveTrain.getInstance();

    public void simpleFollower(Position2d end){
        this.endPosition = end;
        mode = Mode.SIMPLE;
    }

    public void circleFollower(){

    }

    public boolean update(){

        switch (mode){
            case SIMPLE:
                Position2d currentPos = RobotState.getLatestPosition();
                Position2d difference = Position2dMath.subtractCartisian(endPosition, currentPos);
                Position2d rotatedDifference = new Position2d(difference.forward*Math.cos(currentPos.heading) - difference.lateral*Math.sin(currentPos.heading),difference.forward*Math.sin(currentPos.heading) + difference.lateral*Math.cos(currentPos.heading));
                if(difference.forward < 1 && difference.lateral < 1){
                    driveTrain.driveTank(0,0);
                }
                System.out.println("diatance forward " + rotatedDifference.forward + "distance lateral " + rotatedDifference.lateral);
                if(rotatedDifference.lateral>0){
                    driveTrain.driveTank(.1,.4);
                }else{
                    driveTrain.driveTank(.4,.1);
                }

                return false;
            case CIRCLE:
                return false;
        }
        return false;
    }

    enum Mode{
        SIMPLE,
        CIRCLE
    }
}
