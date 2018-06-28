package com.team2052.autokrawler.auto;

public class AutoModeRunner {
    Thread autonomousThread; //create a thread
    AutoModeBase automode; //create an automode base can be done this way because its abstract FACT CHECK THIS

    public void start(){
        if (automode == null){
            return;
        }
        autonomousThread = new Thread(/* lines 15 to 20 == */() -> automode.start()); //this is a lambda expression.
        /* instead of having to write the code that is commented out you can compress it to just a () ->
        private Runnable autoRunnable = new Runnable() {
            @Override
            public void run() {
               autoMode.start();
            }
        }; */
        automode.start();
    }

    public void setAutomode(AutoModeBase automode){ //sets this classes automode and prints the name to the console
        System.out.println(automode);
        this.automode = automode;
    }

    public void stop(){
        if (automode != null){
            automode.stop();
        }
        autonomousThread = null;
    }
}
