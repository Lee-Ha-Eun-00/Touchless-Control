package com.teamSLL.mlkit.Interface;

import com.google.mlkit.vision.face.Face;

public class HeadRecognition {
    private double x_center, y_center;
    private double x_threshold, y_threshold;

    public HeadRecognition(){
        this.x_center = 0;
        this.y_center = 0;
        this.x_threshold = 10;
        this.y_threshold = 10;
    }
    // y is left/right, x is up/down
    public HeadRecognition(double ud_center, double rl_center, double ud_threshold, double rl_threshold){
        this.x_center = ud_center;
        this.y_center = rl_center;
        this.x_threshold = ud_threshold;
        this.y_threshold = rl_threshold;
    }

    public int generationUD(Face face){
        double x = face.getHeadEulerAngleX();
        int result = 0;
        if(x < x_center - x_threshold)   result = -1;
        else if(x > x_center + x_threshold) result = 1;

        return result;
    }
    public int generationRL(Face face){
        double y = face.getHeadEulerAngleY();;
        int result = 0;
        if(y < y_center - y_threshold)   result = -1;
        else if(y > y_center + y_threshold) result = 1;

        return result;
    }

    public void setX_center(double x_center) {
        this.x_center = x_center;
    }
    public void setY_center(double y_center) {
        this.y_center = y_center;
    }
    public void setX_threshold(double x_threshold){
        this.x_threshold = x_threshold;
    }
    public void setY_threshold(double y_threshold){
        this.y_threshold = y_threshold;
    }
    public double getX_center() {
        return x_center;
    }
    public double getY_center() {
        return y_center;
    }
    public double getX_threshold() {
        return x_threshold;
    }
    public double getY_threshold() {
        return y_threshold;
    }
}
