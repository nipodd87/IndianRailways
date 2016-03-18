package com.nitz.studio.indianrailways.model;

/**
 * Created by nitinpoddar on 3/18/16.
 */
public class LiveStationModel {

    private String trainName;
    private String trainNumber;
    private String schArrival;
    private String schDept;
    private String actualArrival;
    private String actualDept;
    public static int responseCode;
    public static int totalTrain;

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getSchArrival() {
        return schArrival;
    }

    public void setSchArrival(String schArrival) {
        this.schArrival = schArrival;
    }

    public String getSchDept() {
        return schDept;
    }

    public void setSchDept(String schDept) {
        this.schDept = schDept;
    }

    public String getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(String actualArrival) {
        this.actualArrival = actualArrival;
    }

    public String getActualDept() {
        return actualDept;
    }

    public void setActualDept(String actualDept) {
        this.actualDept = actualDept;
    }



}
