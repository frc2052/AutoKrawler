package com.team2052.deepspace.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import com.team2052.deepspace.Constants;
import com.team2052.lib.DriveSignal;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrainController {

    // Instance of DriveTrainController class to be created in Robot.java class by running get instance
    private static DriveTrainController singleDriveTrainControllerInstance = new DriveTrainController();
    public static DriveTrainController getInstance() { return singleDriveTrainControllerInstance; }

    AHRS navXGyro = null;

    public final TalonSRX rightMaster;
    public final TalonSRX leftMaster;
    private final VictorSPX rightSlave;
    private final VictorSPX leftSlave;
    private final VictorSPX rightSlave2;
    private final VictorSPX leftSlave2;

    private Solenoid shifterIn;
    private Solenoid shifterOut;

    DriveTrainController(){
        rightMaster = new TalonSRX(Constants.DriveTrain.kDriveRightMasterId);
        leftMaster = new TalonSRX(Constants.DriveTrain.kDriveLeftMasterId);
        rightSlave = new VictorSPX(Constants.DriveTrain.kDriveRightSlaveId);
        leftSlave = new VictorSPX(Constants.DriveTrain.kDriveLeftSlaveId);
        rightSlave2 = new VictorSPX(Constants.DriveTrain.kDriveRightSlave2Id);
        leftSlave2 = new VictorSPX(Constants.DriveTrain.kDriveLeftSlave2Id);

        rightMaster.configFactoryDefault();
        rightSlave.configFactoryDefault();
        leftMaster.configFactoryDefault();
        leftSlave.configFactoryDefault();
        leftSlave2.configFactoryDefault();
        rightSlave2.configFactoryDefault();

        rightMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.DriveTrain.kVelocityControlSlot, Constants.DriveTrain.kCANBusConfigTimeoutMS);
        leftMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.DriveTrain.kVelocityControlSlot, Constants.DriveTrain.kCANBusConfigTimeoutMS);

        rightMaster.setInverted(false);
        rightSlave.setInverted(false);
        rightSlave2.setInverted(false);
        leftMaster.setInverted(true);
        leftSlave.setInverted(true);
        leftSlave2.setInverted(true);

        rightMaster.setSensorPhase(true);
        leftMaster.setSensorPhase(true);

        rightMaster.setNeutralMode(NeutralMode.Brake);
        leftMaster.setNeutralMode(NeutralMode.Brake);
        //Configure talons for follower mode
        rightSlave.follow(rightMaster);
        rightSlave2.follow(rightMaster);
        leftSlave.follow(leftMaster);
        leftSlave2.follow(leftMaster);

        shifterIn = new Solenoid(Constants.DriveTrain.kShiftInSolenoidID);
        shifterOut = new Solenoid(Constants.DriveTrain.kShiftOutSolenoidID);

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

    public void stop(){
        drive(new DriveSignal(0,0));
    }

    public void setHighGear(boolean highGear) {
        shifterOut.set(!highGear);
        shifterIn.set(highGear);
    }

    public void drive(DriveSignal driveSignal) {
        //System.out.println("Left Speed = " + driveSignal.leftMotorSpeedPercent + " rightSpeed = " + driveSignal.rightMotorSpeedPercent);

        leftMaster.set(ControlMode.PercentOutput, driveSignal.leftMotorSpeedPercent);
        rightMaster.set(ControlMode.PercentOutput, driveSignal.rightMotorSpeedPercent);

    }

    public void driveAutoVelocityControl(double leftVel, double rightVel){
        //in/sec * rot/in * ticks/rot * .1 to get ticks/100ms
        System.out.println("Left Vel = " + leftVel + " right Vel = " + rightVel);
        leftMaster.set(ControlMode.Velocity, ((leftVel * Constants.DriveTrain.kTicksPerRot)/Constants.DriveTrain.kDriveWheelCircumferenceInches)/3);
        rightMaster.set(ControlMode.Velocity, ((rightVel * Constants.DriveTrain.kTicksPerRot)/Constants.DriveTrain.kDriveWheelCircumferenceInches)/3);

        System.out.println("SENSOR VEL:" + leftMaster.getSelectedSensorVelocity() * (1.0/Constants.DriveTrain.kTicksPerRot) * Constants.DriveTrain.kDriveWheelCircumferenceInches * 10);


    }

    public void driveAutoMotionProfileControl(){

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
        SmartDashboard.putNumber("Left Encoder",leftMaster.getSelectedSensorPosition(0));

        return leftMaster.getSelectedSensorPosition(0);
    }
    public double getRightEncoder(){
        SmartDashboard.putNumber("Right Encoder",rightMaster.getSelectedSensorPosition(0));
        return rightMaster.getSelectedSensorPosition(0);
    }

    public void resetEncoders(){
        leftMaster.setSelectedSensorPosition(0, Constants.DriveTrain.kVelocityControlSlot, Constants.DriveTrain.kCANBusConfigTimeoutMS);
        rightMaster.setSelectedSensorPosition(0, Constants.DriveTrain.kVelocityControlSlot, Constants.DriveTrain.kCANBusConfigTimeoutMS);
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
            System.out.println("Gyro reset");
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

    public double getGyroAngleRadians() {
        if (navXGyro != null)
        {
            return navXGyro.getAngle() * 0.017453; //NOTE: getAngle tracks all rotations from init, so it can go beyond 2PI and -2PI
        } else {
            System.out.println("DANGER: NO GYRO!!!!");
            return 0;
        }
    }
    /*
    // old gyro code
     public double getOldGyroAngleDegrees() {
        // It just so happens that the gyro outputs 4x the amount that it actually turned
        return -gyro.getAngleZ() / 4.0;

    }
    public double getOldGyroAngleRadians(){
        return getOldGyroAngleDegrees() * 0.017453;
    }
    */
}

