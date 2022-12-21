package com.ari1337an.healthsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class DataScreen extends AppCompatActivity {

    private Handler handler;
    private Context mContext;


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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

        mContext = this.getApplicationContext();

        TextView tempVal = findViewById(R.id.temp_value);
        TextView humidVal = findViewById(R.id.humidity_value);
        TextView oxyVal = findViewById(R.id.oxygen_value);
        TextView heartVal = findViewById(R.id.heart_value);


        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MessageConstants.MESSAGE_READ) {
                    Log.d("CSE323", String.valueOf(msg.obj));
                    String[] splitted = String.valueOf(msg.obj).split(" ");

                    double temperature = (Double.parseDouble(splitted[7]));
                    double humidity = (Double.parseDouble(splitted[5]));
                    double oxygen = (Double.parseDouble(splitted[3]));
                    double heart = (Double.parseDouble(splitted[1]));

                    Utils.storeAllData(temperature, humidity, oxygen, heart, getApplicationContext());
//                    Utils.readData(getApplicationContext());

                    tempVal.setText(Math.round(Double.parseDouble(splitted[7])) + "°");
                    humidVal.setText(Math.round((Double.parseDouble(splitted[5]))) + "");
                    oxyVal.setText(Math.round((Double.parseDouble(splitted[3]))) + "");
                    heartVal.setText(Math.round((Double.parseDouble(splitted[1]))) + "");

                } else if (msg.what == MessageConstants.CONNECTED) {
                    Toast.makeText(mContext, "Connected to Device", Toast.LENGTH_SHORT).show();
                } else if (msg.what == MessageConstants.CONNECTION_FAILED) {
                    Toast.makeText(mContext, "Target Device doesn't have Socket Open!", Toast.LENGTH_SHORT).show();
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
        BluetoothService bluetoothService = BluetoothService.getInstance(this, handler);
        bluetoothService.connect(d);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothService currentService = BluetoothService.getInstance(this, handler);
        currentService.closeAll();
    }
}