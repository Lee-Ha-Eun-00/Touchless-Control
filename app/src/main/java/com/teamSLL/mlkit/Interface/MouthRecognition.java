package com.teamSLL.mlkit.Interface;

import static com.google.mlkit.vision.face.FaceContour.LOWER_LIP_TOP;
import static com.google.mlkit.vision.face.FaceContour.UPPER_LIP_BOTTOM;

import android.graphics.PointF;

import com.google.mlkit.vision.face.Face;

import java.util.List;

public class MouthRecognition {
    private double openThreshold;
    private int frames, frameThreshold;

    public MouthRecognition(){
        this.openThreshold = 100;
        this.frameThreshold = 6;
        this.frames = 0;
    }

    //1s = 3frames
    public MouthRecognition(double threshold, int seconds){
        this.openThreshold = threshold;
        this.frameThreshold = seconds*3;
        this.frames = 0;
    }

    public int generationOpen(Face face){
        double sum = 0;
        if(face.getContour(LOWER_LIP_TOP) != null & face.getContour(UPPER_LIP_BOTTOM) != null){
            List<PointF> top = face.getContour(LOWER_LIP_TOP).getPoints();
            List<PointF> bottom = face.getContour(UPPER_LIP_BOTTOM).getPoints();

            for(int i=0;i<9;i++){
                sum += top.get(i).y - bottom.get(8 - i).y;
            }
            if(sum > openThreshold){
                this.frames++;
            }else{
                this.frames = 0;
            }
        }
        if(this.frames >= this.frameThreshold){
            return 1;
        }
        return 0;
    }

    public void setOpenThreshold(double threshold) {
        this.openThreshold = threshold;
    }
    public void setFrameThreshold(int seconds) {
        this.frameThreshold = seconds*3;
    }

    public double getOpenThreshold() {
        return openThreshold;
    }
    public int getFrameThreshold() {
        return frameThreshold;
    }
    public int getTimes() {
        return frames;
    }
}
