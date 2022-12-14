package com.ari1337an.healthsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Objects;

public class DataScreen extends AppCompatActivity {
    private static final String TAG = "DataScreen";
    private UserSettings settings;
    private Handler handler;
    private Context mContext;

    public void showLogScreen(View view) {
        Intent takeToLogPage = new Intent(getApplicationContext(), ViewLogScreen.class);
        switch (view.getId()) {

            case R.id.heart_card:
                Log.d(TAG, "showLogScreen: heart");
                takeToLogPage.putExtra("datatype", "Item4");
                takeToLogPage.putExtra("itemNo", "Item4");
                startActivity(takeToLogPage);
                break;
            case R.id.temp_card:
                Log.d(TAG, "showLogScreen: temp");
                takeToLogPage.putExtra("datatype", "Item1");
                takeToLogPage.putExtra("itemNo", "Item1");
                startActivity(takeToLogPage);
                break;
            case R.id.oxygen_card:
                Log.d(TAG, "showLogScreen: oxygen");
                takeToLogPage.putExtra("datatype", "Item3");
                takeToLogPage.putExtra("itemNo", "Item3");
                startActivity(takeToLogPage);
                break;
            case R.id.humidity_card:
                Log.d(TAG, "showLogScreen: humidity");
                takeToLogPage.putExtra("datatype", "Item2");
                takeToLogPage.putExtra("itemNo", "Item2");
                startActivity(takeToLogPage);
                break;
            default:
                break;
        }


    }


//    double temperature = 0;
//    double humidity = 0;
//    double oxygen = 0;
//    double heart = 0;

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
        public static final int CONNECTED = 3;
        public static final int CONNECTION_FAILED = 4;
        public static final int DISCONNECTED = 5;

        // ... (Add other message types here as needed.)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DecimalFormat df = new DecimalFormat("#.#");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

        mContext = this.getApplicationContext();
        settings = (UserSettings) getApplication();
        settings.loadAllSharedPreferences();

        TextView tempVal = findViewById(R.id.temp_value);
        TextView humidVal = findViewById(R.id.humidity_value);
        TextView oxyVal = findViewById(R.id.oxygen_value);
        TextView heartVal = findViewById(R.id.heart_value);

        TextView temp_title = findViewById(R.id.temp_title);
        TextView temp_subtitle = findViewById(R.id.temp_subtitle);
        TextView humidity_title = findViewById(R.id.humidity_title);
        TextView humidity_subtitle = findViewById(R.id.humidity_subtitle);
        TextView oxygen_title = findViewById(R.id.oxygen_title);
        TextView oxygen_subtitle = findViewById(R.id.oxygen_subtitle);
        TextView heart_title = findViewById(R.id.heart_title);
        TextView heart_subtitle = findViewById(R.id.heart_subtitle);

        // Set Card 1 Views
        temp_title.setText(settings.getItem1Name());
        temp_subtitle.setText(settings.getItem1Desc());

        humidity_title.setText(settings.getItem2Name());
        humidity_subtitle.setText(settings.getItem2Desc());

        oxygen_title.setText(settings.getItem3Name());
        oxygen_subtitle.setText(settings.getItem3Desc());

        heart_title.setText(settings.getItem4Name());
        heart_subtitle.setText(settings.getItem4Desc());

        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MessageConstants.MESSAGE_READ) {
                    try {
                        Log.d("CSE323", "UIThread: "+msg.obj);
                        String[] splitted = String.valueOf(msg.obj).split(" ");

                        int indexOfItem1 = Integer.parseInt(settings.getItem1Index());
                        int indexOfItem2 = Integer.parseInt(settings.getItem2Index());
                        int indexOfItem3 = Integer.parseInt(settings.getItem3Index());
                        int indexOfItem4 = Integer.parseInt(settings.getItem4Index());

                        String suffixOfItem1 = settings.getItem1Suf();
                        String suffixOfItem2 = settings.getItem2Suf();
                        String suffixOfItem3 = settings.getItem3Suf();
                        String suffixOfItem4 = settings.getItem4Suf();

                        String item1Val = splitted[indexOfItem1];
                        String item2Val = splitted[indexOfItem2];
                        String item3Val = splitted[indexOfItem3];
                        String item4Val = splitted[indexOfItem4];

                         Utils.storeAllData(item1Val, item2Val, item3Val, item4Val, getApplicationContext());

                        int x1 = Integer.parseInt(settings.getItem1Index());
                        int x2 = Integer.parseInt(settings.getItem2Index());
                        int x3 = Integer.parseInt(settings.getItem3Index());
                        int x4 = Integer.parseInt(settings.getItem4Index());

                        int mx = x1;
                        mx = Math.max(mx, x2);
                        mx = Math.max(mx, x3);
                        mx = Math.max(mx, x4);

                        if(mx < splitted.length){
                            if(!(Objects.equals(item1Val, "NAN") || Objects.equals(item2Val, "NAN") || Objects.equals(item3Val, "NAN") || Objects.equals(item4Val, "NAN"))){
                                TextView humid_suff = findViewById(R.id.humid_suff);
                                TextView oxygen_suff = findViewById(R.id.oxygen_suff);
                                TextView pulse_suff = findViewById(R.id.pulse_suff);

                                double value1 = Double.parseDouble(item1Val);
                                double value2 = Double.parseDouble(item2Val);
                                double value3 = Double.parseDouble(item3Val);
                                double value4 = Double.parseDouble(item4Val);
                                value1 = Double.parseDouble(df.format(value1));
                                value2 = Double.parseDouble(df.format(value2));
                                value3 = Double.parseDouble(df.format(value3));
                                value4 = Double.parseDouble(df.format(value4));

                                Log.d(TAG, "handleMessage: " + value1 + " " + value2 + " " + value3 + " " + value4);

                                humid_suff.setText(suffixOfItem2);
                                oxygen_suff.setText(suffixOfItem3);
                                pulse_suff.setText(suffixOfItem4);

//                                tempVal.setText((item1Val) + suffixOfItem1);
//                                humidVal.setText((item2Val)  + "");
//                                oxyVal.setText((item3Val)  + "");
//                                heartVal.setText((item4Val)  + "");
//
                                tempVal.setText((value1) + suffixOfItem1);
                                humidVal.setText((value2)  + "");
                                oxyVal.setText((value3)  + "");
                                heartVal.setText((value4)  + "");

                            }else{
                                Toast.makeText(mContext, "NAN Value!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(mContext, "Settings Index Error!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == MessageConstants.CONNECTED) {
                    Toast.makeText(mContext, "Connected to Device", Toast.LENGTH_SHORT).show();
                } else if (msg.what == MessageConstants.CONNECTION_FAILED) {
                    Toast.makeText(mContext, "Target Device doesn't have Socket Open!", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (msg.what == MessageConstants.DISCONNECTED) {
                    Toast.makeText(mContext, "Disconnected!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };

        Intent currentIntent = getIntent();
        String macAddress = currentIntent.getStringExtra("macaddress");

        BluetoothDevice d = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress);

        // start connection
        BluetoothService bluetoothService = BluetoothService.getInstance(this, handler, settings);
        bluetoothService.connect(d);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BluetoothService currentService = BluetoothService.getInstance(this, handler, settings);
        currentService.closeAll();


        Intent i = new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish(); // to end the current activity

    }
}