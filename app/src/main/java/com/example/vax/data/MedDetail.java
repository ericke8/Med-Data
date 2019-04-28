package com.example.vax.data;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vax.R;

public class MedDetail extends AppCompatActivity {

    private WebView webView;
    private ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_detail);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);

        // Here we turn your string.xml in an array
        String[] myKeys = getResources().getStringArray(R.array.sections);

        TextView myTextView = (TextView) findViewById(R.id.my_textview);
        myTextView.setText(myKeys[position]);

        webView = findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        progDialog = ProgressDialog.show(activity, "Loading","Please wait...", true);
        progDialog.setCancelable(false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDialog.show();
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDialog.dismiss();
            }
        });

        // Get name of medicine to get Mayo Clinic query
        String medName = myKeys[position].trim();
        String queryMedName = "";
        String[] medNameArr = medName.split(" ");
        for (String medNamePart : medNameArr) {
            queryMedName += medNamePart + "%20";
        }
        // Get rid of last space
        queryMedName = queryMedName.substring(0, queryMedName.length()-3);

        webView.loadUrl("https://www.google.com/search?q=" + queryMedName + "&btnI=I%27m+Feeling+Lucky");
        //setContentView(R.layout.activity_med_detail);

    }

}

