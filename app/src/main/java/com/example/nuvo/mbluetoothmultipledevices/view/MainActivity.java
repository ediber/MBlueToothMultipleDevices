package com.example.nuvo.mbluetoothmultipledevices.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.nuvo.mbluetoothmultipledevices.R;
import com.example.nuvo.mbluetoothmultipledevices.utils.FragmentSwapper;
import com.example.nuvo.mbluetoothmultipledevices.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity implements DevicesFragment.OnFragmentInteractionListener, ConnectFragmentParent.OnFragmentInteractionListener {

    @BindView(R.id.frame)
    FrameLayout mFrame;
    @BindView(R.id.progressBar)
    View mProgress;

    private FragmentSwapper mFragmentSwapper;
    private MainViewModel mMainViewModel;
    private ConnectFragmentParent mConnectFragmentParent;
    private List<Integer> mSelectedIndexes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMainViewModel = MainViewModel.getInstance(this, new viewModelListen());

        // MainViewModel is singelton and getInstance may not create ne instance with current params
        mMainViewModel.setActivity(this);
        mMainViewModel.setListener(new viewModelListen());

        mFragmentSwapper = FragmentSwapper.getInstance(getSupportFragmentManager());
        mSelectedIndexes = new ArrayList<>();

        mMainViewModel.onCreate();
    }

    @Override
    public void onDeviceChecked(int selectedDevice, boolean isChecked) {
//        mMainViewModel.onDeviceSelected(selectedDevice);
        if(isChecked){
            mSelectedIndexes.add(selectedDevice);
        } else{ //  remove
            mSelectedIndexes.remove(new Integer(selectedDevice));
        }
    }

    @Override
    public void onShow() {
        mMainViewModel.onShow(mSelectedIndexes);
    }


    @Override
    public void onConnect(int id) {
        mProgress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        mMainViewModel.connect(id);
    }

    @Override
    public void onDisConnect(int id) {
        mMainViewModel.disConnect(id);
    }

    @Override
    public void onStartRequest(int level, int current, int id) {
        mMainViewModel.start(level, current, id);
    }

    @Override
    public void onStopRequest(int id) {
        mMainViewModel.stop(id);
    }



    private class viewModelListen implements MainViewModel.viewModelListener {

        @Override
        public void onShowPairedDevices(List<String> deviceNames) {
            Bundle bundle = new Bundle();
            bundle.putStringArray(DevicesFragment.NAMES, deviceNames.toArray(new String[0]));
            mFragmentSwapper.swapToFragment(DevicesFragment.class, bundle, R.id.frame, false, true);
        }

        @Override
        public void showDeviceDetails(String deviceName, int id) {
            Bundle bundle = new Bundle();
            bundle.putString(ConnectFragmentParent.NAME1, deviceName);
           mConnectFragmentParent = (ConnectFragmentParent) mFragmentSwapper.swapToFragment(ConnectFragmentParent.class, bundle, R.id.frame, true, true);
//            mFragmentSwapper.addInitialFragment(mConnectFragmentParent, bundle, R.id.mFrame, true, CONNECT_FRAGMENT);
        }

        @Override
        public void onConnectionSuccess() {
            String message = "connection sucess";
            ToastOnUIThread(message);
            mMainViewModel.showVersion(id);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    mConnectFragmentParent.setUIState(ConnectFragmentParent.State.CONNECTED);
                }
            });

        }

        @Override
        public void onConnectionError() {
            String message = "connection failed";
            ToastOnUIThread(message);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });

        }

        @Override
        public void onDisconnectFailed() {
            ToastOnUIThread("diconnection failed");
        }

        @Override
        public void onUpdateUIFromLOD(String hex, String binary) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mConnectFragmentParent.updateUILOD(hex, binary, id);
                }
            });

        }

        @Override
        public void onUpdateUIFromVersion(String hex, String payload) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mConnectFragmentParent.updateUIVersion(hex, payload);
                }
            });
        }

        @Override
        public void onStopError(String error) {
            MainActivity.this.runOnUiThread(() -> {
                mConnectFragmentParent.setUIState(ConnectFragmentParent.State.STARTED);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            });
        }

        @Override
        public void onStartError(String error) {
            MainActivity.this.runOnUiThread(() -> {
                mConnectFragmentParent.setUIState(ConnectFragmentParent.State.CONNECTED);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            });
        }
    }

    private void ToastOnUIThread(String message) {
        MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == -1 || requestCode == 1){ // allowed
            mMainViewModel.showPairedDevices();
        }
    }
}
