package com.ari1337an.healthsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void connectBtnClick(View view) {
        Intent takeToDeviceList = (Intent) new Intent(this, DeviceListActivity.class);
        startActivity(takeToDeviceList);
    }
}