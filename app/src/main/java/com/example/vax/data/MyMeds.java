package com.example.vax.data;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.vax.R;
import com.example.vax.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMeds extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String currentUserId;

    private ArrayList<String> meds;

    FirebaseFirestore db;

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_meds);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        currentUserId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        meds = new ArrayList<>();
        DocumentReference docRef = db.collection("users").document(currentUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        for(String s : document.getData().keySet()){
                            meds.add(s);
                            System.out.println("ADDING " + s);
                        }
                        //Log.d("MyMeds", "DocumentSnapshot data: " + document.getData());
                        //Log.d("MyMeds", "meds list data" + meds.toString());
                        ListView listView = (ListView) findViewById(R.id.listView1);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MyMeds.this, android.R.layout.simple_list_item_1, meds);
                        listView.setAdapter(arrayAdapter);
                        listView.setOnItemClickListener(MyMeds.this);
                    } else {
                        //Log.d("MyMeds", "No such document");
                        System.out.println("NO SUCH DOC");
                    }
                } else {
                    //Log.d("MyMeds", "get failed with ", task.getException());
                }
                System.out.println("ALMOST FINISHED " + meds.toString());
            }
        });

        //Log.d("MyMeds", "after loop meds data" + meds.toString());
        System.out.println("FINISHED: " + meds.toString());



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyMeds.this, AddMeds.class);
                intent.putExtra(EXTRA_MESSAGE, currentUserId);
                startActivity(intent);
            }
        });


    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        System.out.println("HELLLLOOOO LOOK AT MEEEE: " + position + " " + id);
        System.out.println(l.toString());
        System.out.println(v.toString());
        System.out.println(meds.toString());

        // Then you start a new Activity via Intent
        Intent intent = new Intent();
        intent.setClass(this, MedDetail.class);
        intent.putExtra("position", position);
        // Or / And
        intent.putExtra("id", id);
        intent.putExtra("name", meds.get(position));
        startActivity(intent);
    }

}


