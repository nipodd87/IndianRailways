package com.nitz.studio.indianrailways;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.nitz.studio.indianrailways.model.SearchStationModel;
import com.nitz.studio.indianrailways.model.SeatAvailModel;
import com.nitz.studio.indianrailways.parser.SearchStationParser;
import com.nitz.studio.indianrailways.parser.SeatAvailParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nitinpoddar on 11/6/15.
 */
public class SeatAvail extends ActionBarActivity implements AdapterView.OnItemSelectedListener{

    private AutoCompleteTextView sourceTxt;
    private AutoCompleteTextView destinationTxt;
    private EditText dateipTxt;
    private ListView seatAvalabilityList;
    private EditText trainNoTxt;
    private Spinner travelClass;
    private TextView nameTxt;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
    private Calendar myCalendar = Calendar.getInstance();
    private String [] classArray;
    private String [] classArrayRes = {"1A", "2A", "3A", "SL", "FC", "CC"};
    private String classSelected;
    private Toolbar toolbar;
    public List<SeatAvailModel> modelList;
    public String mSourceStnCode = "";
    public String mDestinationCode = "";

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
                IndianRailwayInfo.showErrorDialog("Invalid Date", "Travel date cannot be in past", SeatAvail.this);
            }
        }
    };
    public void updateLabel(){

        dateipTxt.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_avail);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);
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
        destinationTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchStationModel searchStationModel = (SearchStationModel) parent.getItemAtPosition(position);
                destinationTxt.setText(searchStationModel.getStationName() + "-" + searchStationModel.getStationCode());
                mDestinationCode = searchStationModel.getStationCode();
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
        trainNoTxt = (EditText) findViewById(R.id.trainNoTxt);
        trainNoTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                trainNoTxt.setCursorVisible(true);
                return false;
            }
        });

        nameTxt = (TextView) findViewById(R.id.nameTxt);
        nameTxt.setVisibility(View.INVISIBLE);

        seatAvalabilityList = (ListView) findViewById(R.id.seatList);
        seatAvalabilityList.setVisibility(View.INVISIBLE);

        dateipTxt.setText(sdf.format(myCalendar.getTime()));
        dateipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SeatAvail.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        classArray = getResources().getStringArray(R.array.travel_class);

        // Spinner element
        travelClass = (Spinner) findViewById(R.id.travelClass);

        // Spinner click listener
        travelClass.setOnItemSelectedListener(this);

        //Set Layout Params

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_dropdown, classArray);
        // attaching data adapter to spinner
        travelClass.setAdapter(dataAdapter);
    }

    public void getFare(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        String dateSelected = sdf2.format(myCalendar.getTime());

        String url = "http://api.railwayapi.com/check_seat/train/" +
                trainNoTxt.getText().toString() + "/source/"+mSourceStnCode+
                "/dest/"+mDestinationCode+"/date/"+dateSelected+
                "/class/"+classSelected+"/quota/GN/apikey/"+IndianRailwayInfo.API_KEY+"/";

        if ((mSourceStnCode.length() == 0) || (mDestinationCode.length() == 0)) {
            Toast.makeText(this, "Please enter valid Station Codes", Toast.LENGTH_SHORT).show();
        } else if (trainNoTxt.getText().toString().length() != 5) {
            Toast.makeText(this, "Please enter valid 5 digit Train no.", Toast.LENGTH_SHORT).show();
        }
        else if (isConnected()){

            MyTask task = new MyTask();
            task.execute(url);
        }
        else {
            IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", SeatAvail.this);
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
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int sid=travelClass.getSelectedItemPosition();
        classSelected = classArrayRes[sid];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        classSelected = classArrayRes[0];
    }

    public class MyTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            IndianRailwayInfo.showProgress(SeatAvail.this, "Loading", "Please wait this may take more than 30 seconds...");
            nameTxt.setVisibility(View.INVISIBLE);
            seatAvalabilityList.setVisibility(View.INVISIBLE);
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
                IndianRailwayInfo.showErrorDialog("Error", "Unable to fetch response from server", SeatAvail.this);
            } else if (s.contains("Connection Error:")){
                IndianRailwayInfo.showErrorDialog("Error", s, SeatAvail.this);
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
    public class SeatAvailAdapter extends ArrayAdapter<SeatAvailModel>{

        public SeatAvailAdapter() {
            super(SeatAvail.this, R.layout.itemview_seat_avail, modelList);
        }

        @Override
        public SeatAvailModel getItem (int pos){
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
                itemView = getLayoutInflater().inflate(R.layout.itemview_seat_avail, parent, false);
            }
            TextView travelDateTxt = (TextView) itemView.findViewById(R.id.travelDateTxt);
            TextView trainClassCodeTxt = (TextView) itemView.findViewById(R.id.trainClassCodeTxt);
            TextView travelStatusTxt = (TextView) itemView.findViewById(R.id.travelStatusTxt);

            SeatAvailModel model = modelList.get(position);

            travelDateTxt.setText(model.getTravelDate());
            travelStatusTxt.setText(model.getStatus());
            trainClassCodeTxt.setText(SeatAvailModel.classCode);

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
