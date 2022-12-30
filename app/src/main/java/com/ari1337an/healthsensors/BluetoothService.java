package com.ari1337an.healthsensors;

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

    @SuppressLint("StaticFieldLeak")
    private static volatile BluetoothService INSTANCE = null;

    private static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Constants
    public static final int STATE_NOT_CONNECTED = 0;
    public static final int STATE_CONNECTED = 1;

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
        public static final int CONNECTED = 3;
        public static final int CONNECTION_FAILED = 4;
        public static final int DISCONNECTED = 5;

        // ... (Add other message types here as needed.)
    }

    public void closeAll(){
        mConnectedThread.cancel();
        mConnectThread.cancel();
        BluetoothService.INSTANCE = null;
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

    private BluetoothService(Context context, Handler handler){
        mHandler = handler;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mState = STATE_NOT_CONNECTED;
    }

    public static BluetoothService getInstance(Context context, Handler handler){
        if(INSTANCE == null){
            synchronized (BluetoothService.class){
                if(INSTANCE == null){
                    INSTANCE = new BluetoothService(context, handler);
                }
            }
        }
        return INSTANCE;
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
            Log.d("CSE323", "Trying to connect to: " + d.getName());
            try {
                mSocket = d.createInsecureRfcommSocketToServiceRecord(BLUETOOTH_SPP);
            } catch (Exception e) {
                Log.e("CSE323", "Socket's create() method failed", e);
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                mSocket.connect();
                mState = STATE_CONNECTED;
                handler.obtainMessage(MessageConstants.CONNECTED, 1).sendToTarget();
            } catch (IOException connectException) {
                connectException.printStackTrace();
                try {
                    mSocket.close();
                    handler.obtainMessage(MessageConstants.CONNECTION_FAILED, 1).sendToTarget();
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

            String line = "";

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    numBytes = mmInStream.read(mmBuffer);
                    byte[] byteGot = Arrays.copyOf(mmBuffer, numBytes);
                    String stringGot = new String(byteGot, StandardCharsets.UTF_8);
//                    if(stringGot.charAt(0) == 'C'){
//                        String[] words = line.split(" ");
//                        if(words.length == 8){ // check that the input stream we got was not partial
//                            Message readMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_READ, line);
//                            readMsg.sendToTarget();
//                        }
//                        line = "";
//                    }else{
//                        line = line + stringGot;
//                    }
//                    Log.d("CSE323", stringGot);



                        int hasDollar = stringGot.indexOf("$",0);
                        int hasSemicolon = stringGot.indexOf(";",0);

                        if(hasDollar >= 0 && hasSemicolon >= 0 && hasDollar < hasSemicolon) {
                            // contains both
                            line = stringGot.substring(hasDollar,hasSemicolon);
                            String[] words = line.split(" ");
                            if(words.length == 8){ // check that the input stream we got was not partial
                                Message readMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_READ, line);
                                readMsg.sendToTarget();
                            }
                            line = "";
                        }else if(hasDollar >= 0){
                            // has a dollar sign // starting
                            line = stringGot.substring(hasDollar);
                        }else if(hasSemicolon >= 0){
                            line = line + stringGot.substring(0, hasSemicolon);
                            // process end of line
                            String[] words = line.split(" ");
                            if(words.length == 8){ // check that the input stream we got was not partial
                                Message readMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_READ, line);
                                readMsg.sendToTarget();
                            }
                            Log.d("CSE323", line);
                            if(hasSemicolon+1 < stringGot.length())line = stringGot.substring(hasSemicolon+1);
                            else line = "";
                        }else {
                            line = line + stringGot;
                        }




                } catch (IOException e) {
                    Message readMsg = mHandler.obtainMessage(MessageConstants.DISCONNECTED, 1);
                    readMsg.sendToTarget();
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
