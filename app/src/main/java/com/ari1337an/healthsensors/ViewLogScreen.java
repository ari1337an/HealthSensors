package com.ari1337an.healthsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ViewLogScreen extends AppCompatActivity {

    private static final String TAG = "ViewLogScreen";
    String data = null;

    TextView logView;
    TextView logHeader;

    List<String> lines;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log_screen);

        logView= findViewById(R.id.logView);
        logHeader = findViewById(R.id.logHeader);

        Intent currentIntent = getIntent();
        String dataType = currentIntent.getStringExtra("datatype");
        data = Utils.readData(getApplicationContext(), dataType);
        logHeader.setText(dataType.toUpperCase() + " logs");
        logView.setText(data);


        lines = Arrays.asList(data.split("\n"));

//        Log.d(TAG, "onCreate: " + lines.toString());
        for(String line: lines){
//            Log.d(TAG, "onCreate: " + line.toString());
            String [] date = line.split(" => ");
//            if(date.length < 2) Log.d(TAG, "onCreate: " + date.length + " " + date[0]);
//            for(String val: date){
//                Log.d(TAG, "onCreate: "+  val);
//            }
//            Log.d(TAG, "onCreate: size " + date[1]);



        }

    }
}