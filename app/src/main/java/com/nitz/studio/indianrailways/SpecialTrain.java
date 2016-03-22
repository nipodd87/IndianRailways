package com.nitz.studio.indianrailways;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by nitinpoddar on 3/20/16.
 */
public class SpecialTrain extends ActionBarActivity {
    private WebView webView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled);
        toolbar = (Toolbar) findViewById(R.id.app_bar_inc);
        setSupportActionBar(toolbar);

        IndianRailwayInfo.showProgress(SpecialTrain.this, "Loading", "Please wait while the Page is loading...");
        webView = (WebView) findViewById(R.id.webview01);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.invokeZoomPicker();
        webView.setWebViewClient(new MyWebClient());
        webView.loadUrl("http://enquiry.indianrail.gov.in/ntes/specialTrains.jsp");
    }

    public class MyWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            IndianRailwayInfo.showProgress(SpecialTrain.this, "Loading", "Please wait while the Page is loading again...");
            view.loadUrl(url);
            return true;

        }
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            //super.onPageFinished(view, url);
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
