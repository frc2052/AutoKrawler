package com.team2052.autokrawler;


public class Constants {
    //All constant values for the robot code will go in this class.


    public class Controls{
        public static final int kToggleDriveTrainButton = 2;

        public static final int kIntakeIntakeButton = 3;
        public static final int kIntakeOutakeButton = 4;
    }

    public class DriveTain{
        public static final int kLeftFrontMotor = 1;
        public static final int kLeftBackMotor = 2;
        public static final int kRightFrontMotor = 3;
        public static final int kRightBackMotor = 4;
    }


    public class AutoMode{
        public static final long ActionLoopTime = (long) (1.0 /50.0 * 1000); //the parenthese "cast"/change the double to a long
    }
}
