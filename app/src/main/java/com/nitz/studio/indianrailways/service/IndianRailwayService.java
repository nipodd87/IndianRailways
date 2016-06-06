package com.nitz.studio.indianrailways.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by poddarn on 6/5/16.
 */
public class IndianRailwayService extends FirebaseInstanceIdService {

    private static final String TAG = "com.nitz.studio";

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed Token: "+refreshToken);
    }

    private void sendRegistrationToServer(String token){

    }
}
