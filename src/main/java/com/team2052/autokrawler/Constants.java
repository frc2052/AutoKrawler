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
        public static final double kDriveWheelDiameterInches = 6.0;
    }


    public class AutoMode{
        public static final long ActionLoopTime = (long) (1.0 /50.0 * 1000); //the parenthese "cast"/change the double to a long
    }
}
