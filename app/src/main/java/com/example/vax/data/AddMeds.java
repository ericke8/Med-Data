package com.example.vax.data;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.vax.R;

public class AddMeds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meds);

        final EditText addMedText = findViewById(R.id.addMedsText);
        final Button addButton = findViewById(R.id.addMedsButton);
    }
}
