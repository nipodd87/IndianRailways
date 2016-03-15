package com.nitz.studio.indianrailways;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nitz.studio.indianrailways.model.PNRHistoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 11/23/15.
 */
public class PNRHistory extends ActionBarActivity{
    private ListView pnrList;
    public List<PNRHistoryModel> pnrHistoryModelList;
    public PNRDBAdapter DBAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnrhistory);

        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);
        pnrList = (ListView) findViewById(R.id.pnrList);
        DBAdapter = new PNRDBAdapter(PNRHistory.this);
        pnrHistoryModelList = new ArrayList<>();
        Cursor cursor;

        cursor = DBAdapter.selectAll();
        while (cursor.moveToNext()) {
            PNRHistoryModel model = new PNRHistoryModel();
            model.setPnrNo(cursor.getString(cursor.getColumnIndex("_id")));
            model.setTrainName(cursor.getString(cursor.getColumnIndex("train_name")));
            model.setTrainNo(cursor.getString(cursor.getColumnIndex("train_number")));
            model.setDoj(cursor.getString(cursor.getColumnIndex("date_of_journey")));
            pnrHistoryModelList.add(model);
        }
        cursor.close();
        ArrayAdapter<PNRHistoryModel> adapter = new PNRHistoryAdapter();
        pnrList.setAdapter(adapter);
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
    public class PNRHistoryAdapter extends ArrayAdapter<PNRHistoryModel>{

        public PNRHistoryAdapter(){
            super(PNRHistory.this, R.layout.itemview_pnr_history, pnrHistoryModelList);
        }
        @Override
        public PNRHistoryModel getItem(int position) {
            if (pnrHistoryModelList != null)
                return pnrHistoryModelList.get(position);
            else
                return null;
        }
        @Override
        public int getCount() {
            if (pnrHistoryModelList != null)
                return pnrHistoryModelList.size();
            else
                return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.itemview_pnr_history, parent, false);
            }
            PNRHistoryModel modelItem = pnrHistoryModelList.get(position);
            TextView pnrTxt = (TextView) itemView.findViewById(R.id.pnrTxt);
            TextView journeyDateTxt = (TextView) itemView.findViewById(R.id.journeyDateTxt);

            pnrTxt.setText(modelItem.getPnrNo());
            journeyDateTxt.setText(modelItem.getDoj());

            Button stop_tracking = (Button) itemView.findViewById(R.id.stop_tracking);
            Button refresh_pnr = (Button) itemView.findViewById(R.id.refresh_pnr);
            stop_tracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PNRHistoryModel item = pnrHistoryModelList.get(position);
                    String pnr = item.getPnrNo();
                    DBAdapter.deleteRow(pnr);
                    pnrHistoryModelList.remove(position);
                    notifyDataSetChanged();
                }
            });
            refresh_pnr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PNRHistoryModel item = pnrHistoryModelList.get(position);
                    String pnrNo = item.getPnrNo();
                    String url = "http://api.railwayapi.com/pnr_status/pnr/" + pnrNo + "/apikey/" + IndianRailwayInfo.API_KEY + "/";
                    if(isConnected()){
                        Intent pnrStatusInt = new Intent(PNRHistory.this, PNRStatusDisplay.class);
                        pnrStatusInt.putExtra("url", url);
                        pnrStatusInt.putExtra("pnr", pnrNo);
                        startActivity(pnrStatusInt);
                        finish();
                    }else{
                        Toast.makeText(PNRHistory.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return itemView;
        }
    }
}
