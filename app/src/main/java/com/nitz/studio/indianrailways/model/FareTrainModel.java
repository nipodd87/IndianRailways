package com.nitz.studio.indianrailways.model;

/**
 * Created by nitinpoddar on 11/5/15.
 */
public class FareTrainModel {

    public static int responseCode;
    public static String trainNo;
    public static String trainName;
    private String classCode;
    private String classFare;
    private String className;


    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassFare() {
        return classFare;
    }

    public void setClassFare(String classFare) {
        this.classFare = classFare;
    }


}
