package com.ari1337an.healthsensors;

import static com.ari1337an.healthsensors.DeviceListActivity.CONNECTING_STATUS;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ConnectThread extends Thread {
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket mSocket = null;
    Context mContext;
    Handler handler;

    @SuppressLint("MissingPermission")
    public ConnectThread(BluetoothAdapter ba, String macAddress, Context context, Handler h) {
        bluetoothAdapter = ba;
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
        mContext = context;
        handler = h;
        UUID uuid = null;

        Log.d("CSE323", "Trying to connect to: " + bluetoothDevice.getName());

        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);

            if (uuids != null) {
                uuid = uuids[0].getUuid();
                Log.d("CSE323", "UUID: " + uuid.toString());
            } else {
                Toast.makeText(mContext, "UUIDs not found, be sure to enable bluetooth!", Toast.LENGTH_SHORT).show();
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        try {

            mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception e) {
            Log.e("CSE323", "Socket's create() method failed", e);
        }
    }

    @SuppressLint("MissingPermission")
    public void run() {

        Log.d("CSE323", "Running connection on separate thread!");

        bluetoothAdapter.cancelDiscovery();
        try {
            mSocket.connect();
            Log.e("Status", "Device connected");
//                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException connectException) {
            connectException.printStackTrace();
            try {
                mSocket.close();
                Log.e("Status", "Cannot connect to device");
//                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
            } catch (IOException closeException) {
                Log.e("CSE323", "Could not close the client socket", closeException);
            }
            return;
        } catch (Exception e){
            e.printStackTrace();
        }

//            // The connection attempt succeeded. Perform work associated with
//            // the connection in a separate thread.
//            connectedThread = new ConnectedThread(mmSocket);
//            connectedThread.run();
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (Exception e) {
            Log.e("CSE323", "Could not close the client socket", e);
        }
    }


}
