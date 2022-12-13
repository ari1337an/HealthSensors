package com.ari1337an.healthsensors;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceListActivity extends AppCompatActivity {

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    RecyclerView deviceListRV;
    ArrayList<Device> foundDevices = new ArrayList<Device>();
    Context mContext;
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 255;

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start Discovering Peripheral Devices
                foundDevices.clear();
                bluetoothAdapter.startDiscovery();
            } else {
                goBackToMainActivity("Permission Denied: ACCESS_LIVE_LOCATION!");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestPermissionAccessFineLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
    }

    public void goBackToMainActivity(String messageForToast) {
        Toast.makeText(this, messageForToast, Toast.LENGTH_SHORT).show();
        finish(); // take back to the main activity
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("CSE323", "Started Discovery!");
                Toast.makeText(context, "Started Discovering!", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                deviceListRV.setAdapter(new DeviceListRVAdapter(foundDevices));
                deviceListRV.setLayoutManager(new LinearLayoutManager(mContext));

                Toast.makeText(context, "Finished Discovering!", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Get the Data
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                // Notify the User
                Toast.makeText(context, "Found a device nearby!" + deviceName, Toast.LENGTH_SHORT).show();

                // Parse the Data
                Device newDevice = new Device();
                newDevice.devicename = deviceName;

                // Add to List
                foundDevices.add(newDevice);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        // Register the Broadcast receiver(s)
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        // Activity Context
        mContext = this;

        // Get the UI
        deviceListRV = findViewById(R.id.deviceListRV);

        // Set the Recycler view Adapter
        deviceListRV.setAdapter(new DeviceListRVAdapter(foundDevices));

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
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionAccessFineLocation();
                }
            }
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Toast.makeText(this, "Thanks for enabling bluetooth!", Toast.LENGTH_SHORT).show();
            }else if(result.getResultCode() == Activity.RESULT_CANCELED){
                goBackToMainActivity("You need to enable bluetooth!");
            }else{
                goBackToMainActivity("Unknown problem!");
            }
        }
    );

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }
}