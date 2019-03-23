package com.team2052.deepspace;


import com.team2052.lib.Autonomous.Position2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotState{



    private Position2d latestPosition = new Position2d();
    private double rightVelocityInch = 0;
    private double leftVelocityInch = 0;
    private double velocityInch = 0;
    private double timeSinceReset = 0;

    private static RobotState singleRobotStateInstance = new RobotState();
    public static RobotState getInstance() { return singleRobotStateInstance; }


    //send the change in position from the last point in inches and radians and add that to the previous position


    public Position2d getLatestPosition(){
        return latestPosition;
    }

    public double getVelocityInch(){
        return velocityInch;
    }

    public double getLeftVelocityInch(){
        return leftVelocityInch;
    }

    public double getRightVelocityInch() {
        return rightVelocityInch;
    }


    public void setRightVelocityInch(double deltarightInch){
        rightVelocityInch = deltarightInch / Constants.Autonomous.kloopPeriodSec;
    }
    public void setLeftVelocityInch(double deltaleftInch){
        leftVelocityInch = deltaleftInch / Constants.Autonomous.kloopPeriodSec;
    }
    public void setLatestPosition(Position2d latestPosition){
        this.latestPosition = latestPosition;
    }

    public void setVelocityInch(double deltaDistance){
        velocityInch = deltaDistance / (Constants.Autonomous.kloopPeriodSec);
    }

    public void outputToSmartDashboard(){
        SmartDashboard.putNumber("Robot Pos X", latestPosition.getLateral());
        SmartDashboard.putNumber("Robot Pos Y", latestPosition.getForward());
        SmartDashboard.putNumber("Robot Pos Angle", latestPosition.getHeading());
        SmartDashboard.putNumber("Robot Velocity", velocityInch);
        SmartDashboard.putNumber("Robot Right Vel inch", rightVelocityInch);
        SmartDashboard.putNumber("Robot Left Vel inch", leftVelocityInch);
        SmartDashboard.putNumber("Robot Time", getTimeSinceReset());
    }

    public void setTimeSinceReset(double timeSinceReset){
        this.timeSinceReset = timeSinceReset;
    }

    public double getTimeSinceReset(){
        return timeSinceReset;
    }
}



