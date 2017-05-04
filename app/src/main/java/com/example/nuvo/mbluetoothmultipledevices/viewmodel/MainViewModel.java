package com.example.nuvo.mbluetoothmultipledevices.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.util.Log;

import com.example.nuvo.mbluetoothmultipledevices.model.BTConnector;
import com.example.nuvo.mbluetoothmultipledevices.model.LODSendMessage;
import com.example.nuvo.mbluetoothmultipledevices.model.ReceiveMessage;
import com.example.nuvo.mbluetoothmultipledevices.model.VersionSendMessage;
import com.example.nuvo.mbluetoothmultipledevices.utils.ConstantsUtil;
import com.example.nuvo.mbluetoothmultipledevices.utils.ConvertUtil;
import com.example.nuvo.mbluetoothmultipledevices.utils.DateUtil;
import com.example.nuvo.mbluetoothmultipledevices.utils.ParseUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.example.nuvo.mbluetoothmultipledevices.utils.DateUtil.dateToStr;
import static com.example.nuvo.mbluetoothmultipledevices.utils.DateUtil.getCurrentDate;


public class MainViewModel extends BaseObservable implements ViewModel {

    private static MainViewModel mInstance = null;

    private Activity mActivity;
    private viewModelListener mListener;
    private ArrayList<BTConnector> mConnectors;

    private ArrayList<Integer> mPacketCounters;
    private ArrayList<String> mStartTimes;
    private ArrayList<String> mElapsedTimes;
    private ArrayList<Date> mDates;
    private ArrayList<String> mTransferRates;



    private MainViewModel(Activity activity, viewModelListener listener) {
        mActivity = activity;
        mListener = listener;

        // add 2 garbage values to avoid exception
        mPacketCounters = new ArrayList<>(Arrays.asList(-99, -99));
        mStartTimes = new ArrayList<>(Arrays.asList("-99", "-99"));
        mElapsedTimes = new ArrayList<>(Arrays.asList("-99", "-99"));
        mDates = new ArrayList<>(Arrays.asList(new Date(), new Date()));
        mTransferRates = new ArrayList<>(Arrays.asList("-99", "-99"));
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

//    @Bindable
//    public String getPacketCounter() {
//        return mPacketCounters + "";
//    }

    public void setPacketCounter(String packetCounter, int id) {
        this.mPacketCounters.set(id, Integer.parseInt(packetCounter));
//        notifyPropertyChanged(BR.packetCounter);
    }

//    @Bindable
//    public String getStartTime() {
//        return mStartTimes;
//    }

    public void setStartTime(String startTime, int id) {
        this.mStartTimes.set(id, startTime);
//        notifyPropertyChanged(BR.startTime);
    }

/*    @Bindable
    public String getElapsedTime() {
        return mElapsedTimes;
    }*/

    public void setElapsedTime(String elapsedTime, int id) {
        this.mElapsedTimes.set(id, elapsedTime);
//        notifyPropertyChanged(BR.elapsedTime);
    }

   /* @Bindable
    public String getTransferRate() {
        return mTransferRates;
    }
*/
    public void setTransferRate(String transferRate, int id) {
        this.mTransferRates.set(id, transferRate);
//        notifyPropertyChanged(BR.transferRate);
    }


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
        setPacketCounter("0", id);
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

        initializeStartTime(id);
    }

    private void initializeStartTime(int id) {
        mDates.set(id, getCurrentDate());
        setStartTime(dateToStr(mDates.get(id), "HH:mm:ss"), id);
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
                    setPacketCounter(packetsCounter + "", id);
                    long diffLong = DateUtil.differenceBetweenDatesLong(mDates.get(id), getCurrentDate());
                    String diffStr = DateUtil.longToStr(diffLong, "mm:ss");
                    setElapsedTime(diffStr, id);
                    double transferRate = (double) mPacketCounters.get(id) / diffLong * 1000;
                    setTransferRate(String.format("%.1f", transferRate), id);
                    mListener.onUpdateUIFromLOD(hex, payload, id, mPacketCounters, mTransferRates, mStartTimes, mElapsedTimes);
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

        void onUpdateUIFromLOD(String hex, String binary, int id, ArrayList<Integer> packetCounters, ArrayList<String> transferRates,
                               ArrayList<String> startTimes, ArrayList<String> elapsedTimes);

        void onUpdateUIFromVersion(String hex, String payload, int id);

        void onStopError(String error, int id);

        void onStartError(String error, int id);
    }


}
