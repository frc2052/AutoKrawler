package com.team2052.lib;

/**
 * Created by KnightKrawler on 1/19/2018.
 */
public class DriveSignal { //a drive signal is a motor speed for both motors. This allows us to send both variabes at once
    public double leftMotorSpeedPercent;
    public double rightMotorSpeedPercent;

    public
    DriveSignal(double left, double right) {
        this.leftMotorSpeedPercent = left;
        this.rightMotorSpeedPercent = right;
    }

    public static DriveSignal NEUTRAL = new DriveSignal(0, 0);

    @Override
    public String toString() { //overrides the intrinsic tostring method to make debugging easier
        return "L: " + leftMotorSpeedPercent + ", R: " + rightMotorSpeedPercent;
    }
}
