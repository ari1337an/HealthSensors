package com.ari1337an.healthsensors;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private UserSettings settings;

    AlertDialog alertDialog;
    int permissionsCount = 0;
    ArrayList<String> permissionsList;
    ActivityResultLauncher<String[]> permissionsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = (UserSettings) getApplication();
        settings.loadAllSharedPreferences();

        permissionsList = new ArrayList<>();
        String[] permissionsStr;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            permissionsStr = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        }else{
            permissionsStr = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        }
        permissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
        new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String,Boolean> result) {
                ArrayList<Boolean> list = new ArrayList<>(result.values());
                permissionsList = new ArrayList<>();


                String[] permissionsStr;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    permissionsStr = new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    };
                }else{
                    permissionsStr = new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    };
                }


                int permissionsCount = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                        permissionsList.add(permissionsStr[i]);
                    }else if (!hasPermission(MainActivity.this, permissionsStr[i])){
                        permissionsCount++;
                    }
                }
                if (permissionsList.size() > 0) {
                    //Some permissions are denied and can be asked again.
                    askForPermissions(permissionsStr);
                } else if (permissionsCount > 0) {
                    //Show alert dialog
                    showPermissionDialog();
                } else {
                    //All permissions granted. Do your stuff 🤞
                }
            }
        });

        askForPermissions(permissionsStr);

    }

//    private void loadSharedPreferences() {
//        SharedPreferences sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
//        String item1Index = sharedPreferences.getString(UserSettings.ITEM1_INDEX, String.valueOf(1));
//        settings.setItem1Index(item1Index);
//    }

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    private void askForPermissions(String[] permissionsList) {
        if (permissionsList.length > 0) {
            permissionsLauncher.launch(permissionsList);
        }
    }

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required")
                .setMessage("Some permissions are need to be allowed to use this app without any problems.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    dialog.dismiss();
                });
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    public void connectBtnClick(View view) {
        Intent takeToDeviceList = (Intent) new Intent(this, DeviceListActivity.class);
        startActivity(takeToDeviceList);

    }

    public void prefrencesBtnClick(View view) {
        Intent takeToPreferences = (Intent) new Intent(this, Preferences.class);
        startActivity(takeToPreferences);
    }

    public void connectedBtnClick(View view) {
        Intent takeToConnected = (Intent) new Intent(this, ConnectedDeviceList.class);
        startActivity(takeToConnected);
    }
}