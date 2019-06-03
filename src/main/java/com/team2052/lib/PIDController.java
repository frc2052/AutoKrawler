package com.team2052.lib;

public class PIDController {

    private double errorSum;
    private double p;
    private double i;
    private double d;
    private double lastSensorValue;
    private boolean running = false;

    public PIDController(double p, double i, double d){
        this.p = p;
        this.i = i;
        this.d = d;
    }

    public double getOutput(double sensor, double target){
        double difference = (target - sensor);

        double pOutput = p * difference;

        errorSum+= difference;
        double iOutput = i * errorSum;

        if(!running){
            running = true;
            lastSensorValue = sensor;
        }
        double dOutput = -d * (sensor-lastSensorValue);

        return pOutput + iOutput + dOutput;
    }

    public boolean isRunning(){
        return running;
    }
}