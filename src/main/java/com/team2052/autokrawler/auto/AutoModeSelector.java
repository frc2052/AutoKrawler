package com.team2052.autokrawler.auto;

import com.team2052.autokrawler.auto.modes.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class AutoModeSelector {
    private static SendableChooser<AutoModeDefinition> sendableChooserAutoMode;

    public enum AutoModeDefinition{
        DONT_MOVE("Don't Move", DontMove.class);

        //create a class variable type and check if it extends AutoMode. the name of this variable is clazz
        private final Class<? extends  AutoMode> clazz;
        public final String name;

        AutoModeDefinition(String name, Class<? extends AutoMode> clazz){
            this.name = name;
            this.clazz = clazz;
        }
    }
    public enum Side{
        RED, BLUE
    }
}
