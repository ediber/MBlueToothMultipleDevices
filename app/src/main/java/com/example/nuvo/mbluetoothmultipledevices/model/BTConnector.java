package com.example.nuvo.mbluetoothmultipledevices.model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import com.example.nuvo.mbluetoothmultipledevices.utils.ConstantsUtil;
import com.example.nuvo.mbluetoothmultipledevices.viewmodel.MainViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;


public class BTConnector {

    private BluetoothSocket mSocket;
    private BluetoothAdapter mBtAdapter;
    private ArrayList<BluetoothDevice> mPairedDevicesList;
    private BluetoothDevice mSelectedDevice;
    private ConnectingThread mConnectingThread;
    private int mPacketsCounter = 0;
    private int mId;

    public BTConnector(int id) {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mId = id;
    }

    private void createSocket() {
        mSocket = null;
        try {
            // TODO make in new thread
            mSocket = mSelectedDevice.createRfcommSocketToServiceRecord(mSelectedDevice.getUuids()[0].getUuid());
            Log.d(ConstantsUtil.CONNECTION_TAG + " 1", "socket created");

        } catch (IOException e) {
            try {
                mSocket.close();
                Log.d(ConstantsUtil.CONNECTION_TAG + " 1", "socket closed");
            } catch (IOException closeException) {
                Log.d(ConstantsUtil.CONNECTION_TAG + " 1", "Could not close socket", closeException);
            }
        }
    }

    public void connect(SocketConnectedListener listener) {
        mConnectingThread = new ConnectingThread(new SocketConnectedListener() {
            @Override
            public void onConnectionSuccess(int id) {
                listener.onConnectionSuccess(mId);
            }

            @Override
            public void onConnectionError(int id) {
                listener.onConnectionError(mId);
            }
        });

        mConnectingThread.start();
    }

    public void disconnect(SocketDisConnectListener listener) {
//        new DisconnectingThread(mSocket, listener).start();
        mConnectingThread.cancel();
    }

    public void send(SendMessage sendMessage, MessageSentListener listener) {
        writeToSocket(sendMessage.toBytes(), listener);
    }

    public void listenToIncomingMessages(MessageReceivedListener listener) {
        new AlreadyConnectedThread(mSocket, listener).start();
    }

    private List<String> showPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        mPairedDevicesList = new ArrayList<>(pairedDevices);

        List<String> deviceNames = devicesToNames(mPairedDevicesList);
        // TODO do it with RXjava

        return deviceNames;
    }

    private List<String> devicesToNames(List<BluetoothDevice> pairedDevicesList) {
        List<String> names = new ArrayList<>();

        // to add on min api 24
//                List<String> names = mPairedDevicesList.stream().map(BluetoothDevice::getName).collect(Collectors.toList());

        for (BluetoothDevice device : pairedDevicesList) {
            names.add(device.getName());
        }
        return names;
    }


    // if disabled ask the user to enable it
    public void showPairedDevicesIfBTEnabled(Activity activity, MainViewModel.viewModelListener listener) {
        // check if BT enabled on device
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 1);
        } else {
            List<String> deviceNames = showPairedDevices();
            if(listener != null){
                listener.onShowPairedDevices(deviceNames);
            }
        }
    }

    public void setSelectedDevice(int position) {
        mSelectedDevice = mPairedDevicesList.get(position);
    }

    public String getSelectedName() {
        return mSelectedDevice.getName();
    }

    // Call this from the main activity to mLst data to the remote device.
    private void writeToSocket(byte[] bytes, MessageSentListener listener) {

        OutputStream outStream = null;
        try {
            outStream = mSocket.getOutputStream();
        } catch (IOException e) {
            listener.onError("Error occurred when creating output stream", mId);
            Log.e("tag", "Error occurred when creating output stream", e);
        }

        try {
            if (outStream != null) {
                outStream.write(bytes);
            }

        } catch (IOException e) {
            listener.onError("Error occurred when sending data", mId);
            Log.e("tag", "Error occurred when sending data", e);
        }
    }

    public void clearPacketsCounter() {
        mPacketsCounter = 0;
    }


    private class ConnectingThread extends Thread {

        private SocketConnectedListener listener;

        public ConnectingThread(SocketConnectedListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            createSocket();

            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                mSocket.connect();
                Log.d(ConstantsUtil.CONNECTION_TAG + " 2", "socket connected");
                listener.onConnectionSuccess(mId);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(ConstantsUtil.CONNECTION_TAG + " 2", "socket connection error", e);
                listener.onConnectionError(mId);
            }
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mSocket.close();
                Log.d(ConstantsUtil.CONNECTION_TAG + " 3", "socket closed");
            } catch (IOException e) {
                Log.d(ConstantsUtil.CONNECTION_TAG + " 3", "socket close error", e);
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

/*    private class DisconnectingThread extends Thread {
        private SocketDisConnectListener mListener;

        public DisconnectingThread(BluetoothSocket mSocket, SocketDisConnectListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void run() {
            try {
                mSocket.close();
            } catch (IOException e) {
                mListener.onDisconnectError(mId);
            }
        }
    }*/

    public class AlreadyConnectedThread extends Thread {

        private InputStream mInnput;
//    private Handler mHandler; // handler that gets info from Bluetooth service


        private MessageReceivedListener mListener;

        public AlreadyConnectedThread(BluetoothSocket socket, MessageReceivedListener listener) {
            mListener = listener;
            mInnput = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.

            try {
                mInnput = socket.getInputStream();
            } catch (IOException e) {
                Log.e(ConstantsUtil.GENERAL_TAG, "Error occurred when creating input stream", e);
            }

        }

        public void run() {
            byte[] buffer = new byte[ConstantsUtil.BUFFER_SIZE];
            int numBytes;

            // Keep listening to the InputStream until an exception occurs.
            while (true) {

                try {
                    // Read from the InputStream.
                    numBytes = mInnput.read(buffer);

                    Log.d(ConstantsUtil.GENERAL_TAG + " 1", "buffer initial: " + buffer);
                    Log.d(ConstantsUtil.GENERAL_TAG + " 1", "numBytes: " + numBytes);

                    mPacketsCounter ++;
                    mListener.onReceived(buffer, numBytes, mPacketsCounter, mId);

                } catch (IOException e) {
                    Log.d(ConstantsUtil.GENERAL_TAG, "Input stream was disconnected", e);
                    break;
                }


                /*try {
                    byte[] initBuffer = new byte[3];

                    // Read from the InputStream.
                    numBytes = mInnput.read(initBuffer);
                    Log.d(ConstantsUtil.GENERAL_TAG, "num first Bytes: " + numBytes);
                    Log.d(ConstantsUtil.GENERAL_TAG, " first buffer: " + ConvertUtil.decimalToHexString(initBuffer));

                    int bytesLeftSize = (int)(initBuffer[2] & 0xFF) + 4;


                    byte[] leftBuffer = new byte[bytesLeftSize];
                    numBytes = mInnput.read(leftBuffer);

                    Log.d(ConstantsUtil.GENERAL_TAG, "num left Bytes: " + numBytes);
                    Log.d(ConstantsUtil.GENERAL_TAG, "second buffer: " + ConvertUtil.decimalToHexString(leftBuffer));

                    byte[] both = new byte[initBuffer.length + leftBuffer.length];
                    System.arraycopy(initBuffer, 0, both, 0, initBuffer.length);
                    System.arraycopy(leftBuffer, 0, both, initBuffer.length, leftBuffer.length);

                    mListener.onReceived(both);

                } catch (IOException e) {
                        Log.d(ConstantsUtil.GENERAL_TAG, "Input stream was disconnected", e);
                    break;
                }*/
            }
        }
    }

    public interface SocketConnectedListener {
        void onConnectionSuccess(int id);

        void onConnectionError(int id);
    }

    public interface SocketDisConnectListener {
        void onDisconnectError(int id);
    }

    public interface MessageSentListener {
        void onError(String error, int id);
    }

    public interface MessageReceivedListener {
        void onReceived(byte[] mBuffer, int numBytes, int packetsCounter, int id);
    }
}
