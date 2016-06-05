package com.nitz.studio.indianrailways;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by nitinpoddar on 10/26/15.
 */
public class IndianRailwayInfo extends Application{
    public static final String API_KEY = "csxwo2047";
    public static final String MIXPANEL_TOKEN = "a853af3ecf4658f679705d3bb446dd21";
    public static final String GCM_API_KEY = "AIzaSyA6G0qSZDoOnGhvyoQhodPFHLhh83Ic1DQ";
    public final static String APP_PNAME = "com.nitz.studio.indianrailways";
    public static ProgressDialog progressDialog;

    public static void showErrorDialog(String title, String message, Context mContext){
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.icon_alert)
                .show();
    }
    public static void showProgress(Context mContext, String title, String message){
        progressDialog = ProgressDialog.show(mContext, title, message, true);
    }
    public static  void hideProgress(){
        progressDialog.dismiss();
    }
}
