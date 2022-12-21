package com.ari1337an.healthsensors;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;

public class Utils {
    private static final String TAG = "Utils";
    public static void storeData (double temperature, double humidity, double oxygen, double heart){
        Log.d(TAG, "storeData: " + temperature + " " + humidity + " " + oxygen + " " + heart);
//        FileOutputStream fOut = openFileOutput("file name", Context.MODE_PRIVATE);
    }
}
