package com.team2052.lib;

import com.team2052.deepspace.Constants;

public class DriveHelper {
    double mQuickStopAccumulator;
    public static final double kThrottleDeadband = 0.1;
    private static final double kWheelDeadband = 0.1;
    private static final double kTurnSensitivity = 1.25;
    private DriveSignal mSignal = new DriveSignal(0, 0);

    /**
     * Helper for driving
     * the "turning" stick controls the curvature of the robot's path rather than
     * its rate of heading change. This helps make the robot more controllable at
     * high speeds. Also handles the robot's quick turn functionality - "quick turn"
     * overrides constant-curvature turning for turn-in-place maneuvers.
     */
    public DriveSignal drive(double throttle, double wheel, boolean isQuickTurn) {
        wheel = handleDeadband(wheel, kWheelDeadband);
        throttle = handleDeadband(throttle, kThrottleDeadband);

        double overPower;

        double angularPower;

        if (isQuickTurn) {
            if (Math.abs(throttle) < 0.2) {
                double alpha = 0.1;
                mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator + alpha * limit(wheel, 1.0) * 2;
            }
            overPower = 1.0;
            angularPower = wheel * Constants.DriveTrain.kTurnInPlaceSpeed;
        } else {
            overPower = 0.0;
            angularPower = Math.abs(throttle) * wheel * kTurnSensitivity - mQuickStopAccumulator;
            if (mQuickStopAccumulator > 1) {
                mQuickStopAccumulator -= 1;
            } else if (mQuickStopAccumulator < -1) {
                mQuickStopAccumulator += 1;
            } else {
                mQuickStopAccumulator = 0.0;
            }
        }

        double rightPwm = throttle - angularPower;
        double leftPwm = throttle + angularPower;
        if (leftPwm > 1.0) {
            rightPwm -= overPower * (leftPwm - 1.0);
            leftPwm = 1.0;
        } else if (rightPwm > 1.0) {
            leftPwm -= overPower * (rightPwm - 1.0);
            rightPwm = 1.0;
        } else if (leftPwm < -1.0) {
            rightPwm += overPower * (-1.0 - leftPwm);
            leftPwm = -1.0;
        } else if (rightPwm < -1.0) {
            leftPwm += overPower * (-1.0 - rightPwm);
            rightPwm = -1.0;
        }

        mSignal.rightMotorSpeedPercent = rightPwm;
        mSignal.leftMotorSpeedPercent = leftPwm;
        return mSignal;
    }

    public double handleDeadband(double val, double deadband) {
        return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
    }

    private static double limit(double value, double limit) {
        if (Math.abs(value) > limit) {
            if (value < 0) {
                return -limit;
            } else {
                return limit;
            }
        }
        return value;
    }
}
