package com.nitz.studio.indianrailways;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nitz.studio.indianrailways.model.TrainStatus;
import com.nitz.studio.indianrailways.parser.TrainStatusParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by nitinpoddar on 10/28/15.
 */
public class TrainLiveStatus extends ActionBarActivity {

    private EditText tneditTxt;
    private Button yesterdayBtn;
    private Button todayBtn;
    private Button tomorrowBtn;
    private TextView trainLiveStatusTxt;
    private TextView currentPosition;
    private Calendar todayCalendar;
    private Calendar yesterdayCalendar;
    private Calendar tomorrowCalendar;
    private Toolbar toolbar;
    private ListView statusListView;
    public List<TrainStatus> trainList;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainlivestatus);
        getWindow().setBackgroundDrawable(null);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tneditTxt = (EditText) findViewById(R.id.tneditTxt);
        tneditTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tneditTxt.setCursorVisible(true);
                return false;
            }
        });
        currentPosition = (TextView) findViewById(R.id.currentPosition);
        currentPosition.setVisibility(View.INVISIBLE);
        currentPosition.setSelected(true);
        currentPosition.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        currentPosition.setSingleLine(true);
        yesterdayBtn = (Button) findViewById(R.id.yesterdayBtn);
        todayBtn = (Button) findViewById(R.id.todayBtn);
        tomorrowBtn = (Button) findViewById(R.id.tomorrowBtn);
        statusListView = (ListView) findViewById(R.id.statusListView);
        trainLiveStatusTxt = (TextView) findViewById(R.id.trainLiveStatusTxt);
        trainLiveStatusTxt.setVisibility(View.INVISIBLE);
        getCurrentDate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void getLiveStatus(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        int id = view.getId();
        String dateSelected = determineDateSelected(id);
        String trainNo = tneditTxt.getText().toString();

        if (trainNo.length() != 5) {
            tneditTxt.setText("");
            Toast.makeText(this, "Please enter valid 5 digit Train no.", Toast.LENGTH_SHORT).show();
        } else {
            if (isConnected()){
                String url = "http://api.railwayapi.com/live/train/" + trainNo + "/doj/" + dateSelected + "/apikey/" +
                        IndianRailwayInfo.API_KEY + "/";
                MyTask task = new MyTask();
                task.execute(url);
            }else{
                IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", TrainLiveStatus.this);
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
    public String determineDateSelected(int id){
        Calendar cal1 = Calendar.getInstance();
        if (id == R.id.yesterdayBtn){
            cal1.add(Calendar.DATE,-1);
            return simpleDateFormat.format(cal1.getTime());
        }else if(id == R.id.todayBtn){
            return simpleDateFormat.format(cal1.getTime());
        }else{
            cal1.add(Calendar.DATE,1);
            return simpleDateFormat.format(cal1.getTime());
        }
    }
    public void getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        todayCalendar = Calendar.getInstance();
        String today = sdf.format(todayCalendar.getTime());

        yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.add(Calendar.DATE, -1);
        String yesterday = sdf.format(yesterdayCalendar.getTime());

        tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.add(Calendar.DATE, 1);
        String tomorrow = sdf.format(tomorrowCalendar.getTime());

        yesterdayBtn.setText(yesterday);
        todayBtn.setText(today);
        tomorrowBtn.setText(tomorrow);
    }

    public class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            IndianRailwayInfo.showProgress(TrainLiveStatus.this, "Loading", "Please Wait...");
            trainLiveStatusTxt.setVisibility(View.INVISIBLE);
            statusListView.setVisibility(View.INVISIBLE);
            currentPosition.setVisibility(View.INVISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String content = HttpManager.getData(params[0]);
                if (content == null){
                    return null;
                }
                return content;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            IndianRailwayInfo.hideProgress();
            trainList = TrainStatusParser.parseFeed(s);

            if (s == null) {
                IndianRailwayInfo.showErrorDialog("Server Error", "Server Unavailable. Please try after some time.", TrainLiveStatus.this);
            } else if (s.contains("Connection Error:")) {
                IndianRailwayInfo.showErrorDialog("Error", s, TrainLiveStatus.this);
            }
            else {
            trainList = TrainStatusParser.parseFeed(s);
            if (TrainStatus.responseCode > 200 && TrainStatus.responseCode <= 400) {
                IndianRailwayInfo.showErrorDialog("Error", "Invalid Train number or date.", TrainLiveStatus.this);
            }
            else if(TrainStatus.responseCode > 400 ){
                IndianRailwayInfo.showErrorDialog("Server Error", "Server Unavailable. Please try after some time.", TrainLiveStatus.this);
            }
            else
            {
            statusListView.setVisibility(View.VISIBLE);
            trainLiveStatusTxt.setVisibility(View.VISIBLE);
            currentPosition.setVisibility(View.VISIBLE);
            currentPosition.setText(TrainStatus.position);
                ArrayAdapter<TrainStatus> adapter = new MyTrainStatusAdapter();
            statusListView.setAdapter(adapter);
                }
            }
        }
    }

    private class MyTrainStatusAdapter extends ArrayAdapter<TrainStatus> {

        public TrainStatus trainStatus;
        public TextView statusLate;
        public TextView statusOntime;

        public MyTrainStatusAdapter() {
            super(TrainLiveStatus.this, R.layout.itemview_train_status, trainList);
        }

        @Override
        public TrainStatus getItem(int pos) {
            if (trainList != null)
                return trainList.get(pos);
            else
                return null;
        }

        @Override
        public int getCount() {
            if (trainList != null)
                return trainList.size();
            else
                return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.itemview_train_status, parent, false);
            }
            //find the item for each row
            TextView stationName = (TextView) itemView.findViewById(R.id.item_station);
            TextView scheduleArrival = (TextView) itemView.findViewById(R.id.item_schedule);
            TextView actualArrival = (TextView) itemView.findViewById(R.id.item_actual);
            TextView item_hasArrivedTxt = (TextView) itemView.findViewById(R.id.item_hasArrivedTxt);
            TextView item_hasDepartedTxt = (TextView) itemView.findViewById(R.id.item_hasDepartedTxt);
            statusLate = (TextView) itemView.findViewById(R.id.status_late);
            trainStatus = trainList.get(position);

            //fill each item
            stationName.setText(trainStatus.getStationName());
            boolean hasArrived = trainStatus.isHasArrived();
            boolean hasDeparted = trainStatus.isHasDeparted();

            if (hasArrived) {
                item_hasArrivedTxt.setText("Y");
            } else {
                item_hasArrivedTxt.setText("N");
                actualArrival.setText("N/A");
            }

            if (hasDeparted) {
                item_hasDepartedTxt.setText("Y");
            } else {
                item_hasDepartedTxt.setText("N");
            }
            scheduleArrival.setText(trainStatus.getScheduleArrival());
            actualArrival.setText(trainStatus.getActualArrival());

            if (trainStatus.getStatusOfArrival().contains("-") || trainStatus.getScheduleArrival().equals("Source")){
                String line1 = trainStatus.getStatusOfArrival().substring(0, trainStatus.getStatusOfArrival().indexOf("l"));
                String line2 = trainStatus.getStatusOfArrival().substring(trainStatus.getStatusOfArrival().indexOf("l"));
                statusLate.setText(line1 +"\n"+line2);
                statusLate.setBackgroundResource(R.drawable.textview_ontime);
            } else if (!trainStatus.isHasArrived()){
                  statusLate.setText("Not\nArrived");
                  statusLate.setBackgroundResource(R.drawable.textview_ontime);
            } else{
                String line1 = trainStatus.getStatusOfArrival().substring(0, trainStatus.getStatusOfArrival().indexOf("l"));
                String line2 = trainStatus.getStatusOfArrival().substring(trainStatus.getStatusOfArrival().indexOf("l"));
                statusLate.setText(line1 +"\n"+line2);
                statusLate.setBackgroundResource(R.drawable.textview_late);
            }
            return itemView;
        }
    }
}
