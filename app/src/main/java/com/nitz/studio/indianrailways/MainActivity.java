package com.nitz.studio.indianrailways;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nitz.studio.indianrailways.model.MainListModel;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ListView listView;
    private ImageView app_icon;
    public int [] imgResource={R.drawable.icon_train_pnr, R.drawable.icon_train_status, R.drawable.icon_train_route,
            R.drawable.icon_search_train, R.drawable.icon_train_fare, R.drawable.icon_seat_avail, R.drawable.icon_train_irctc,
            R.drawable.icon_train_cancelled, R.drawable.icon_train_reschedule, R.drawable.icon_train_diverted, R.drawable.icon_rate_us, R.drawable.icon_share_app};

    public String [] mainListItem;
    public List<MainListModel> mainListModelList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.listView);
        listView.setBackgroundColor(Color.TRANSPARENT);
        Resources res = getResources();
        mainListItem = res.getStringArray(R.array.status_name);
        mainListModelList = new ArrayList<>();
        int i=0;
        for (int imgR: imgResource){
            MainListModel model = new MainListModel();
            model.setImgResource(imgR);
            model.setMenuItem(mainListItem[i]);
            i++;
            mainListModelList.add(model);
        }
        listView.setAdapter(new CustomAdapter());
        listView.setOnItemClickListener(new CustomAdapter());
    }
    public class CustomAdapter extends ArrayAdapter<MainListModel> implements AdapterView.OnItemClickListener {

        public CustomAdapter() {
            super(MainActivity.this, R.layout.activity_main_list, mainListModelList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.activity_main_list, parent, false);
            }

            ImageView imgRes = (ImageView) itemView.findViewById(R.id.imageView);
            TextView mainListItem = (TextView) itemView.findViewById(R.id.mainListItem);

            MainListModel model = mainListModelList.get(position);

            imgRes.setImageResource(model.getImgResource());
            mainListItem.setText(model.getMenuItem());

            return itemView;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selected = mainListItem[position];
            takeAction(selected);
        }
        private void takeAction(String selection) {
            switch (selection){
                case "PNR Status":
                    Intent intent = new Intent(MainActivity.this, PNRStatus.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.animation_slide, R.anim.animation_slide_back);
                    break;
                case "Train Live Status":
                    Intent liveStatusIntent = new Intent(MainActivity.this, TrainLiveStatus.class);
                    startActivity(liveStatusIntent);
                    overridePendingTransition(R.anim.animation_slide, R.anim.animation_slide_back);
                    break;
                case "Train Route":
                    Intent trainRouteIntent = new Intent(MainActivity.this, TrainRoute.class);
                    startActivity(trainRouteIntent);
                    overridePendingTransition(R.anim.animation_slide, R.anim.animation_slide_back);
                    break;
                case "Search Trains":
                    Intent searchTrainIntent = new Intent(MainActivity.this, SearchTrain.class);
                    startActivity(searchTrainIntent);
                    overridePendingTransition(R.anim.animation_slide, R.anim.animation_slide_back);
                    break;
                case "Fare Enquiry":
                    Intent fareEnquiryIntent = new Intent(MainActivity.this, FareEnquiry.class);
                    startActivity(fareEnquiryIntent);
                    overridePendingTransition(R.anim.animation_slide, R.anim.animation_slide_back);
                    break;
                case "Seat Availability":
                    Intent seatAvailIntent = new Intent(MainActivity.this, SeatAvail.class);
                    startActivity(seatAvailIntent);
                    overridePendingTransition(R.anim.animation_slide, R.anim.animation_slide_back);
                    break;
                case "Cancelled Trains":
                    if (isConnected()) {
                        finish();
                        Intent cancelledTrainIntent = new Intent(MainActivity.this, CancelledTrain.class);
                        startActivity(cancelledTrainIntent);
                        break;
                    } else{
                        IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", MainActivity.this);
                        break;
                    }
                case "IRCTC Booking":
                    if (isConnected()) {
                        Intent irctcBooking = new Intent(MainActivity.this, IRCTCBooking.class);
                        startActivity(irctcBooking);
                        break;
                    } else{
                        IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", MainActivity.this);
                        break;
                    }
                case "Rescheduled Trains":
                    if (isConnected()) {
                        finish();
                        Intent rescheduledTrain = new Intent(MainActivity.this, RescheduledTrain.class);
                        startActivity(rescheduledTrain);
                        break;
                    } else{
                        IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", MainActivity.this);
                        break;
                    }
                case "Diverted Trains":
                    if (isConnected()) {

                        Intent divertedTrains = new Intent(MainActivity.this, DivertedTrain.class);
                        startActivity(divertedTrains);
                        break;
                    } else{
                        IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", MainActivity.this);
                        break;
                    }
                case "Rate us":
                    if (isConnected()) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + IndianRailwayInfo.APP_PNAME)));
                        break;
                    } else{
                        IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", MainActivity.this);
                        break;
                    }
                case "Share the App":
                if (isConnected())
                {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Indian Railways App provides all transit related information. \n" +
                            "Hi Friends,\n I'm using Indian Railways app. It's really awesome. \n Please download it from: \n " +
                            "https://goo.gl/kpHWkc");
                    startActivity(shareIntent);
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
    }
}
