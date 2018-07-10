package com.team2052.autokrawler.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.team2052.autokrawler.Constants;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;

public class DriveTrain {

    protected static final int kVelocityControlSlot = 0;

    // Instance of DriveTrain class to be created in Robot.java class by running get instance
    private static DriveTrain singleDriveTrainInstance = new DriveTrain();
    public static DriveTrain getInstance() { return singleDriveTrainInstance; }

    AHRS navXGyro = null;

    public final TalonSRX rightMaster;
    public final TalonSRX leftMaster;
    private final TalonSRX rightSlave;
    private final TalonSRX leftSlave;

    DriveTrain(){
        rightMaster = new TalonSRX(Constants.DriveTrain.kDriveRightMasterId);
        leftMaster = new TalonSRX(Constants.DriveTrain.kDriveLeftMasterId);
        rightSlave = new TalonSRX(Constants.DriveTrain.kDriveRightSlaveId);
        leftSlave = new TalonSRX(Constants.DriveTrain.kDriveLeftSlaveId);

        rightMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, kVelocityControlSlot, Constants.DriveTrain.kCANBusConfigTimeoutMS);
        leftMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, kVelocityControlSlot, Constants.DriveTrain.kCANBusConfigTimeoutMS);

        rightMaster.setInverted(false);
        rightSlave.setInverted(false);
        leftMaster.setInverted(true);
        leftSlave.setInverted(true);

        rightMaster.setSensorPhase(false);
        leftMaster.setSensorPhase(false);

        //Configure talons for follower mode
        rightSlave.set(ControlMode.Follower, rightMaster.getDeviceID());
        leftSlave.set(ControlMode.Follower, leftMaster.getDeviceID());

        try {
            /***********************************************************************
             * navX-MXP:
             * - Communication via RoboRIO MXP (SPI, I2C, TTL UART) and USB.
             * - See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface.
             *
             * navX-Micro:
             * - Communication via I2C (RoboRIO MXP or Onboard) and USB.
             * - See http://navx-micro.kauailabs.com/guidance/selecting-an-interface.
             *
             * Multiple navX-model devices on a single robot are supported.
             ************************************************************************/
            navXGyro = new AHRS(SPI.Port.kMXP);
            //ahrs = new AHRS(SerialPort.Port.kMXP, SerialDataType.kProcessedData, (byte)50);
            navXGyro.enableLogging(true);
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
            System.out.println("Error instantiating navX MXP:  " + ex.getMessage());
        }
    }


    public void drive(double tank, double turn) { //drives the motors depending
        // on the joystick values and the drive mode

        double leftSpeed = 0;
        double rightSpeed = 0;


        leftSpeed = tank - turn;
        rightSpeed = tank + turn;


        leftMaster.set(ControlMode.PercentOutput, leftSpeed);
        rightMaster.set(ControlMode.PercentOutput, rightSpeed);

    }

    private double checkbounds(double Speed){ //this checks to make sure the speed is between 1 & -1
        if (Speed > 1){
            return 1.0;
        }else if(Speed < -1){
            return -1.0;
        }else{
            return Speed;
        }

    }

    public double getLeftEncoder(){
        return leftMaster.getSelectedSensorPosition(0);
    }
    public double getRightEncoder(){
        return rightMaster.getSelectedSensorPosition(0);
    }

    public void zeroGyro() {
        if (navXGyro != null) {
            System.out.println("Reseting Gyro");
            try {
                navXGyro.reset();
            } catch  (Exception exc) {
                System.out.println("DANGER: Failed to reset Gyro" + exc.getMessage() + " ---- ");
                exc.printStackTrace();
            }
            if (navXGyro.isCalibrating())
            {
                System.out.println("Gyro still calibrating");
            }
            System.out.println("Gyro Reset");
        } else {
            System.out.println("DANGER: NO GYRO!!!!");
        }

    }

    /**
     * @return gyro angle in degrees
     */
    public double getGyroAngleDegrees() {
        if (navXGyro != null)
        {
            return navXGyro.getAngle(); //NOTE: getAngle tracks all rotations from init, so it can go beyond 360 and -360
        } else {
            System.out.println("DANGER: NO GYRO!!!!");
            return 0;
        }
    }
}

