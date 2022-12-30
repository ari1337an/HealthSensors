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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceListActivity extends AppCompatActivity implements ItemsClicked {

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    RecyclerView deviceListRV;
    ArrayList<BluetoothDevice> foundDevices = new ArrayList<>();
    ProgressBar loaderDiscovery;
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

    /**
     * After completing the request Permission the discovery process will start at the onRequestPermissionsResult function above
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void startNearbyBluetoothDeviceDiscovery(){
        if(Build.VERSION.SDK_INT >= 31){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
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
                foundDevices.add(device);
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
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    @Override
    public void onClickedItem(BluetoothDevice d) {
        Log.d("CSE323", "clicked on: " + d.getAddress());
        Intent takeToConnectedPage = new Intent(mContext, DataScreen.class);
        takeToConnectedPage.putExtra("macaddress", d.getAddress());
        startActivity(takeToConnectedPage);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}