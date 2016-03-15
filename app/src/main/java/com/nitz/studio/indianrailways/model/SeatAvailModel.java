package com.nitz.studio.indianrailways.model;

/**
 * Created by nitinpoddar on 11/7/15.
 */
public class SeatAvailModel {

    public static int responseCode;
    public static String trName;
    public static String trNo;
    public static String error;
    public static String classCode;
    public static String className;

    private String travelDate;
    private String status;

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
