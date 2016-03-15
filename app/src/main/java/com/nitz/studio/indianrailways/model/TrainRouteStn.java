package com.nitz.studio.indianrailways.model;


/**
 * Created by nitinpoddar on 11/1/15.
 */
public class TrainRouteStn {
    private int serialNo;
    private int distance;
    private int day;
    private String stationName;
    private String scheduleArrival;
    private String scheduleDept;
    public static int responseCode;
    public static String trainName;
    public static String fromStation;

    public static String days;

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getScheduleArrival() {
        return scheduleArrival;
    }

    public void setScheduleArrival(String scheduleArrival) {
        this.scheduleArrival = scheduleArrival;
    }

    public String getScheduleDept() {
        return scheduleDept;
    }

    public void setScheduleDept(String scheduleDept) {
        this.scheduleDept = scheduleDept;
    }
}
