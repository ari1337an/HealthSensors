package com.ari1337an.healthsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class DataViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
    }

    public void sendDataToDevice(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserSettings uset = (UserSettings) getApplication();
        BluetoothService currentService = BluetoothService.getInstance(this, new Handler(), uset );
        currentService.closeAll();
    }
}