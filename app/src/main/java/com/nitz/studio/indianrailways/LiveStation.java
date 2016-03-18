package com.nitz.studio.indianrailways;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.nitz.studio.indianrailways.model.LiveStationModel;

import java.io.IOException;
import java.util.List;

/**
 * Created by nitinpoddar on 3/17/16.
 */
public class LiveStation extends ActionBarActivity{

    private AutoCompleteTextView sourceTxt;
    private Button twoHourButton;
    private Button fourHourButton;
    public String mSourceStnCode = "";
    public ListView liveStationList;
    public List<LiveStationModel> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livestation);

        sourceTxt = (AutoCompleteTextView) findViewById(R.id.stationNameTxt);
        twoHourButton = (Button) findViewById(R.id.twoHourButton);
        fourHourButton = (Button) findViewById(R.id.fourHourButton);
        liveStationList = (ListView) findViewById(R.id.liveStationList);
        liveStationList.setVisibility(View.INVISIBLE);
    }

    public void onNextTwoHour(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (stationNameLength()){
            if (isConnected()){
                String url = "http://api.railwayapi.com/arrivals/station/" +mSourceStnCode+
                        "/hours/2"+
                        "/apikey/"+IndianRailwayInfo.API_KEY+"/";

            } else{
                IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", LiveStation.this);
            }
        } else
            {
                Toast.makeText(this, "Please enter valid station name", Toast.LENGTH_SHORT).show();
        }
    }
    public void onNextFourHour(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean stationNameLength(){
        if (sourceTxt.length() == 0)
            return false;
        else
            return true;
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }else {
            return false;
        }
    }
    public class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            IndianRailwayInfo.showProgress(LiveStation.this, "Loading", "Please wait this may take more than 30 seconds...");
            liveStationList.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String content = null;
            try {
                content = HttpManager.getData(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String s) {
            IndianRailwayInfo.hideProgress();
            if (s == null){
                IndianRailwayInfo.showErrorDialog("Error", "Unable to fetch response from server", LiveStation.this);
            } else if (s.contains("Connection Error:")){
                IndianRailwayInfo.showErrorDialog("Error", s, LiveStation.this);
            } else {
                modelList = SeatAvailParser.parseFeed(s);
                if ((SeatAvailModel.responseCode > 200) && (SeatAvailModel.responseCode < 400)  ) {
                    IndianRailwayInfo.showErrorDialog("Error", "Railway server responding slow or wrong parameters given", SeatAvail.this);
                } else if (SeatAvailModel.responseCode > 400) {
                    IndianRailwayInfo.showErrorDialog("Server Error", "Server is currently unavailable. Please try after some time", SeatAvail.this);
                } else {
                    nameTxt.setVisibility(View.VISIBLE);
                    seatAvalabilityList.setVisibility(View.VISIBLE);
                    nameTxt.setText(SeatAvailModel.trNo + " / " + SeatAvailModel.trName);

                    ArrayAdapter<SeatAvailModel> adapter = new SeatAvailAdapter();
                    seatAvalabilityList.setAdapter(adapter);
                }
            }
        }
    }
}
