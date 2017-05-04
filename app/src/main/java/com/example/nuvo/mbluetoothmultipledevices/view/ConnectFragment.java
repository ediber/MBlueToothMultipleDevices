package com.example.nuvo.mbluetoothmultipledevices.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nuvo.mbluetoothmultipledevices.R;
import com.example.nuvo.mbluetoothmultipledevices.databinding.FragmentConnectBinding;
import com.example.nuvo.mbluetoothmultipledevices.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.nuvo.mbluetoothmultipledevices.view.ConnectFragmentParent.State.DISCONNECTED;
import static com.example.nuvo.mbluetoothmultipledevices.view.ConnectFragmentParent.State.STARTED;
import static com.example.nuvo.mbluetoothmultipledevices.view.ConnectFragmentParent.State.STOPPED;


public class ConnectFragment extends Fragment {


    public static final String NAME = "name";
    public static final String ID = "id";

    private OnFragmentInteractionListener mListener;
    private String mName;
    private int mId;

    @BindView(R.id.connectName)
    TextView mNameView;

    @BindView(R.id.connectVersion)
    TextView mVersionView;

    @BindView(R.id.select)
    View mConnect;

    @BindView(R.id.dissconect)
    View mDissconect;

    @BindView(R.id.start)
    View mStart;

    @BindView(R.id.stop)
    View mStop;

    @BindView(R.id.level)
    Spinner mSpinnerLevel;

    @BindView(R.id.current)
    Spinner mSpinnerCurrent;

    @BindView(R.id.indicatorsLayoutPositive)
    LinearLayout mIndicatorsLayoutPositive;

    @BindView(R.id.indicatorsLayoutNegative)
    LinearLayout mIndicatorsLayoutNegative;

    @BindView(R.id.progressBar1)
    View mProgress;

    @BindView(R.id.recievedPackets)
    TextView mRecievedPackets;

    @BindView(R.id.effectiveTransferRate)
    TextView mEffectiveTransferRate;

    @BindView(R.id.startTime)
    TextView mStartTime;

    @BindView(R.id.elapsedTime)
    TextView mElapsedTime;



    public ConnectFragment() {
        // Required empty public constructor
    }

    public static ConnectFragment newInstance() {
        ConnectFragment fragment = new ConnectFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(NAME);
            mId = getArguments().getInt(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_connect, container, false);

        FragmentConnectBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connect, container, false);
        binding.setViewModel(MainViewModel.getInstance(null, null));
        View view = binding.getRoot();


        ButterKnife.bind(this, view);

        mNameView.setText(mName);

        setSpinners();

        mConnect.setOnClickListener((View v) -> {
            toShowProgress(true);
            mListener.onConnect(mId);
        });

        mDissconect.setOnClickListener((View v) -> {
            mListener.onDisConnect(mId);
            setUIState(DISCONNECTED);
        });

        mStart.setOnClickListener((View v) -> {
            mListener.onStartRequest(mSpinnerLevel.getSelectedItemPosition(), mSpinnerCurrent.getSelectedItemPosition(), mId);
            setUIState(STARTED);
        });

        mStop.setOnClickListener((View v) -> {
            mListener.onStopRequest(mId);
            setUIState(STOPPED);
        });

        setUIState(DISCONNECTED);

        return view;
    }

    public void setListener(OnFragmentInteractionListener listener) {
        this.mListener = listener;
    }

    public void updateUILOD(String hex, String binary, ArrayList<Integer> packetCounters, ArrayList<String> transferRates,
                            ArrayList<String> startTimes, ArrayList<String> elapsedTimes) {
        for (int i = 0; i < binary.length() / 2; i++) {
            if (binary.charAt(i) == '0') { // attached
                mIndicatorsLayoutPositive.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_green));
            } else {
                mIndicatorsLayoutPositive.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_red));
            }
        }

        int layoutIndex = 0;
        for (int i = binary.length() / 2; i < binary.length(); i++) {
            if (binary.charAt(i) == '0') { // attached
                mIndicatorsLayoutNegative.getChildAt(layoutIndex).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_green));
            } else {
                mIndicatorsLayoutNegative.getChildAt(layoutIndex).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_red));
            }

            layoutIndex++;
        }

        mRecievedPackets.setText(packetCounters.get(mId) + "");
        mEffectiveTransferRate.setText(transferRates.get(mId));
        mStartTime.setText(startTimes.get(mId));
        mElapsedTime.setText(elapsedTimes.get(mId));
    }

    public void updateUIVersion(String hex, String payload) {
        mVersionView.setText(hex);
    }

    private void setSpinners() {
        // set spinners
        List<String> levelsLst = new ArrayList(Arrays.asList("P_95_N_5", "P_92_5_N_7_5", "P_90_N_10", "P_87_5_N_12_5", "P_85_N_15", "P_80_N_20", "P_75_N_25", "P_70_N_30"));
        ArrayAdapter<String> levelArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levelsLst); //selected item will look like a spinner set from XML
        levelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLevel.setAdapter(levelArrayAdapter);

        List currentLst = new ArrayList(Arrays.asList("CURRENT_6_NA", "CURRENT_24_NA", "CURRENT_6_UA", "CURRENT_24_UA"));
        ArrayAdapter<String> currentArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, currentLst); //selected item will look like a spinner set from XML
        currentArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrent.setAdapter(currentArrayAdapter);
    }

    public void setUIState(ConnectFragmentParent.State state) {
        switch (state){
            case DISCONNECTED:
                mConnect.setEnabled(true);
                mStart.setEnabled(false);
                mDissconect.setEnabled(false);
                mStop.setEnabled(false);
                mSpinnerLevel.setEnabled(false);
                mSpinnerCurrent.setEnabled(false);
                setNeutrall(mIndicatorsLayoutPositive);
                setNeutrall(mIndicatorsLayoutNegative);
                break;

            case CONNECTED:
                mConnect.setEnabled(false);
                mStart.setEnabled(true);
                mDissconect.setEnabled(true);
                mStop.setEnabled(false);
                mSpinnerLevel.setEnabled(true);
                mSpinnerCurrent.setEnabled(true);

                toShowProgress(false);
                break;

            case STARTED:
                mConnect.setEnabled(false);
                mStart.setEnabled(false);
                mDissconect.setEnabled(false);
                mStop.setEnabled(true);
                mSpinnerLevel.setEnabled(false);
                mSpinnerCurrent.setEnabled(false);
                break;

            case STOPPED:
                mConnect.setEnabled(false);
                mStart.setEnabled(true);
                mDissconect.setEnabled(true);
                mStop.setEnabled(false);
                mSpinnerLevel.setEnabled(true);
                mSpinnerCurrent.setEnabled(true);
                setNeutrall(mIndicatorsLayoutPositive);
                setNeutrall(mIndicatorsLayoutNegative);
                break;
        }
    }

    private void toShowProgress(boolean show) {
        if(show){
            mProgress.setVisibility(View.VISIBLE);
            mProgress.bringToFront();
        } else { // hide
            mProgress.setVisibility(View.GONE);
        }
    }

    private void setNeutrall(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            layout.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_blue));
        }
    }

    @Override
    public void onStop() {
        mListener.onStopRequest(mId);
        mListener.onDisConnect(mId);
        setUIState(DISCONNECTED);
        super.onStop();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onConnect(int id);

        void onDisConnect(int id);

        void onStartRequest(int selectedLevel, int selectedCurrent, int id);

        void onStopRequest(int id);
    }

/*    public enum State{
        DISCONNECTED,
        CONNECTED,
        STARTED,
        STOPPED
    }*/

}
