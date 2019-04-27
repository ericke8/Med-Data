package com.example.vax.data;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import com.example.vax.R;


import com.example.vax.ui.login.LoginActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private List<String> myMeds;
    private List<String> badMeds;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = findViewById(R.id.message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        myMeds = new ArrayList<String>();
        myMeds.add("Betamethasone NA Phosphate");
        myMeds.add("THIS IS NOT A BAD MED");
        badMeds = new ArrayList<String>();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView2);
        textView.setText(message);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create URL
                URL githubEndpoint = null;
                try {
                    githubEndpoint = new URL("https://api.fda.gov/drug/enforcement.json?search=report_date:[20040101+TO+20131231]&limit=100");
                } catch (Exception e) {

                }
// Create connection
                try {
                    HttpsURLConnection myConnection = (HttpsURLConnection) githubEndpoint.openConnection();
                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        // Do something with the value
                        getDrugs(jsonReader);
                        TextView textView = findViewById(R.id.json);
                        textView.setMovementMethod(new ScrollingMovementMethod());
                        textView.setText(compareMedLists(myMeds, badMeds));

                    } else {
                        TextView textView = findViewById(R.id.json);
                        textView.setText("Error Connecting to Database...");
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public void scrollSwitch(View view){
        Intent intent = new Intent(this, ScrollingActivity.class);
        TextView text = (TextView) findViewById(R.id.textView2);
        String message = text.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void myMedsSwitch(View view){
        Intent intent = new Intent(this, MyMeds.class);
        //TextView text = (TextView) findViewById(R.id.textView2);
        //String message = text.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void getDrugs(JsonReader jsonReader){
        try {
            jsonReader.beginObject(); // Start processing the JSON object
            while (jsonReader.hasNext()) {
                String key = jsonReader.nextName();
                if (key.equals("results")) {
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {

                       String result = getDrugName(jsonReader);
                       System.out.println(result);
                       badMeds.add(result);
                    }
                    jsonReader.endArray();
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.close();
        } catch (Exception e) {
        }
    }

    public String getDrugName(JsonReader jsonReader) {
        try {
            String keyName = "product_description";
            String ans = null;
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals(keyName)) {
                    ans = jsonReader.nextString();
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            return ans;
        } catch (Exception e) {
            return "HEROIN";
        }
    }

    public String compareMedLists(List<String> myMeds, List<String> badMeds) {
        String ans = "";
        for (int i = 0; i < badMeds.size(); i++) {
            for (int j = 0; j < myMeds.size(); j++) {
                if (badMeds.get(i).contains(myMeds.get(j))) {
                    ans += myMeds.get(j);
                    ans += "\n";
                }
            }
        }
        return ans;
    }

}
