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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceListActivity extends AppCompatActivity implements ItemsClicked {

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    RecyclerView deviceListRV;
    ArrayList<Device> foundDevices = new ArrayList<>();
    ProgressBar loaderDiscovery;
    Context mContext;
    public static Handler handler;
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 255;

    final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update


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

    /**
     * After completing the request Permission the discovery process will start at the onRequestPermissionsResult function above
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void startNearbyBluetoothDeviceDiscovery(){
        int REQUEST_PERMISSION_BLUETOOTH_SCAN = 254;
        int REQUEST_PERMISSION_BLUETOOTH_CONNECT = 250;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
    }

    public void goBackToMainActivity(String messageForToast) {
        Toast.makeText(this, messageForToast, Toast.LENGTH_SHORT).show();
        finish(); // take back to the main activity
    }

    private void updateTheList(){
        deviceListRV.setAdapter(new DeviceListRVAdapter(foundDevices, this));
        deviceListRV.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                loaderDiscovery.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Started Discovering!", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                updateTheList();
                Toast.makeText(context, "Finished Discovering!", Toast.LENGTH_SHORT).show();
                loaderDiscovery.setVisibility(View.GONE);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Get the Data
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                if(deviceName == null) return;

                // Notify the User
                Toast.makeText(context, "Found a device nearby! " + deviceName, Toast.LENGTH_SHORT).show();

                // Parse the Data
                Device newDevice = new Device();
                newDevice.devicename = deviceName;
                newDevice.macaddress = deviceHardwareAddress;

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
        loaderDiscovery = findViewById(R.id.loaderDiscovery);

        // Set the Recycler view Adapter
        deviceListRV.setAdapter(new DeviceListRVAdapter(foundDevices,this));

        // Initialize the Bluetooth adapter.
        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();


        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                Log.d("CSE323", "msg1");
                                break;
                            case -1:
                                Log.d("CSE323", "msg2");
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        switch (arduinoMsg.toLowerCase()){
                            case "led is turned on":
                                Log.d("CSE323", "msg3");
                                break;
                            case "led is turned off":
                                Log.d("CSE323", "msg4");
                                break;
                        }
                        break;
                }
            }
        };



        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            goBackToMainActivity("Device doesn't support bluetooth");
        } else {
            // found bluetooth adapter
            if (!bluetoothAdapter.isEnabled()) { // enable the bluetooth now
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                someActivityResultLauncher.launch(enableBtIntent);
            } else { // bluetooth is enabled, now look for peripheral devices
                startNearbyBluetoothDeviceDiscovery();
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

    @Override
    public void onClickedItem(Device d) {
        Log.d("CSE323", "clicked on: " + d.macaddress);

        ConnectThread connectThread = new ConnectThread(bluetoothAdapter, d.macaddress, mContext, handler);
        connectThread.start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}