package com.nitz.studio.indianrailways.model;

/**
 * Created by nitinpoddar on 11/3/15.
 */
public class TrainModel {

    public static int responseCode;
    public static int totalTrain;
    private int serialNo;
    private String trainNumber;
    private String trainName;
    private String deptTime;
    private String arrivalTime;
    private String daysRun;

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getDeptTime() {
        return deptTime;
    }

    public void setDeptTime(String deptTime) {
        this.deptTime = deptTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDaysRun() {
        return daysRun;
    }

    public void setDaysRun(String daysRun) {
        this.daysRun = daysRun;
    }


}
