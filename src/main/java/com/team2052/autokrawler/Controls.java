package com.team2052.autokrawler;

import edu.wpi.first.wpilibj.Joystick;

public class Controls {

    private static Controls instance = new Controls();

    private Controls() {
    }

    public static Controls getInstance() {
        return instance;
    }

    private Joystick leftPrimaryStick = new Joystick(0); //left joystick
    private Joystick rightPrimaryStick = new Joystick(1);//right joystick
    private Joystick secondaryStick = new Joystick(2);

    public double getTankJoy1() {
        double val = -leftPrimaryStick.getY();
        if (val < .15 && val > -.15)
        {
            val = 0;
        }
        return val;
    } //these return the values for the joysticks for other classes

    public double getTurnJoy1() {
        return leftPrimaryStick.getX();
    }

    public double getTankJoy2() {
        return rightPrimaryStick.getY();
    }

    public double getTurnJoy2()
    {
        double val = rightPrimaryStick.getX();
        if (val < .15 && val > -.15)
        {
            val = 0;
        }
        return val;
    }

    public boolean reset(){
        return leftPrimaryStick.getTrigger();
    }
}
