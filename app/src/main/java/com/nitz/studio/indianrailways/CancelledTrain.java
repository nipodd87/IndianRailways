package com.nitz.studio.indianrailways;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by nitinpoddar on 11/7/15.
 */
public class CancelledTrain extends ActionBarActivity {

    private WebView webView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);
        IndianRailwayInfo.showProgress(CancelledTrain.this, "Loading", "Please wait while the Page is loading...");

        webView = (WebView) findViewById(R.id.webview01);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.invokeZoomPicker();
        webView.setWebViewClient(new MyWebClient());
        webView.loadUrl("http://enquiry.indianrail.gov.in/mntes/");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh_only, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            webView.loadUrl("javascript:document.querySelectorAll('input[value=\"Go\"]')[0].click();");
        }
        return true;
    }
    public class MyWebClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            IndianRailwayInfo.showProgress(CancelledTrain.this, "Loading", "Please wait while the Page is loading...");
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            webView.loadUrl("javascript:document.querySelectorAll('input[value=\"Cancelled Trains\"]')[0].click();");
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            IndianRailwayInfo.hideProgress();
        }
    }
    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return super.onKeyDown(keyCode, event);
    }
}

