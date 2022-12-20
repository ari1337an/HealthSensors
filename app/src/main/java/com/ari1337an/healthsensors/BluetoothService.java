package com.ari1337an.healthsensors;

import static com.ari1337an.healthsensors.DeviceListActivity.CONNECTING_STATUS;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;


public class BluetoothService {
    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;
    private final Context mContext;
    private int mState;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Constants
    public static final int STATE_NOT_CONNECTED = 0;
    public static final int STATE_CONNECTED = 1;

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    public BluetoothService(Context context, Handler handler){
        mHandler = handler;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mState = STATE_NOT_CONNECTED;
    }

    public synchronized void connect(BluetoothDevice device){
        mConnectThread = new ConnectThread(mBluetoothAdapter, device, mContext, mHandler);
        mConnectThread.start();
    }

    public synchronized void connected(BluetoothSocket socket){
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    public class ConnectThread extends Thread {
        BluetoothAdapter bluetoothAdapter;
        BluetoothSocket mSocket = null;
        Context mContext;
        Handler handler;

        @SuppressLint("MissingPermission")
        public ConnectThread(BluetoothAdapter ba, BluetoothDevice d, Context context, Handler h) {
            bluetoothAdapter = ba;
            mContext = context;
            handler = h;
            UUID uuid = null;

            Log.d("CSE323", "Trying to connect to: " + d.getName());

            try {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);

                Log.d("CSE323", "Total uuids: " + uuids.length);
                if (uuids != null) {
                    uuid = uuids[1].getUuid();
                    Log.d("CSE323", "UUID: " + uuid.toString());
                } else {
                    Toast.makeText(mContext, "UUIDs not found, be sure to enable bluetooth!", Toast.LENGTH_SHORT).show();
                }

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            try {
//                mSocket = d.createInsecureRfcommSocketToServiceRecord(uuid);
                mSocket = d.createInsecureRfcommSocketToServiceRecord(BLUETOOTH_SPP);
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
                Log.d("Status", "Device connected");
                mState = STATE_CONNECTED;
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                connectException.printStackTrace();
                try {
                    mSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e("CSE323", "Could not close the client socket", closeException);
                }
                return;
            } catch (Exception e){
                e.printStackTrace();
            }

            connected(mSocket);
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (Exception e) {
                Log.e("CSE323", "Could not close the client socket", e);
            }
        }


    }



    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("CSE323", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("CSE323", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            Log.d("CSE323", "Created streams from socket!");
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            String data = "Connected from HealthSensor App!";
            byte[] dataB = data.getBytes();
            write(dataB);

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {

//                    synchronized (this){
//                        try{
//                            wait(5000);
//                        }catch (InterruptedException e){
//                            e.printStackTrace();
//                        }
//                    }

                    Log.d("CSE323", "Waiting for InputStream from Bluetooth Device...");
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
//                    Message readMsg = mHandler.obtainMessage(
//                            MessageConstants.MESSAGE_READ, numBytes, -1,
//                            mmBuffer);
//                    readMsg.sendToTarget();
                    byte[] byteGot = Arrays.copyOf(mmBuffer, numBytes);
                    String stringGot = new String(byteGot, StandardCharsets.UTF_8);

                    Log.d("CSE323", "Received : " + String.valueOf(numBytes) + "Byte(s)");
                    Log.d("CSE323", "Data is : " + stringGot);
                } catch (IOException e) {
                    Log.d("CSE323", "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                Log.d("CSE323", "Written Successfully");
                // Share the sent message with the UI activity.
//                Message writtenMsg = mHandler.obtainMessage(
//                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
//                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e("CSE323", "Error occurred when sending data", e);

                // Send a failure message back to the activity.
//                Message writeErrorMsg =
//                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
//                Bundle bundle = new Bundle();
//                bundle.putString("toast",
//                        "Couldn't send data to the other device");
//                writeErrorMsg.setData(bundle);
//                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("CSE323", "Could not close the connect socket", e);
            }
        }
    }
}
