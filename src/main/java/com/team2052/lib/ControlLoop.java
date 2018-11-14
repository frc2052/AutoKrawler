package com.team2052.lib;
import edu.wpi.first.wpilibj.Notifier;

import java.util.ArrayList;

public class ControlLoop {
    private final Object runningThread = new Object();
    private double periodSec = 1.0 / 100.0; //default to hardcode
    private ArrayList<ILoopable> loopables = new ArrayList<ILoopable>();

    private Runnable runnable = () -> {
        synchronized (runningThread) {
            for (ILoopable loopable : loopables) {
                loopable.update();
            }
        }
    };

    private Notifier notifier;

    public ControlLoop(double periodSec) {
        this.periodSec = periodSec;
        notifier = new Notifier(runnable);
    }

    public synchronized void start() {
        for (ILoopable loopable : loopables) {
            loopable.onStart();
        }

        synchronized (runningThread) {
            notifier.startPeriodic(periodSec);
        }
    }

    public synchronized void stop() {
        synchronized (runningThread) {
            notifier.stop();
        }

        for (ILoopable loopable : loopables) {
            loopable.onStop();
        }
    }

    public synchronized void addLoopable(ILoopable loopable) {
        synchronized (runningThread) {
            loopables.add(loopable);
        }
    }
}