package com.team2052.lib.Autonomous;

public class RateLimiter {

    public double constrain(double input, double negMaxChange, double posMaxChange){
        if (input > posMaxChange){
            return posMaxChange;
        }else if(input < negMaxChange){
            return negMaxChange;
        }else{
            return input;
        }
    }
}
