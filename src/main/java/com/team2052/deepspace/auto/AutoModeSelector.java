package com.team2052.deepspace.auto;

import com.team2052.deepspace.Constants;
import com.team2052.deepspace.auto.modes.DontMove;
import com.team2052.deepspace.auto.modes.Test;
import com.team2052.deepspace.auto.modes.WaitToStart;
import com.team2052.lib.Autonomous.Position2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoModeSelector {
    private static SendableChooser<PositionSelection> sendableChooserPosition;
    private static SendableChooser<FirstTargetSelection> sendableChooserFirstTarget;
    private static SendableChooser<SecondTargetSelection> sendableChooserSecondTarget;

    public static void putToShuffleBoard() {
        sendableChooserPosition = new SendableChooser<PositionSelection>();
        sendableChooserFirstTarget = new SendableChooser<FirstTargetSelection>();
        sendableChooserSecondTarget = new SendableChooser<SecondTargetSelection>();

        for (int i = 0; i < PositionSelection.values().length; i++) {
            PositionSelection mode = PositionSelection.values()[i];
            if (i == 0) {
                sendableChooserPosition.setDefaultOption(mode.name, mode);
            } else {
                sendableChooserPosition.addOption(mode.name, mode);
            }
        }

        for (int i = 0; i < FirstTargetSelection.values().length; i++) {
            FirstTargetSelection mode = FirstTargetSelection.values()[i];
            if (i == 0) {
                sendableChooserFirstTarget.setDefaultOption(mode.name, mode);
            } else {
                sendableChooserFirstTarget.addOption(mode.name, mode);
            }
        }

        for (int i = 0; i < SecondTargetSelection.values().length; i++) {
            SecondTargetSelection mode = SecondTargetSelection.values()[i];
            if (i == 0) {
                sendableChooserSecondTarget.setDefaultOption(mode.name, mode);
            } else {
                sendableChooserSecondTarget.addOption(mode.name, mode);
            }
        }
        SmartDashboard.putData("Start Position", sendableChooserPosition);
        SmartDashboard.putData("First Target", sendableChooserFirstTarget);
        SmartDashboard.putData("Second Target", sendableChooserSecondTarget);
        SmartDashboard.putBoolean("Wait To Start?", waitForStart);
    }

    private static PositionSelection lastPosition = null;
    private static FirstTargetSelection lastFirst = null;
    private static SecondTargetSelection lastSecond = null;
    private static AutoMode selectedAuto = null;
    private static AutoMode secondAuto  = null;
    private static boolean waitForStart = false;

    public static AutoMode getSelectedAutoMode() {
        waitForStart = SmartDashboard.getBoolean("Wait To Start?", false);
        PositionSelection position = sendableChooserPosition.getSelected();
        FirstTargetSelection first = sendableChooserFirstTarget.getSelected();
        SecondTargetSelection second = sendableChooserSecondTarget.getSelected();

        //set defaults if none selected
        if (position == null){
            position = PositionSelection.CENTER;
        }
        if (first == null) {
            first = FirstTargetSelection.NONE;
        }
        if (second == null) {
            second = SecondTargetSelection.NONE;
        }

        //TODO: REVIEW - comment this logic
        if (selectedAuto == null || selectedAuto.isActionFinished() || position != lastPosition || first != lastFirst || second != lastSecond) {
            lastPosition = position;
            lastFirst = first;
            lastSecond = second;
            //System.out.println("pos: "+ lastPosition.name + " " + lastPosition + " first: " + lastFirst.name + " " + lastFirst);
            switch (position) {
                case TEST:
                    System.out.println("selected test");
                    selectedAuto = new Test(position.startPos);
                    secondAuto = new DontMove();
                    break;
                case LEFT:
                case LEFTHAB2:
                    switch (first) {
                        case NONE:
                            secondAuto = new DontMove();
                            break;
                        default:
                            selectedAuto = null;
                    }

                    switch (second){

                        case NONE:
                            secondAuto = new DontMove();
                            break;

                        default:
                            secondAuto = null;
                            break;
                    }
                    break;
                case RIGHT:
                case RIGHTHAB2:
                    switch (first) {
                        case NONE:
                            secondAuto = new DontMove();
                            break;
                        default:
                            selectedAuto = null;
                    }

                    switch (second){
                        case NONE:
                            secondAuto = new DontMove();
                            break;
                        default:
                            secondAuto = null;
                            break;
                    }

                    break;
                case CENTER:
                    switch (first) {
                        case NONE:
                            secondAuto = new DontMove();
                            break;
                        default:
                            selectedAuto = null;
                    }

                    switch (second){
                        case NONE:
                            secondAuto = new DontMove();
                            break;
                        default:
                            secondAuto = null;
                            break;
                    }
                    break;
                case CENTERLEFT:
                    switch (first) {
                        case NONE:
                            secondAuto = new DontMove();
                            break;
                        default:
                            secondAuto = null;
                            break;
                    }

                    switch (second){
                        case NONE:
                            secondAuto = new DontMove();
                            break;
                        default:
                            secondAuto = null;
                            break;
                    }
                    break;
                case CENTERRIGHT:
                    switch (first){
                        case NONE:
                            secondAuto = new DontMove();
                            break;
                        default:
                            secondAuto = null;
                            break;
                    }

                    switch (second){
                        case NONE:
                            secondAuto = new DontMove();
                            break;
                        default:
                            secondAuto = null;
                            break;
                    }
                    break;
                case NONE:
                    switch (first){
                        case NONE:
                            selectedAuto = new DontMove();
                            break;
                        default:
                            selectedAuto = null;
                    }

                default:
                    selectedAuto = null; //set null because we check if its null on line for smart dashboard
            }
            if(selectedAuto != null && secondAuto != null){
                System.out.println("INITING: "+selectedAuto.getClass().getSimpleName());
                selectedAuto.init();
                System.out.println("INITING SECOND: "+secondAuto.getClass().getSimpleName());
                secondAuto.init();

                if(waitForStart){
                    AutoMode delayStart = new WaitToStart(position.startPos);
                    delayStart.init();
                    delayStart.appendAction(selectedAuto.getAction());
                    delayStart.appendAction(secondAuto.getAction());
                    selectedAuto = delayStart;
                }else {
                    selectedAuto.appendAction(secondAuto.getAction());
                }
            }
        }

        if (selectedAuto != null && secondAuto != null) {
            SmartDashboard.putBoolean("Does AutoMode Exist?", true);

            return selectedAuto;
        } else {
            SmartDashboard.putBoolean("Does AutoMode Exist?", false);
            return new DontMove();
        }
    }

    public static Position2d getStartingPos() {
        return sendableChooserPosition.getSelected().startPos;
    }

    public enum PositionSelection {
        NONE("Select Start", new Position2d(0, 0)),
        CENTER("StartCenter", new Position2d(0, 0)),
        CENTERLEFT("StartCenterLeft", new Position2d(0,-10)),
        CENTERRIGHT("StartCenterRight", new Position2d(0,10)),
        LEFT("StartLeft", new Position2d(0, Constants.Autonomous.kStartLeftInchOffset)),
        LEFTHAB2("startLeftHab2", new Position2d(Constants.Autonomous.kStartHab2Offset, Constants.Autonomous.kStartLeftInchOffset)),
        RIGHT("StartRight", new Position2d(0, Constants.Autonomous.kStartRightInchOffset)),
        RIGHTHAB2("startRightHab2", new Position2d(Constants.Autonomous.kStartHab2Offset, Constants.Autonomous.kStartRightInchOffset)),
        TEST("test", new Position2d(0, -10));

        public String name;
        public Position2d startPos;

        PositionSelection(String name, Position2d startPos) {
            this.name = name;
            this.startPos = startPos;
        }
    }

    public enum FirstTargetSelection {
        NONE("Select Target One"),
        FCLHATCH("ForwardCenterLeftHatch"),
        FCRHATCH("ForwardCenterRightHatch"),
        FLCHATCH("ForwardLeftCloseHatch"),
        //FLMHATCH("ForwardLeftMiddleHatch"),
        //FLFHATCH("ForwardLeftFarHatch"),
        FRCHATCH("ForwardRightCloseHatch");
        //FRMHATCH("ForwardRightMiddleHatch"),
        //FRFHATCH("ForwardRightFarHatch"),
        //FLSRCHATCH("ForwardLeftRocketClose"),
        //BLSRFHATCH("BackwardLeftRocketFar"),

        //BCLHATCH("BackCenterLeftHatch"),
        //BLCHATCH("BackLeftCloseHatch"),
        //BCRHATCH("BackCenterRightHatch"),
        //BRCHATCH("BackRightCloseHatch");
            /*
        BLMHATCH("BackLeftMiddleHatch"),
        BLFHATCH("BackLeftFarHatch"),

        BRMHATCH("BackRightMiddleHatch"),
        BRFHATCH("BackRightFarHatch")*/

        public String name;

        FirstTargetSelection(String name) {
            this.name = name;
        }
    }

    //ADD All POSSIBLE COMBONATIONS KEYWORDS  CREATE ALL CLASSES!!!
    public enum SecondTargetSelection {
        NONE("None"),
        BACKUP("Backup"),
        LHATCH("CenterLeftHatch"),
        RHATCH("CenterRightHatch"),
        //LFHATCH("LeftFarHatch"),
        //FHATCH("RightFarHatch"),
        LMHATCH("LeftMiddleHatch"),
        RMHATCH("RightMiddleHatch"),
        LCHATCH("LeftCloseHatch"),
        RCHATCH("RightCloseHatch");


        public String name;

        SecondTargetSelection(String name) {
            this.name = name;
        }
    }

    public static void nullSelectedAutoMode() {
        selectedAuto = null;
    }
}

