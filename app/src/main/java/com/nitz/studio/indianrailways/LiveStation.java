package com.nitz.studio.indianrailways;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nitz.studio.indianrailways.model.LiveStationModel;
import com.nitz.studio.indianrailways.model.SearchStationModel;
import com.nitz.studio.indianrailways.parser.LiveStationParser;
import com.nitz.studio.indianrailways.parser.SearchStationParser;

import java.io.IOException;
import java.util.ArrayList;
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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livestation);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);

        twoHourButton = (Button) findViewById(R.id.twoHourButton);
        fourHourButton = (Button) findViewById(R.id.fourHourButton);
        liveStationList = (ListView) findViewById(R.id.liveStationList);
        liveStationList.setVisibility(View.INVISIBLE);
        sourceTxt = (AutoCompleteTextView) findViewById(R.id.stationNameTxt);
        sourceTxt.setThreshold(2);
        sourceTxt.setAdapter(new StationSearchAdapter(this));
        sourceTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchStationModel searchStationModel = (SearchStationModel) parent.getItemAtPosition(position);
                sourceTxt.setText(searchStationModel.getStationName() + "-" + searchStationModel.getStationCode());
                mSourceStnCode = searchStationModel.getStationCode();
            }
        });
        sourceTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sourceTxt.setCursorVisible(true);
                return false;
            }
        });
    }

    public void onNextTwoHour(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        resultForClick("2");
    }

    public void onNextFourHour(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        resultForClick("4");
    }

    public void resultForClick(String hourClicked){

        if (stationNameLength()){
            if (isConnected()){
                String url = "http://api.railwayapi.com/arrivals/station/" +mSourceStnCode+
                        "/hours/"+hourClicked+
                        "/apikey/"+IndianRailwayInfo.API_KEY+"/";
                MyTask task = new MyTask();
                task.execute(url);

            } else{
                IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", LiveStation.this);
            }
        } else
        {
            Toast.makeText(this, "Please enter valid station name", Toast.LENGTH_SHORT).show();
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
                modelList = LiveStationParser.parseFeed(s);
                if ((LiveStationModel.responseCode > 200) && (LiveStationModel.responseCode < 400)  ) {
                    IndianRailwayInfo.showErrorDialog("Error", "Railway server responding slow or wrong parameters given", LiveStation.this);
                } else if (LiveStationModel.responseCode > 400) {
                    IndianRailwayInfo.showErrorDialog("Server Error", "Server is currently unavailable. Please try after some time", LiveStation.this);
                } else {
                    liveStationList.setVisibility(View.VISIBLE);
                    ArrayAdapter<LiveStationModel> adapter = new LiveStationAdapter();
                    liveStationList.setAdapter(adapter);
                }
            }
        }
    }

    public class LiveStationAdapter extends ArrayAdapter<LiveStationModel>{

        public LiveStationAdapter() {
            super(LiveStation.this, R.layout.itemview_live_station, modelList);
        }

        @Override
        public LiveStationModel getItem (int pos){
            if (modelList != null)
                return modelList.get(pos);
            else
                return null;
        }

        @Override
        public int getCount()
        {
            if (modelList != null)
                return modelList.size();
            else
                return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.itemview_live_station, parent, false);
            }
            TextView trainNo = (TextView) itemView.findViewById(R.id.trainNoTxt);
            TextView trainName = (TextView) itemView.findViewById(R.id.trainNameTxt);
            TextView schArr = (TextView) itemView.findViewById(R.id.schArrTxt);
            TextView schDept = (TextView) itemView.findViewById(R.id.schDeptTxt);
            TextView actArr = (TextView) itemView.findViewById(R.id.actArrTxt);
            TextView actDept = (TextView) itemView.findViewById(R.id.actDeptTxt);

            LiveStationModel model = modelList.get(position);

            trainNo.setText(model.getTrainNumber());
            trainName.setText(model.getTrainName());
            schArr.setText(model.getSchArrival());
            schDept.setText(model.getSchDept());
            actArr.setText(model.getActualArrival());
            actDept.setText(model.getActualDept());
            return itemView;
        }
    }
    public class StationSearchAdapter extends BaseAdapter implements Filterable {

        public List<SearchStationModel> resultList = new ArrayList<SearchStationModel>();
        private Context mContext;

        public StationSearchAdapter(Context context){
            mContext = context;
        }

        @Override
        public int getCount() {
            if (resultList != null)
                return resultList.size();
            else
                return 0;
        }

        @Override
        public SearchStationModel getItem(int position)
        {
            if (resultList != null)
                return resultList.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }
            SearchStationModel model = resultList.get(position);
            TextView stationName = (TextView)convertView.findViewById(android.R.id.text1);
            stationName.setText(model.getStationName()+"-"+model.getStationCode());
            stationName.setTextColor(Color.BLACK);
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if(constraint != null){
                        List<SearchStationModel> searchStationModels = findStationName(mContext, constraint.toString());
                        filterResults.values = searchStationModels;
                        filterResults.count = searchStationModels.size();
                    }
                    return filterResults;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count>0){
                        resultList = (List<SearchStationModel>) results.values;
                        notifyDataSetChanged();
                    } else{
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
        private List<SearchStationModel> findStationName(Context context, String stationCode){
            String stationUrl = "http://api.railwayapi.com/suggest_station/name/"+stationCode+"/apikey/"+
                    IndianRailwayInfo.API_KEY+"/";
            try {
                String content = HttpManager.getData(stationUrl);
                List<SearchStationModel> resultModel = SearchStationParser.parseFeed(content);
                return resultModel;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
