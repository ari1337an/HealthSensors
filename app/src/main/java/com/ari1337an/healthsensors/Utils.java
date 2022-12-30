package com.ari1337an.healthsensors;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;


public class Utils {
    private static final String TAG = "Utils";
    public static void storeAllData (String temperature, String humidity, String oxygen, String heart, Context context){
        Log.d(TAG, "storeData: " + temperature + " " + humidity + " " + oxygen + " " + heart);
        storeData(temperature, context, "Item1");
        storeData(humidity, context, "Item2");
        storeData(oxygen, context, "Item3");
        storeData(heart, context, "Item4");
    }

    public static void storeData(String value, Context context, String dataType){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(dataType+".txt", Context.MODE_APPEND));
            outputStreamWriter.append(String.valueOf(LocalDateTime.now())).append(" => ").append(String.valueOf(value)).append("\n");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }



    public static String readData(Context context, String dataType){
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(dataType + ".txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        Log.d(TAG, "readTempData: " + ret + "\n\n");
        return ret;
    }

}
