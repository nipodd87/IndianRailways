package com.nitz.studio.indianrailways.model;


/**
 * Created by nitinpoddar on 10/27/15.
 */
public class PNRStatusModel {

    public static int response_code;
    public static boolean error;
    public static String train_name;
    public static String train_num;
    public static String doj;
    public static String from_station_name;
    public static String chart_prepared;
    public static String to_station_name;
    public static String reserved_upto_name;
    public static String boarding_point_name;
    public static String class1;
    public static int total_passengers;

    private int serialNo;
    private String bookingStatus;
    private String currentStatus;

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

}
