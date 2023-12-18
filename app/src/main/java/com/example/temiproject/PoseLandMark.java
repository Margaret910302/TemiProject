package com.example.temiproject;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.ArrayList;

public class PoseLandMark {
    float poseCounter;    // 計數器
    static int direction;
    private static final String TAG = "PoseLandMark";
    float x,y, visible;
    PoseLandMark(){
        poseCounter = 0;
        direction = 0;
    }
    PoseLandMark(float x, float y, float visible) {
        this.x = x;
        this.y = y;
        this.visible = visible;
        poseCounter = 0;
        direction = 0;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setVisible(float visible) {
        this.visible = visible;
    }
    public void setPoseCounter(float count){
        this.poseCounter = count;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVisible() {
        return visible;
    }
    public float getPoseCounter(){
        return poseCounter;
    }

    /* 解析人體骨架的關鍵點 */
    public static ArrayList<PoseLandMark> getPoseMarkers(LandmarkProto.NormalizedLandmarkList key) {
        ArrayList<PoseLandMark> poseMarkers = new ArrayList<PoseLandMark>();
        int landmarkIndex = 0;
        for (LandmarkProto.NormalizedLandmark landmark : key.getLandmarkList()) {
            PoseLandMark marker = new PoseLandMark(landmark.getX(), landmark.getY(), landmark.getVisibility());
            ++landmarkIndex;
            poseMarkers.add(marker);
        }
        return poseMarkers;
    }

    /* 偵測到的姿勢關鍵點的數量 */
    public static String getkey(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        return "Pose landmarks: " + key.getLandmarkCount() + "\n";
    }

    /* 計算並回傳關鍵點之間的角度 */
    public static double getAngle(PoseLandMark firstPoint, PoseLandMark midPoint, PoseLandMark lastPoint) {
        double result =
                Math.toDegrees(
                        Math.atan2(lastPoint.getY() - midPoint.getY(),lastPoint.getX() - midPoint.getX())
                                - Math.atan2(firstPoint.getY() - midPoint.getY(),firstPoint.getX() - midPoint.getX()));
        result = Math.abs(result);
        if (result > 180) {
            result = (360.0 - result);
        }
        return result;
    }

    /* 伏地挺身 */
    public static String pushup(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_elbow = getAngle(poseMarkers.get(11),poseMarkers.get(13),poseMarkers.get(15));   // 手肘
        double left_greater = getAngle(poseMarkers.get(11), poseMarkers.get(23), poseMarkers.get(25));   // 軀幹
        double right_elbow = getAngle(poseMarkers.get(12),poseMarkers.get(14),poseMarkers.get(16));   // 手肘
        double right_greater = getAngle(poseMarkers.get(12), poseMarkers.get(24), poseMarkers.get(26));   // 軀幹

        String back;
        if( left_elbow <= 90  && right_elbow <= 90  ){
            back = "DOWN";
        }
        else if( left_elbow >= 125  && right_elbow >= 125 ){
            back = "UP";
        }
        else{
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "elbow :" + left_elbow + "\n"+
                "greater :" + left_greater + "\n"+
                back
        );

        return back;
    }

    /* 向下甩手 */
    public static String handDown(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_elbow = getAngle(poseMarkers.get(11),poseMarkers.get(13),poseMarkers.get(15));   // 左手肘
        double right_elbow = getAngle(poseMarkers.get(12),poseMarkers.get(14),poseMarkers.get(16));   // 右手肘

        String back;
        if( left_elbow <= 17  && right_elbow <= 17  ){
            back = "DOWN";
        }
        else if( left_elbow >= 140  && right_elbow >= 140 ){
            back = "UP";
        }
        else{
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "left_elbow :" + left_elbow + "\n"+
                "right_elbow :" + right_elbow + "\n"+
                back
        );

        return back;
    }

    /* 搖頭擺尾 */
    public static String shakeHead(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_hip = getAngle(poseMarkers.get(11),poseMarkers.get(23),poseMarkers.get(25));   // 左髖關節
        double right_hip = getAngle(poseMarkers.get(12),poseMarkers.get(24),poseMarkers.get(26));   // 右髖關節

        String back;
        if( left_hip <= 20  && right_hip <= 20  ){
            back = "DOWN";
        }
        else if( left_hip >= 130  && right_hip >= 130 ){
            back = "UP";
        }
        else{
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "left_hip :" + left_hip + "\n"+
                "right_hip :" + right_hip + "\n"+
                back
        );

        return back;
    }

    /* 輕提腳跟 */
    public static String tiptoe(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_ankle = getAngle(poseMarkers.get(25),poseMarkers.get(27),poseMarkers.get(29));   // 左腳踝
        double right_ankle = getAngle(poseMarkers.get(26),poseMarkers.get(28),poseMarkers.get(30));   // 右腳踝

        left_ankle = 360 - left_ankle;

        String back;
        if( left_ankle >= 205  && right_ankle <= 155  ){
            back = "DOWN";
        }
        else if( left_ankle <= 205  && right_ankle >= 155 ){
            back = "UP";
        }
        else{
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "left_ankle :" + left_ankle + "\n"+
                "right_ankle :" + right_ankle + "\n"+
                back
        );

        return back;
    }

    /* 轉體運動 */
    public static String rotateBody(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_shoulder = getAngle(poseMarkers.get(13),poseMarkers.get(11),poseMarkers.get(23));   // 左肩膀
        double right_shoulder = getAngle(poseMarkers.get(14),poseMarkers.get(12),poseMarkers.get(24));   // 右肩膀

        String back;
        if( left_shoulder <= 55  && right_shoulder <= 55  ){
            back = "DOWN";
        }
        else if( left_shoulder >= 130  && right_shoulder >= 130 ){
            back = "UP";
        }
        else{
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "left_shoulder :" + left_shoulder + "\n"+
                "right_shoulder :" + right_shoulder + "\n"+
                back
        );

        return back;
    }

    /* 雙手托天 */
    public static String handUp(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_hip = getAngle(poseMarkers.get(11),poseMarkers.get(23),poseMarkers.get(25));   // 左髖關節
        double right_hip = getAngle(poseMarkers.get(12),poseMarkers.get(24),poseMarkers.get(26));   // 右髖關節

        String back;
        if( left_hip <= 22  && right_hip <= 22  ){
            back = "DOWN";
        }
        else if( left_hip >= 170  && right_hip >= 170 ){
            back = "UP";
        }
        else{
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "left_hip :" + left_hip + "\n"+
                "right_hip :" + right_hip + "\n"+
                back
        );

        return back;
    }

    /* 蹲下起立 */
    public static String squatDown(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_knee = getAngle(poseMarkers.get(23),poseMarkers.get(25),poseMarkers.get(27));   // 左膝蓋
        double right_knee = getAngle(poseMarkers.get(24),poseMarkers.get(26),poseMarkers.get(28));   // 右膝蓋

        left_knee = 360 - left_knee;

        String back;
        if( left_knee <= 178  && right_knee <= 178  ){
            back = "DOWN";
        }
        else if( left_knee >= 285  && right_knee >= 75 ){
            back = "UP";
        }
        else{
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "left_knee :" + left_knee + "\n"+
                "right_knee :" + right_knee + "\n"+
                back
        );

        return back;
    }
}