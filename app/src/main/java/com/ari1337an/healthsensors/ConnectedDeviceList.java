package com.ari1337an.healthsensors;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ConnectedDeviceList extends AppCompatActivity implements ItemsClicked {

    Context mContext = null;
    ProgressBar loaderDiscovery;
    RecyclerView deviceListRV;
    ArrayList<BluetoothDevice> foundDevices = new ArrayList<>();
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_device_list);

        // Get the context
        mContext = getApplicationContext();

        // Get the UI
        deviceListRV = findViewById(R.id.deviceListRVConnected);
        loaderDiscovery = findViewById(R.id.loaderDiscoveryConnected);

        // Initialize the Bluetooth adapter.
        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            goBackToMainActivity("Device doesn't support bluetooth");
        } else {
            // found bluetooth adapter
            if (!bluetoothAdapter.isEnabled()) { // enable the bluetooth now
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                someActivityResultLauncher.launch(enableBtIntent);
            } else { // bluetooth is enabled, now look for peripheral devices
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    Toast.makeText(mContext, "You have " + pairedDevices.size() + " devices paired!", Toast.LENGTH_SHORT).show();
                    for (BluetoothDevice device : pairedDevices) {
                        foundDevices.add(device);
                    }
                }
                updateTheList();
            }
        }

    }

    public void goBackToMainActivity(String messageForToast) {
        Toast.makeText(this, messageForToast, Toast.LENGTH_SHORT).show();
        finish(); // take back to the main activity
    }

    private void updateTheList(){
        deviceListRV.setAdapter(new DeviceListRVAdapter(foundDevices, this));
        deviceListRV.setLayoutManager(new LinearLayoutManager(mContext));
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(this, "Thanks for enabling bluetooth!", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }else if(result.getResultCode() == Activity.RESULT_CANCELED){
                    goBackToMainActivity("You need to enable bluetooth!");
                }else{
                    goBackToMainActivity("Unknown problem!");
                }
            }
    );

    @Override
    public void onClickedItem(BluetoothDevice d) {
        Log.d("CSE323", "clicked on: " + d.getAddress());

        // Create a bluetooth service to connect
        Intent takeToConnectedPage = new Intent(mContext, DataScreen.class);
        takeToConnectedPage.putExtra("macaddress", d.getAddress());
        startActivity(takeToConnectedPage);
    }
}