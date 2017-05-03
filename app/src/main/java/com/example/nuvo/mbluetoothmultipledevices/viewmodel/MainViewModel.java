package com.example.nuvo.mbluetoothmultipledevices.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.example.nuvo.mbluetoothmultipledevices.BR;
import com.example.nuvo.mbluetoothmultipledevices.model.BTConnector;
import com.example.nuvo.mbluetoothmultipledevices.model.LODSendMessage;
import com.example.nuvo.mbluetoothmultipledevices.model.ReceiveMessage;
import com.example.nuvo.mbluetoothmultipledevices.model.VersionSendMessage;
import com.example.nuvo.mbluetoothmultipledevices.utils.ConstantsUtil;
import com.example.nuvo.mbluetoothmultipledevices.utils.ConvertUtil;
import com.example.nuvo.mbluetoothmultipledevices.utils.DateUtil;
import com.example.nuvo.mbluetoothmultipledevices.utils.ParseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.nuvo.mbluetoothmultipledevices.utils.DateUtil.dateToStr;
import static com.example.nuvo.mbluetoothmultipledevices.utils.DateUtil.getCurrentDate;


public class MainViewModel extends BaseObservable implements ViewModel {

    private static MainViewModel mInstance = null;

    private Activity mActivity;
    private viewModelListener mListener;
//    private BTConnector mConnector0;
//    private BTConnector mConnector1;
    private int mPacketCounter;
    private String mStartTime;
    private String mElapsedTime;
    private Date mDate;
    private String mTransferRate;
    private ArrayList<BTConnector> mConnectors;


    private MainViewModel(Activity activity, viewModelListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    public static MainViewModel getInstance(Activity activity, viewModelListener listener) {
        if(mInstance == null) {
            mInstance = new MainViewModel(activity, listener);
        }
        return mInstance;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setListener(viewModelListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onCreate() {
        // Get the local Bluetooth adapter
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        BTConnector mConnector0 = new BTConnector(0);
        BTConnector mConnector1 = new BTConnector(1);
        mConnectors = new ArrayList<BTConnector>();
        mConnectors.add(mConnector0);
        mConnectors.add(mConnector1);

        showPairedDevices();
        new ConvertUtil(); // call constructor to initialize ConvertUtil inner list of binaries
    }

    public void showPairedDevices() {
        mConnectors.get(0).showPairedDevicesIfBTEnabled(mActivity, mListener);
        mConnectors.get(1).showPairedDevicesIfBTEnabled(mActivity, null); // to initialize paired devices list in BTConnector
    }

    @Bindable
    public String getPacketCounter() {
        return mPacketCounter + "";
    }

    public void setPacketCounter(String packetCounter) {
        this.mPacketCounter = Integer.parseInt(packetCounter);
        notifyPropertyChanged(BR.packetCounter);
    }

    @Bindable
    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
        notifyPropertyChanged(BR.startTime);
    }

    @Bindable
    public String getElapsedTime() {
        return mElapsedTime;
    }

    public void setElapsedTime(String mElapsedTime) {
        this.mElapsedTime = mElapsedTime;
        notifyPropertyChanged(BR.elapsedTime);
    }

    @Bindable
    public String getTransferRate() {
        return mTransferRate;
    }

    public void setTransferRate(String mTransferRate) {
        this.mTransferRate = mTransferRate;
        notifyPropertyChanged(BR.transferRate);
    }

    /*public void onDeviceSelected(int position) {
        mConnector0.setSelectedDevice(position);
        mListener.showDeviceDetails(mConnector0.getSelectedName(), mConnector1.getSelectedName());
    }*/

    public void onShow(List<Integer> mSelectedIndexes) {
        String deviceName0 = "";
        String deviceName1 = "";
        mConnectors.get(0).setSelectedDevice(mSelectedIndexes.get(0));
        deviceName0 = mConnectors.get(0).getSelectedName();

        if(mConnectors.size() == 2){   // 2 BT devices selected
            mConnectors.get(1).setSelectedDevice(mSelectedIndexes.get(1));
            deviceName1 = mConnectors.get(1).getSelectedName();
        }
        mListener.showDeviceDetails(deviceName0, deviceName1);

    }

    public void connect(int id) {
        mConnectors.get(id).connect(new BTConnector.SocketConnectedListener() {
            @Override
            public void onConnectionSuccess(int id) {
                mListener.onConnectionSuccess(id);
            }

            @Override
            public void onConnectionError(int id) {
                mListener.onConnectionError(id);
            }
        });

    }

    public void disConnect(int id) {
        setPacketCounter("0");
        mConnectors.get(id).disconnect(new BTConnector.SocketDisConnectListener() {
            @Override
            public void onDisconnectError(int id) {
                mListener.onDisconnectFailed(id);
            }
        });
    }

    public void start(int level, int current, int id) {
        LODSendMessage sendMessage = new LODSendMessage();
        sendMessage.setStart();
        sendMessage.addPayload(level);
        sendMessage.addPayload(current);

        mConnectors.get(id).send(sendMessage, new BTConnector.MessageSentListener() {
            @Override
            public void onError(String error, int id) {
                mListener.onStartError(error, id);
            }
        });

        mConnectors.get(id).listenToIncomingMessages((buffer, numBytes, packetsCounter, id1) -> {
            parseMessage(buffer, numBytes, packetsCounter, id1);
        });

        initializeStartTime();
    }

    private void initializeStartTime() {
        mDate = getCurrentDate();
        setStartTime(dateToStr(mDate, "HH:mm:ss"));
    }

    private void parseMessage(byte[] buffer, int numBytes, int packetsCounter, int id) {
        List<Integer> positiveBuffer;
        positiveBuffer = ConvertUtil.bytesToPositive(buffer);

        List<ReceiveMessage> messages = ParseUtil.bufferToPackets(positiveBuffer, numBytes);

        Log.d(ConstantsUtil.GENERAL_TAG, "  ");


        for (ReceiveMessage message : messages) {
            final String hex = message.toHexa(); // just for test TODO remove later
            String payload = message.getPayloadInBinary();

            switch (message.getmType()) {
                case LOD:
//                Log.d(ConstantsUtil.GENERAL_TAG, " message: " + ConvertUtil.decimalToHexString(message));
                    setPacketCounter(packetsCounter + "");
                    long diffLong = DateUtil.differenceBetweenDatesLong(mDate, getCurrentDate());
                    String diffStr = DateUtil.longToStr(diffLong, "mm:ss");
                    setElapsedTime(diffStr);
                    double transferRate = (double)mPacketCounter / diffLong * 1000;
                    setTransferRate(String.format("%.1f", transferRate));
                    mListener.onUpdateUIFromLOD(hex, payload, id);
                    break;

                case VERSION:
                    mListener.onUpdateUIFromVersion(hex, payload, id);
                    break;
            }

        }
    }


    public void stop(int id) {
        LODSendMessage message = new LODSendMessage();
        message.setStop();
        mConnectors.get(id).send(message, new BTConnector.MessageSentListener() {
            @Override
            public void onError(String error, int id) {
                mListener.onStopError(error, id);
            }
        });
        mConnectors.get(id).clearPacketsCounter();
    }

    public void showVersion(int id) {
        VersionSendMessage message = new VersionSendMessage();
        message.setStart();
        mConnectors.get(id).send(message, new BTConnector.MessageSentListener() {
            @Override
            public void onError(String error, int id) {

            }
        });

        mConnectors.get(id).listenToIncomingMessages((buffer, numBytes, packetsCounter, id1) -> {
            parseMessage(buffer, numBytes, packetsCounter, id1);
        });
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }


    public interface viewModelListener {
        void onShowPairedDevices(List<String> deviceNames);

        void showDeviceDetails(String deviceName0, String deviceName1);

        void onConnectionSuccess(int id);

        void onConnectionError(int id);

        void onDisconnectFailed(int id);

        void onUpdateUIFromLOD(String hex, String binary, int id);

        void onUpdateUIFromVersion(String hex, String payload, int id);

        void onStopError(String error, int id);

        void onStartError(String error, int id);
    }


}
