package com.team2052.autokrawler;


public class Constants {
    //All constant values for the robot code will go in this class.


    public class Controls{
    }

    public class DriveTrain{
        public static final int kDriveLeftMasterId = 1;
        public static final int kDriveLeftSlaveId = 2;
        public static final int kDriveRightMasterId = 4;
        public static final int kDriveRightSlaveId = 3;

        public static final int kCANBusConfigTimeoutMS = 10;
        public static final int kkTicksPerRot = 1024;
        public static final double kDriveWheelCircumferenceInches = 6.0 * Math.PI;
    }


    public class Autonomous{ //all units for distances, velocity, and acceleration are in inches
        public static final long kActionLoopTime = (long) (20); //   1/50 *1000milli = 20 milli  ...
        public static final double kturnSpeed = 3.0; //constant from 1-5     higher = faster

        public static final double kMaxVelocity = 50;
        public static final double kMaxAccel = 12.0;///TESTTTTTT
        public static final double kloopPeriod = 1.0 / 100.0; //0.01

        public static final double kLookaheadDistance = 12; //12-25
        public static final double kTrackWidth = 10;
        public static final double kV = 1/kMaxVelocity;
        public static final double kA = 0;
        public static final double kP = 0;

    }
}
