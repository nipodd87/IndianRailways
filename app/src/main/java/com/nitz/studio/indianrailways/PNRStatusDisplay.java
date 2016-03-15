package com.nitz.studio.indianrailways;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nitz.studio.indianrailways.model.PNRStatusModel;
import com.nitz.studio.indianrailways.parser.PNRParser;

import java.io.IOException;
import java.util.List;

/**
 * Created by nitinpoddar on 10/27/15.
 */
public class PNRStatusDisplay extends ActionBarActivity {

    private TextView trainNoTxt;
    private TextView trainNo;
    private TextView trainNameTxt;
    private TextView trainName;
    private TextView boardingDateTxt;
    private TextView boardingDate;
    private TextView fromTxt;
    private TextView from;
    private TextView toTxt;
    private TextView to;
    private TextView reservedUptoTxt;
    private TextView reservedUpto;
    private TextView boardingPointTxt;
    private TextView boardingPoint;
    public static TextView class1Txt;
    private TextView class1;
    private TextView journeyDetail;
    private TextView passengerInfo;
    private TextView chartPrepTxt;
    private int response_code;
    private String pnr;
    private String train_name;
    private String train_number;
    private String date_of_journey;
    private String url;
    private StringBuffer passengerDetail = new StringBuffer();
    private boolean isRefresh;
    private ListView pnrStatusList;
    public List<PNRStatusModel> pnrStatusModelList;
    private Toolbar toolbar;
    private int total_passenger;
    public PNRDBAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnrdisplay);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);

        trainNo = (TextView) findViewById(R.id.trainNo);
        trainName = (TextView) findViewById(R.id.trainName);
        boardingDate = (TextView) findViewById(R.id.boardingDate);
        from = (TextView) findViewById(R.id.from);
        to = (TextView) findViewById(R.id.to);
        reservedUpto = (TextView) findViewById(R.id.reservedUpto);
        boardingPoint = (TextView) findViewById(R.id.boardingPoint);
        class1 = (TextView) findViewById(R.id.class1);
        journeyDetail = (TextView) findViewById(R.id.journeyDetail);
        passengerInfo = (TextView) findViewById(R.id.passengerInfo);

        trainNoTxt = (TextView) findViewById(R.id.trainNoTxt);
        trainNameTxt = (TextView) findViewById(R.id.trainNameTxt);
        boardingDateTxt = (TextView) findViewById(R.id.boardingDateTxt);
        fromTxt = (TextView) findViewById(R.id.fromTxt);
        toTxt = (TextView) findViewById(R.id.toTxt);
        reservedUptoTxt = (TextView) findViewById(R.id.reservedUptoTxt);
        boardingPointTxt = (TextView) findViewById(R.id.boardingPointTxt);
        class1Txt = (TextView) findViewById(R.id.class1Txt);
        chartPrepTxt = (TextView) findViewById(R.id.chartPrepTxt);

        pnrStatusList = (ListView) findViewById(R.id.pnrStatusList);
        pnrStatusList.setVisibility(View.INVISIBLE);

        trainNo.setVisibility(View.INVISIBLE);
        trainName.setVisibility(View.INVISIBLE);
        boardingDate.setVisibility(View.INVISIBLE);
        from.setVisibility(View.INVISIBLE);
        to.setVisibility(View.INVISIBLE);
        reservedUpto.setVisibility(View.INVISIBLE);
        boardingPoint.setVisibility(View.INVISIBLE);
        journeyDetail.setVisibility(View.INVISIBLE);
        passengerInfo.setVisibility(View.INVISIBLE);
        chartPrepTxt.setVisibility(View.INVISIBLE);
        isRefresh = false;

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        pnr = intent.getStringExtra("pnr");

        MyTask task = new MyTask();
        task.execute(url);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_items, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
         if (id == R.id.action_share){
             if (response_code == 200 || response_code == 204) {
                 String message = "PNR: " + pnr + "\n" +
                         "Train: " + trainNoTxt.getText().toString() + "/" + trainNameTxt.getText().toString() + "\n" +
                         "Date: " + boardingDateTxt.getText().toString() + "\n" +
                         "Class: " + class1Txt.getText().toString() + "\n" +
                         "Passengers: " + "\n" +
                         passengerDetail.toString() + "\n" +
                         "Download from:" + "\n" +
                 "https://goo.gl/fTOjTJ";

                 Intent shareIntent = new Intent(Intent.ACTION_SEND);
                 shareIntent.setAction(Intent.ACTION_SEND);
                 shareIntent.setType("text/plain");
                 shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                 startActivity(shareIntent);
             } else{
                 Toast.makeText(this, "No data to share", Toast.LENGTH_SHORT).show();
             }
             return true;
         }
        if (id == R.id.action_refresh){
            isRefresh = true;
            MyTask refreshTask =  new MyTask();
            refreshTask.execute(url);
        }
        return super.onOptionsItemSelected(item);
    }
    public class MyTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            IndianRailwayInfo.showProgress(PNRStatusDisplay.this, "Loading", "Please Wait...");
            pnrStatusList.setVisibility(View.INVISIBLE);
            trainNoTxt.setVisibility(View.INVISIBLE);
            trainNameTxt.setVisibility(View.INVISIBLE);
            boardingDateTxt.setVisibility(View.INVISIBLE);
            fromTxt.setVisibility(View.INVISIBLE);
            toTxt.setVisibility(View.INVISIBLE);
            reservedUptoTxt.setVisibility(View.INVISIBLE);
            boardingPointTxt.setVisibility(View.INVISIBLE);
            class1Txt.setVisibility(View.INVISIBLE);
            journeyDetail.setVisibility(View.INVISIBLE);
            passengerInfo.setVisibility(View.INVISIBLE);

            trainNo.setVisibility(View.INVISIBLE);
            trainName.setVisibility(View.INVISIBLE);
            boardingDate.setVisibility(View.INVISIBLE);
            from.setVisibility(View.INVISIBLE);
            to.setVisibility(View.INVISIBLE);
            reservedUpto.setVisibility(View.INVISIBLE);
            boardingPoint.setVisibility(View.INVISIBLE);
            class1.setVisibility(View.INVISIBLE);
            chartPrepTxt.setVisibility(View.INVISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            String data = null;
            try {
                data = HttpManager.getData(params[0]);
                return data;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            IndianRailwayInfo.hideProgress();
            String jsonData = s;
            if (s == null){
                showErrorDialog("Error", "Unable to fetch response from server");
            } else {
                if (s.contains("Connection Error:")){
                    showErrorDialog("Error", s);
                }else {

                    pnrStatusModelList = PNRParser.parseFeed(jsonData);
                    response_code = PNRStatusModel.response_code;

                    if (PNRStatusModel.error == true) {
                        showErrorDialog("Error", "Invalid PNR or Server busy. Please try again later.");
                    } else if(PNRStatusModel.response_code > 400 ){
                        showErrorDialog("Server Error", "Server Unavailable. Please try after some time.");
                    }

                    else {
                        //check if value present, if not add it
                        adapter = new PNRDBAdapter(PNRStatusDisplay.this);
                        train_name = PNRStatusModel.train_name;
                        train_number = PNRStatusModel.train_num;
                        date_of_journey = PNRStatusModel.doj;

                        int count = adapter.getPNRCount(pnr, "_id");
                        if (count == 0){
                            adapter.insertPNR(pnr, train_number, train_name, date_of_journey);
                        }
                        //set Visibility of Text fields
                        setVisibleFields();
                        //Update Journey Details
                        updateJourneyDetails();
                        //Update Passengers Details
                        ArrayAdapter<PNRStatusModel> adapter = new PNRAdapter();
                        pnrStatusList.setAdapter(adapter);
                    }
                }
            }

        }
        public void showErrorDialog(String title, String message){
            new AlertDialog.Builder(PNRStatusDisplay.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            Intent intent = new Intent(PNRStatusDisplay.this, PNRStatus.class);
                            startActivity(intent);
                        }
                    })
                    .setIcon(R.drawable.icon_alert)
                    .show();
        }
        private void updateJourneyDetails() {
            total_passenger = PNRStatusModel.total_passengers;

            trainNoTxt.setText(PNRStatusModel.train_num);
            trainNameTxt.setText(PNRStatusModel.train_name);
            boardingDateTxt.setText(PNRStatusModel.doj);
            fromTxt.setText(PNRStatusModel.from_station_name);
            toTxt.setText(PNRStatusModel.to_station_name);
            reservedUptoTxt.setText(PNRStatusModel.reserved_upto_name);
            boardingPointTxt.setText(PNRStatusModel.boarding_point_name);
            class1Txt.setText(PNRStatusModel.class1);
            if (PNRStatusModel.chart_prepared.equals("Y")){
                chartPrepTxt.setText("CHART PREPARED");
            }else{
                chartPrepTxt.setText("CHART NOT PREPARED");
            }
        }
        private void setVisibleFields() {
            pnrStatusList.setVisibility(View.VISIBLE);
            //make details visible
            trainNoTxt.setVisibility(View.VISIBLE);
            trainNameTxt.setVisibility(View.VISIBLE);
            boardingDateTxt.setVisibility(View.VISIBLE);
            fromTxt.setVisibility(View.VISIBLE);
            toTxt.setVisibility(View.VISIBLE);
            reservedUptoTxt.setVisibility(View.VISIBLE);
            boardingPointTxt.setVisibility(View.VISIBLE);
            class1Txt.setVisibility(View.VISIBLE);

            trainNo.setVisibility(View.VISIBLE);
            trainName.setVisibility(View.VISIBLE);
            boardingDate.setVisibility(View.VISIBLE);
            from.setVisibility(View.VISIBLE);
            to.setVisibility(View.VISIBLE);
            reservedUpto.setVisibility(View.VISIBLE);
            boardingPoint.setVisibility(View.VISIBLE);
            class1.setVisibility(View.VISIBLE);
            journeyDetail.setVisibility(View.VISIBLE);
            passengerInfo.setVisibility(View.VISIBLE);
            chartPrepTxt.setVisibility(View.VISIBLE);
        }
    }
    public class PNRAdapter extends ArrayAdapter<PNRStatusModel>{
        public PNRAdapter() {
            super(PNRStatusDisplay.this, R.layout.itemview_pnr_status, pnrStatusModelList);
        }
        @Override
        public PNRStatusModel getItem (int pos){
            if (pnrStatusModelList != null)
                return pnrStatusModelList.get(pos);
            else
                return null;
        }

        @Override
        public int getCount()
        {   if (pnrStatusModelList != null)
                return pnrStatusModelList.size();
            else
                return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.itemview_pnr_status, parent, false);
            }
            TextView slNOTxt = (TextView) itemView.findViewById(R.id.slNOTxt);
            TextView bookingStatusTxt = (TextView) itemView.findViewById(R.id.bookingStatusTxt);
            TextView currentStatusTxt = (TextView) itemView.findViewById(R.id.currentStatusTxt);
            PNRStatusModel model = pnrStatusModelList.get(position);
            slNOTxt.setText("" + model.getSerialNo());
            bookingStatusTxt.setText(model.getBookingStatus());
            currentStatusTxt.setText(model.getCurrentStatus());

            if (total_passenger> 0 && !isRefresh) {
                passengerDetail.append("Passenger " + (position + 1) + ": ");
                passengerDetail.append(model.getCurrentStatus());
                passengerDetail.append("\n");
                total_passenger--;
            }
            return itemView;
        }
    }
}
