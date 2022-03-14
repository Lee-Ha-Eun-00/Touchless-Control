package com.teamSLL.mlkit.Interface;

import com.google.mlkit.vision.face.Face;

import java.util.List;

public class MotionRecongition {
    private HeadRecognition hr;
    private EyeRecognition er;
    private MouthRecognition mr;

    public static final int MOTION_DEFAULT = 0x0;
    public static final int HEAD_UP = 0x0000001;
    public static final int HEAD_DOWN = 0x0000010;
    public static final int HEAD_LEFT = 0x0000100;
    public static final int HEAD_RIGHT = 0x0001000;
    public static final int MOUSE_OPEN = 0x0010000;
    public static final int EYE_CLOSED_SHORT = 0x0100000;
    public static final int EYE_CLOSED_LONG = 0x1000000;

    public MotionRecongition(){
        hr = new HeadRecognition(0,0,15,15);
        er = new EyeRecognition(2, 60);
        mr = new MouthRecognition(50,2);
    }

    public int getHeadRLEvent(Face face){
        int result = hr.generationRL(face);
        switch(result){
            case -1:
                return HEAD_RIGHT;
            case 1:
                return HEAD_LEFT;
            default:
                return MOTION_DEFAULT;
        }
    }
    public int getHeadUDEvent(Face face){
        int result = hr.generationUD(face);
        switch(result){
            case -1:
                return HEAD_DOWN;
            case 1:
                return HEAD_UP;
            default:
                return MOTION_DEFAULT;
        }
    }
    public int getEyeCLosedEvent(Face face){
        int result = er.generationClosed(face);
        switch(result){
            case -1:
                return EYE_CLOSED_LONG;
            case 1:
                return EYE_CLOSED_SHORT;
            default:
                return MOTION_DEFAULT;
        }
    }
    public int getMouseOpenEvent(Face face){
        int result = mr.generationOpen(face);
        switch(result){
            case 1:
                return MOUSE_OPEN;
            default:
                return MOTION_DEFAULT;
        }
    }

    public int getAllEvent(Face face){
        // return 0x0x0010110;
        return getHeadRLEvent(face)
                | getHeadUDEvent(face)
                | getEyeCLosedEvent(face)
                | getMouseOpenEvent(face);

        /*
        * json으로 출력?
        * {
        *   headUD : UP,
        *   headRL : CENTER,
        *   Eye : Closed Short,
        *   Mouse : Open
        * }
        * */
    }
}
