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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nitz.studio.indianrailways.model.FareTrainModel;
import com.nitz.studio.indianrailways.model.SearchStationModel;
import com.nitz.studio.indianrailways.parser.FareTrainParser;
import com.nitz.studio.indianrailways.parser.SearchStationParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nitinpoddar on 11/4/15.
 */
public class FareEnquiry extends ActionBarActivity {

    private AutoCompleteTextView sourceTxt;
    private AutoCompleteTextView destinationTxt;
    private EditText dateipTxt;
    private ListView fareList;
    private EditText trainNoTxt;
    private TextView nameTxt;
    private CheckBox tatkalCheckBox;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
    private Toolbar toolbar;
    private Calendar myCalendar = Calendar.getInstance();
    public List<FareTrainModel> fareTrainModelList;
    public String mSourceStnCode = "";
    public String mDestinationCode = "";
    public String isTatkal;

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
            if (isAfterToday) {
                updateLabel();
            } else {
                IndianRailwayInfo.showErrorDialog("Invalid Date", "Travel date cannot be in past", FareEnquiry.this);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fareenquiry);
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
        fareList = (ListView) findViewById(R.id.fareList);

        nameTxt.setVisibility(View.INVISIBLE);
        isTatkal = "0";
        tatkalCheckBox = (CheckBox) findViewById(R.id.tatkalCheckBox);
        tatkalCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tatkalCheckBox.isChecked())
                    isTatkal = "PT";
                else
                    isTatkal = "0";
            }
        });
        dateipTxt.setText(sdf.format(myCalendar.getTime()));

        dateipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(FareEnquiry.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void getFare(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        String dateSelected = sdf2.format(myCalendar.getTime());

        if ((mSourceStnCode.length() == 0) || (mDestinationCode.length() == 0)) {
            Toast.makeText(this, "Please enter valid Station Codes", Toast.LENGTH_SHORT).show();
        } else if (trainNoTxt.getText().toString().length() != 5) {
            Toast.makeText(this, "Please enter valid 5 digit train no.", Toast.LENGTH_SHORT).show();
        } else if (isConnected()) {
            String url = "http://api.railwayapi.com/fare/train/" +
                    trainNoTxt.getText().toString() + "/source/" + mSourceStnCode + "/dest/" +
                    mDestinationCode + "/age/18/quota/" + isTatkal + "/doj/" + dateSelected +
                    "/apikey/" + IndianRailwayInfo.API_KEY + "/";
            MyTask task = new MyTask();
            task.execute(url);
        } else {
            IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", FareEnquiry.this);
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void updateLabel() {
        dateipTxt.setText(sdf.format(myCalendar.getTime()));
    }

    public class MyTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            IndianRailwayInfo.showProgress(FareEnquiry.this, "Loading", "Please wait this may take more than 30 seconds...");
            fareList.setVisibility(View.INVISIBLE);
            nameTxt.setVisibility(View.INVISIBLE);
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
            fareTrainModelList = null;
            if (s == null) {
                IndianRailwayInfo.showErrorDialog("Server Error", "Unable to fetch response from server", FareEnquiry.this);
            } else if (s.contains("Connection Error:")){
                IndianRailwayInfo.showErrorDialog("Error", s, FareEnquiry.this);
            }
            else {
                fareTrainModelList = FareTrainParser.parseFeed(s);
                if (FareTrainModel.responseCode > 200 && FareTrainModel.responseCode < 400) {
                    IndianRailwayInfo.showErrorDialog("Error", "Train does not seem to run on selected date or stations.", FareEnquiry.this);
                } else if (FareTrainModel.responseCode > 400) {
                    IndianRailwayInfo.showErrorDialog("Server Error", "Server is currently unavailable. Please try after some time", FareEnquiry.this);
                } else {
                    fareList.setVisibility(View.VISIBLE);
                    nameTxt.setVisibility(View.VISIBLE);

                    String TrainNameNo = FareTrainModel.trainNo + " / " + FareTrainModel.trainName;

                    ArrayAdapter<FareTrainModel> adapter = new FareTrainAdapter();
                    fareList.setAdapter(adapter);
                }
            }
        }
        public class FareTrainAdapter extends ArrayAdapter<FareTrainModel> {
            public FareTrainAdapter() {
                super(FareEnquiry.this, R.layout.itemview_train_fare, fareTrainModelList);
            }
            @Override
            public FareTrainModel getItem (int pos){
                if (fareTrainModelList != null)
                    return fareTrainModelList.get(pos);
                else
                    return null;
            }
            @Override
            public int getCount()
            {   if (fareTrainModelList != null)
                    return fareTrainModelList.size();
                else
                    return 0;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = convertView;
                if (itemView == null) {
                    itemView = getLayoutInflater().inflate(R.layout.itemview_train_fare, parent, false);
                }
                TextView classCode = (TextView) itemView.findViewById(R.id.classCode);
                TextView className = (TextView) itemView.findViewById(R.id.className);
                TextView farePrice = (TextView) itemView.findViewById(R.id.farePrice);
                TextView trainName = (TextView) itemView.findViewById(R.id.trainName);
                TextView trainNumber = (TextView) itemView.findViewById(R.id.trainNumber);

                FareTrainModel model = fareTrainModelList.get(position);

                classCode.setText(model.getClassCode());
                className.setText(model.getClassName());
                farePrice.setText("INR"+"\n"+model.getClassFare());
                trainName.setText(FareTrainModel.trainName);
                trainNumber.setText(FareTrainModel.trainNo);

                return itemView;
            }
        }
    }
    public class StationSearchAdapter extends BaseAdapter implements Filterable {

        public List<SearchStationModel> resultList = new ArrayList<SearchStationModel>();
        private Context mContext;

        public StationSearchAdapter(Context context) {
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
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }
            SearchStationModel model = resultList.get(position);
            TextView stationName = (TextView) convertView.findViewById(android.R.id.text1);
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
                    if (constraint != null) {
                        List<SearchStationModel> searchStationModels = findStationName(mContext, constraint.toString());
                        filterResults.values = searchStationModels;
                        filterResults.count = searchStationModels.size();
                    }
                    return filterResults;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        resultList = (List<SearchStationModel>) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
        private List<SearchStationModel> findStationName(Context context, String stationCode) {
            String stationUrl = "http://api.railwayapi.com/suggest_station/name/" + stationCode + "/apikey/" +
                    IndianRailwayInfo.API_KEY + "/";
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