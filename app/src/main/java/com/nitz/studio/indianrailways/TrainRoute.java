package com.nitz.studio.indianrailways;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nitz.studio.indianrailways.model.TrainRouteStn;
import com.nitz.studio.indianrailways.parser.TrainRouteParser;
import java.io.IOException;
import java.util.List;

/**
 * Created by nitinpoddar on 10/31/15.
 */
public class TrainRoute extends ActionBarActivity {

    private ListView trainRoutList;
    private EditText trTxt;
    private TextView tr_name;
    private TextView dayRun;
    private Toolbar toolbar;
    public List<TrainRouteStn> trainRouteStnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainroute);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trainRoutList = (ListView) findViewById(R.id.trainRoutList);
        trTxt = (EditText) findViewById(R.id.trTxt);
        trTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                trTxt.setCursorVisible(true);
                return false;
            }
        });
        trainRoutList = (ListView) findViewById(R.id.trainRoutList);

        tr_name = (TextView) findViewById(R.id.tr_name);
        dayRun = (TextView) findViewById(R.id.dayRun);

        tr_name.setVisibility(View.INVISIBLE);
        dayRun.setVisibility(View.INVISIBLE);
        trainRoutList.setVisibility(View.INVISIBLE);

        trainRoutList = (ListView) findViewById(R.id.trainRoutList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void getTrainRoute(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        String trainNo = trTxt.getText().toString();
        if (trainNo.length()!=5){
            Toast.makeText(this, "Please enter valid 5 digit Train no.", Toast.LENGTH_SHORT).show();
        } else {
                if (isConnected()){
                    String url = "https://api.railwayapi.com/route/train/"+trainNo+"/apikey/"+IndianRailwayInfo.API_KEY+"/";
                    MyTask task = new MyTask();
                    task.execute(url);
                }else{
                    IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", TrainRoute.this);
                }
        }
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
            super.onPreExecute();
            IndianRailwayInfo.showProgress(TrainRoute.this, "Loading", "Please Wait...");
            tr_name.setVisibility(View.INVISIBLE);
            dayRun.setVisibility(View.INVISIBLE);
            trainRoutList.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String content = HttpManager.getData(params[0]);
                return content;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            IndianRailwayInfo.hideProgress();

            if (s == null) {
                IndianRailwayInfo.showErrorDialog("Server Error", "Unable to fetch response from server", TrainRoute.this);
                return;
            }
            else if (s.contains("Connection Error:")){
                IndianRailwayInfo.showErrorDialog("Error", s, TrainRoute.this);
                return;
            }
            else {
                trainRouteStnList = TrainRouteParser.parseFeed(s);

                if (TrainRouteStn.responseCode > 200 && TrainRouteStn.responseCode < 400) {
                    IndianRailwayInfo.showErrorDialog("Error", "Invalid Train Number", TrainRoute.this);
                    return;
                } else if (TrainRouteStn.responseCode > 400)
                {
                    IndianRailwayInfo.showErrorDialog("Server Error", "Server Unavailable. Please try after some time.", TrainRoute.this);
                    return;
                }
                if (trainRouteStnList != null) {
                    setVisibileFields();
                    ArrayAdapter<TrainRouteStn> adapter = new TrainRouteAdapter();
                    trainRoutList.setAdapter(adapter);
                }
            }
        }

        private void setVisibileFields() {

            tr_name.setVisibility(View.VISIBLE);
            dayRun.setVisibility(View.VISIBLE);
            trainRoutList.setVisibility(View.VISIBLE);

            tr_name.setTextColor(Color.WHITE);
            tr_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            tr_name.setText("Train Name: "+TrainRouteStn.trainName);


            dayRun.setTextColor(Color.WHITE);
            dayRun.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            dayRun.setText("Runs Every: "+TrainRouteStn.days);

        }
    }

    public class TrainRouteAdapter extends ArrayAdapter<TrainRouteStn>{

        public TrainRouteAdapter() {
            super(TrainRoute.this, R.layout.itemview_train_route, trainRouteStnList);
        }

        @Override
        public TrainRouteStn getItem (int pos){
            if (trainRouteStnList != null)
                return trainRouteStnList.get(pos);
            else
                return null;
        }

        @Override
        public int getCount()
        {
            if (trainRouteStnList != null)
                return trainRouteStnList.size();
            else
                return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.itemview_train_route, parent, false);
            }
            TextView stationName = (TextView) itemView.findViewById(R.id.stationNameTxt);
            TextView arrivesAt = (TextView) itemView.findViewById(R.id.arrivesAtTxt);
            TextView distance = (TextView) itemView.findViewById(R.id.distanceTxt);
            TextView day = (TextView) itemView.findViewById(R.id.dayTxt);

            TrainRouteStn trainRouteStn = trainRouteStnList.get(position);
            stationName.setText(trainRouteStn.getStationName());
            arrivesAt.setText(trainRouteStn.getScheduleArrival());
            distance.setText(""+trainRouteStn.getDistance());
            day.setText(""+trainRouteStn.getDay());
            return itemView;
            }
        }
    }

