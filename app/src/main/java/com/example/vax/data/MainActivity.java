
package com.example.vax.data;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import com.example.vax.R;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


import com.example.vax.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.sql.Timestamp;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private List<String> myMeds;
    private List<String> badMeds;
    private String currentUserID;


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
        TextView textVieww = (TextView) findViewById(R.id.news);


        textVieww.setMovementMethod(new ScrollingMovementMethod());
        mTextMessage = findViewById(R.id.message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        myMeds = new ArrayList<String>();
        myMeds.add("Betamethasone NA Phosphate");
        myMeds.add("THIS IS NOT A BAD MED");
        badMeds = new ArrayList<String>();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        currentUserID = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.alertText);
        textView.setText("No recalls to report");


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create URL
                URL drugURL = null;
                try {
                    drugURL = new URL("https://api.fda.gov/drug/enforcement.json?search=report_date:[19700101+TO+20191231]&limit=100");
                } catch (Exception e) {

                }
// Create connection
                try {
                    HttpsURLConnection myConnection = (HttpsURLConnection) drugURL.openConnection();
                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        // Do something with the value
                        getDrugs(jsonReader);
                        TextView medAlerts = findViewById(R.id.alertText);
                        medAlerts.setMovementMethod(new ScrollingMovementMethod());
                        medAlerts.setText(compareMedLists(myMeds, badMeds));

                    } else {
                        TextView medAlerts = findViewById(R.id.alertText);
                        medAlerts.setText("Error Connecting to Database...");
                    }
                } catch (Exception e) {

                }

                TextView foodInfo = findViewById(R.id.news);
                foodInfo.setText(getFoodInfo());
            }
        });
    }

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public void scrollSwitch(View view){
        Intent intent = new Intent(this, ScrollingActivity.class);
        TextView text = (TextView) findViewById(R.id.alertText);
        String message = text.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void myMedsSwitch(View view){
        Intent intent = new Intent(this, MyMeds.class);
        //TextView text = (TextView) findViewById(R.id.textView2);
        //String message = text.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, currentUserID);
        startActivity(intent);
    }

    public void infoRecallSwitch(View view) {

        Intent myIntent = new Intent(this, InfoRecall.class);
        startActivity(myIntent);
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
            String dateName = "recall_initiation_date";
            String ans = null;
            String date = null;
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals(keyName)) {
                    ans = jsonReader.nextString();
                } else if (name.equals(dateName)) {
                    date = jsonReader.nextString();
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            return date + ": " + ans;
        } catch (Exception e) {
            return "HEROIN";
        }
    }

    public String compareMedLists(List<String> myMeds, List<String> badMeds) {
        String ans = "";
        for (int i = 0; i < badMeds.size(); i++) {
            for (int j = 0; j < myMeds.size(); j++) {
                if (badMeds.get(i).contains(myMeds.get(j))) {
                    String date = badMeds.get(i).split(": ")[0];
                    date = date.substring(4, 6) + "/" + date.substring(6) + "/" + date.substring(0, 4);
                    ans += date + ": " + myMeds.get(j);
                    ans += "\n";
                    ans += "\n";
                }
            }
        }
        return ans;
    }

    public String getFoodInfo() {
        String result = " ";
        URL foodURL;
        try {
            long millisecondsInWeek = 604800000;
            String dateRange[] = getTimeRange(millisecondsInWeek * 2);
            foodURL = new URL("https://api.fda.gov/food/enforcement.json?search=report_date:[" + dateRange[0].trim() + "+TO+" + dateRange[1].trim() + "]&limit=5");
            HttpsURLConnection myConnection = (HttpsURLConnection) foodURL.openConnection();
            if (myConnection.getResponseCode() == 200) {
                // Success
                // Further processing here
                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                JsonReader jsonReader = new JsonReader(responseBodyReader);

                try {
                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) {
                        String key = jsonReader.nextName();
                        if (key.equals("results")) {
                            jsonReader.beginArray();
                            while (jsonReader.hasNext()) {
                                result += getFoodName(jsonReader);
                                result += "\n\n";
                            }
                            jsonReader.endArray();
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.close();
                } catch (Exception e) {
                }
            } else {
                return "Error Connecting to Database...";
            }
        } catch (Exception e) {
            return "Error Connecting to Database...";
        }
        return result;
    }


    public String getFoodName(JsonReader jsonReader) {
        try {
            String keyName = "product_description";
            String keyDate = "report_date";
            String ans = null;
            String tempName = "";
            String tempDate = "";
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals(keyName)) {
                    tempName = jsonReader.nextString();
                } else if (name.equals(keyDate)) {
                    tempDate = jsonReader.nextString();
                } else {
                    jsonReader.skipValue();
                }
            }

            jsonReader.endObject();
            tempDate = tempDate.substring(4,6) + "/" + tempDate.substring(6) + "/" + tempDate.substring(0, 4);
            ans = tempDate + ": " + tempName;
            return ans;
        } catch (Exception e) {
            return "FOOD";
        }
    }

    public String[] getTimeRange(long range) {
        String[] timeRange = new String[2];
        Date date = new Date();
        long timeNow = date.getTime();
        long timeThen = timeNow - range;
        Timestamp ts0 = new Timestamp(timeThen);
        Timestamp ts1 = new Timestamp(timeNow);
        timeRange[1] = ts1.toString();
        timeRange[0] = ts0.toString();

        // Parse Out Date
        for (int i = 0; i < timeRange.length; i++) {
            String result = "";
            String dateStr = timeRange[i].split(" ")[0];
            String[] dateParts = dateStr.split("-");
            for (String datePart : dateParts) {
                result += datePart;
            }
            timeRange[i] = result;
        }
        return timeRange;
    }

}
