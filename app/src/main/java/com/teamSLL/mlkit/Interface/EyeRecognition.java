package com.teamSLL.mlkit.Interface;

import com.google.mlkit.vision.face.Face;

public class EyeRecognition {
    private int shortFrameThreshold;
    private int longFrameThreshold;
    private int blinked;

    public EyeRecognition(){
        this.shortFrameThreshold = 9;
        this.longFrameThreshold = 100;
        this.blinked = 0;
    }
    //1s = 3times
    public EyeRecognition(int shortThreshold, int longThreshold){
        this.shortFrameThreshold = shortThreshold * 3;
        this.longFrameThreshold = longThreshold * 3;
        this.blinked = 0;
    }

    public int generationClosed(Face face){
        if(face.getLeftEyeOpenProbability() != null && face.getRightEyeOpenProbability() != null){
            if(face.getLeftEyeOpenProbability() < 0.1 && face.getRightEyeOpenProbability() < 0.1)
                blinked++;
            else blinked = 0;
        }
        if(blinked > longFrameThreshold){
            return -1;
        }
        if(blinked > shortFrameThreshold){
            return 1;
        }
        return 0;
    }
}
