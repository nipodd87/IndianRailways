package com.nitz.studio.indianrailways;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nitz.studio.indianrailways.model.SearchStationModel;
import com.nitz.studio.indianrailways.model.TrainModel;
import com.nitz.studio.indianrailways.parser.SearchStationParser;
import com.nitz.studio.indianrailways.parser.SearchTrainParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nitinpoddar on 11/2/15.
 */
public class SearchTrain extends ActionBarActivity {

    private AutoCompleteTextView sourceTxt;
    private AutoCompleteTextView destinationTxt;
    private EditText dateipTxt;

    private TextView availableTrainTxt;
    private TextView totalTrain;
    private TextView totalTrainTxt;
    private ListView listView2;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("ddMM");
    private Calendar myCalendar = Calendar.getInstance();
    private Toolbar toolbar;
    public List<TrainModel> trainModelList;
    public String mSourceStnCode = "";
    public String mDestinationStnCode = "";

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Calendar currentDate = Calendar.getInstance();
            boolean isAfterToday = myCalendar.after(currentDate);
            if (isAfterToday){
                updateLabel();
            } else{
                IndianRailwayInfo.showErrorDialog("Invalid Date", "Travel date cannot be in past", SearchTrain.this);
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchtrain);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sourceTxt = (AutoCompleteTextView) findViewById(R.id.sourceTxt);
        destinationTxt = (AutoCompleteTextView) findViewById(R.id.destinationTxt);
        sourceTxt.setThreshold(2);
        destinationTxt.setThreshold(2);
        sourceTxt.setAdapter(new StationSearchAdapter(this));
        destinationTxt.setAdapter(new StationSearchAdapter(this));
        sourceTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchStationModel searchStationModel = (SearchStationModel) parent.getItemAtPosition(position);
                sourceTxt.setText(searchStationModel.getStationName() + "-"+searchStationModel.getStationCode());
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
        destinationTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchStationModel searchStationModel = (SearchStationModel) parent.getItemAtPosition(position);
                destinationTxt.setText(searchStationModel.getStationName() + "-"+searchStationModel.getStationCode());
                mDestinationStnCode = searchStationModel.getStationCode();
            }
        });
        destinationTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                destinationTxt.setCursorVisible(true);
                return false;
            }
        });
        dateipTxt = (EditText) findViewById(R.id.dateipTxt);
        updateLabel();
        dateipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SearchTrain.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        availableTrainTxt = (TextView) findViewById(R.id.availableTrainTxt);
        availableTrainTxt.setVisibility(View.INVISIBLE);

        totalTrain = (TextView) findViewById(R.id.totalTrain);
        totalTrain.setVisibility(View.INVISIBLE);

        totalTrainTxt = (TextView) findViewById(R.id.totalTrainTxt);
        totalTrainTxt.setVisibility(View.INVISIBLE);

        listView2 = (ListView) findViewById(R.id.listView2);
        listView2.setVisibility(View.INVISIBLE);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void getTrainList(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        availableTrainTxt.setVisibility(View.INVISIBLE);
        totalTrain.setVisibility(View.INVISIBLE);
        totalTrainTxt.setVisibility(View.INVISIBLE);
        listView2.setVisibility(View.INVISIBLE);

        String tempDate = dateipTxt.getText().toString();
        String selectedDate = tempDate.substring(0,2) +"-"+ tempDate.substring(3,5);

        String url = "http://api.railwayapi.com/between/source/" + mSourceStnCode + "/dest/" + mDestinationStnCode
                + "/date/"+selectedDate+ "/apikey/" + IndianRailwayInfo.API_KEY + "/";

        if ((mSourceStnCode.length() == 0) || (mDestinationStnCode.length() == 0)) {
            Toast.makeText(this, "Please enter valid Station Codes", Toast.LENGTH_SHORT).show();
        } else if(isConnected()){
            MyTask task = new MyTask();
            task.execute(url);
        }
        else {
            IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", SearchTrain.this);
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

    protected void updateLabel(){

        dateipTxt.setText(sdf.format(myCalendar.getTime()));
    }
    public class MyTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            IndianRailwayInfo.showProgress(SearchTrain.this, "Loading", "Please Wait...");
            availableTrainTxt.setVisibility(View.INVISIBLE);
            totalTrain.setVisibility(View.INVISIBLE);
            totalTrainTxt.setVisibility(View.INVISIBLE);
            listView2.setVisibility(View.INVISIBLE);
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
            IndianRailwayInfo.hideProgress();

            if (s == null){
                IndianRailwayInfo.showErrorDialog("Server Error", "Unable to fetch response from server", SearchTrain.this);
                return;
            } else {
                trainModelList = SearchTrainParser.parseFeed(s);
                if (TrainModel.responseCode > 200 && TrainModel.responseCode < 400 ){
                    IndianRailwayInfo.showErrorDialog("Error", "No Train runs between the given station codes on the selected date", SearchTrain.this);
                } else {
                    availableTrainTxt.setVisibility(View.VISIBLE);
                    totalTrain.setVisibility(View.VISIBLE);
                    totalTrainTxt.setVisibility(View.VISIBLE);
                    listView2.setVisibility(View.VISIBLE);

                    totalTrainTxt.setText("" + TrainModel.totalTrain);
                    ArrayAdapter<TrainModel> adapter = new TrainModelAdapter();
                    listView2.setAdapter(adapter);
                }
            }
        }
        public class TrainModelAdapter extends ArrayAdapter<TrainModel> {

            public TrainModelAdapter() {
                super(SearchTrain.this, R.layout.itemview_train_search, trainModelList);
            }
            @Override
            public TrainModel getItem (int pos){
                if (trainModelList != null)
                    return trainModelList.get(pos);
                else
                    return null;
            }
            @Override
            public int getCount()
            {
                if (trainModelList != null)
                    return trainModelList.size();
                else
                    return 0;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = convertView;
                if (itemView == null) {
                    itemView = getLayoutInflater().inflate(R.layout.itemview_train_search, parent, false);
                }

                TextView trainNumber = (TextView) itemView.findViewById(R.id.trainNumberTxt);
                TextView trainName = (TextView) itemView.findViewById(R.id.nameTxt);
                TextView deptTime = (TextView) itemView.findViewById(R.id.dept);
                TextView arrivesTime = (TextView) itemView.findViewById(R.id.arrives);
                TextView runDays = (TextView) itemView.findViewById(R.id.runsOn);

                TrainModel trainModel = trainModelList.get(position);

                trainNumber.setText(trainModel.getTrainNumber());
                trainName.setText(trainModel.getTrainName());
                deptTime.setText(trainModel.getDeptTime());
                arrivesTime.setText(trainModel.getArrivalTime());
                runDays.setText(trainModel.getDaysRun());

                return itemView;
            }
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
        {   if (resultList != null)
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
            stationName.setText(model.getStationName() + "-" + model.getStationCode());
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