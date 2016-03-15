package com.nitz.studio.indianrailways;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by nitinpoddar on 10/26/15.
 */
public class PNRStatus extends ActionBarActivity {

    private EditText pnrStatusEditTxt;
    private Toolbar toolbar;
    public PNRDBAdapter pnrdbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnrstatus);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);
        pnrStatusEditTxt = (EditText) findViewById(R.id.pnrStatusEditTxt);
        pnrStatusEditTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pnrStatusEditTxt.setCursorVisible(true);
                return false;
            }
        });
         }

    public void pnrStatusCheck(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        String pnrNo = pnrStatusEditTxt.getText().toString();
        String url = "http://api.railwayapi.com/pnr_status/pnr/" + pnrNo + "/apikey/" + IndianRailwayInfo.API_KEY + "/";
        if (pnrNo.length()!=10){
            pnrStatusEditTxt.setText("");
            Toast.makeText(this, "Please enter valid 10 digit PNR no.", Toast.LENGTH_SHORT).show();
        } else{
            if(isConnected()){
                Intent pnrStatusInt = new Intent(this, PNRStatusDisplay.class);
                pnrStatusInt.putExtra("url", url);
                pnrStatusInt.putExtra("pnr", pnrNo);
                startActivity(pnrStatusInt);
                finish();
            }else{
                IndianRailwayInfo.showErrorDialog("Network Error", "No Network Connection", PNRStatus.this);
            }
        }
    }
    public void getPNRHistory(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        pnrdbAdapter = new PNRDBAdapter(PNRStatus.this);
        int count = pnrdbAdapter.getTableCount();
        if (count > 0) {
            Intent pnrHistory = new Intent(this, PNRHistory.class);
            startActivity(pnrHistory);
            finish();
        } else{
            Toast.makeText(this, "No data in PNR History", Toast.LENGTH_SHORT).show();
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
