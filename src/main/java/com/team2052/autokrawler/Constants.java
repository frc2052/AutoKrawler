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


    public class Autonomous{
        public static final long ActionLoopTime = (long) (20); //   1/50 *1000 = 20   ...
        public static final double turnSpeed = 3.0; //constant from 1-5     higher = faster

        public static final double maxAccel = 12.0;///TESTTTTTT

    }
}
