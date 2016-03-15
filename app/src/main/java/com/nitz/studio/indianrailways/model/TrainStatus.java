package com.nitz.studio.indianrailways.model;

/**
 * Created by nitinpoddar on 10/31/15.
 */
public class TrainStatus {

    public static int responseCode;
    private int serialNo;
    private String scheduleArrival;
    private String actualArrival;
    private String stationName;
    private String statusOfArrival;
    private boolean hasArrived;
    private boolean hasDeparted;
    private int latemin;
    private String actualArrivalDate;
    private String scheduleArrivalDate;
    public static String position;

    public boolean isHasArrived() {
        return hasArrived;
    }

    public void setHasArrived(boolean hasArrived) {
        this.hasArrived = hasArrived;
    }

    public boolean isHasDeparted() {
        return hasDeparted;
    }

    public void setHasDeparted(boolean hasDeparted) {
        this.hasDeparted = hasDeparted;
    }

    public int getLatemin() {
        return latemin;
    }

    public void setLatemin(int latemin) {
        this.latemin = latemin;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getScheduleArrival() {
        return scheduleArrival;
    }

    public void setScheduleArrival(String scheduleArrival) {
        this.scheduleArrival = scheduleArrival;
    }

    public String getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(String actualArrival) {
        this.actualArrival = actualArrival;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStatusOfArrival() {
        return statusOfArrival;
    }

    public void setStatusOfArrival(String statusOfArrival) {
        this.statusOfArrival = statusOfArrival;
    }

    public String getActualArrivalDate() {
        return actualArrivalDate;
    }

    public void setActualArrivalDate(String actualArrivalDate) {
        this.actualArrivalDate = actualArrivalDate;
    }

    public String getScheduleArrivalDate() {
        return scheduleArrivalDate;
    }

    public void setScheduleArrivalDate(String scheduleArrivalDate) {
        this.scheduleArrivalDate = scheduleArrivalDate;
    }
}
