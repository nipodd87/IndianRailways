package com.nitz.studio.indianrailways.model;

/**
 * Created by nitinpoddar on 11/10/15.
 */
public class SearchStationModel {
    private String stationName;
    private String stationCode;
    private String nameCode;
    public static int responseCode;
    public static int total;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }
}
