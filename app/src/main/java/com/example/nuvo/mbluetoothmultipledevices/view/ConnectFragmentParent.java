package com.example.nuvo.mbluetoothmultipledevices.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nuvo.mbluetoothmultipledevices.R;
import com.example.nuvo.mbluetoothmultipledevices.utils.FragmentSwapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static com.example.nuvo.mbluetoothmultipledevices.view.ConnectFragmentParent.State.DISCONNECTED;


public class ConnectFragmentParent extends Fragment {


    public static final String NAME1 = "name";

    private OnFragmentInteractionListener mListener;
    private String mName;
    private State mState;
    private FragmentSwapper mFragmentSwapper;
    private List<ConnectFragment> mFragments;

    public ConnectFragmentParent() {
        // Required empty public constructor
    }

    public static ConnectFragmentParent newInstance() {
        ConnectFragmentParent fragment = new ConnectFragmentParent();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(NAME1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.parent_fragment_connect, container, false);

        ButterKnife.bind(this, view);

        mFragments = new ArrayList<>();
        mFragmentSwapper = FragmentSwapper.getInstance(getChildFragmentManager());

        Bundle bundle0 = new Bundle();
        bundle0.putString(ConnectFragment.NAME, "device 0");
        bundle0.putInt(ConnectFragment.ID, 0);
        ConnectFragment connectFragment0 = (ConnectFragment) mFragmentSwapper.swapToFragment(ConnectFragment.class, bundle0, R.id.frame0, true, true);

        Bundle bundle1 = new Bundle();
        bundle1.putString(ConnectFragment.NAME, "device 1");
        bundle1.putInt(ConnectFragment.ID, 1);
        ConnectFragment connectFragment1 = (ConnectFragment) mFragmentSwapper.swapToFragment(ConnectFragment.class, bundle1, R.id.frame1, true, true);

        mFragments.add(connectFragment0);
        mFragments.add(connectFragment1);

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateUILOD(String hex, String binary, int id) {

    }

    public void updateUIVersion(String hex, String payload) {
    }



    public void setUIState(State state) {

    }



    @Override
    public void onStop() {
        mListener.onStopRequest(0);
        mListener.onDisConnect(0);
        mListener.onStopRequest(1);
        mListener.onDisConnect(1);
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

    public enum State{
        DISCONNECTED,
        CONNECTED,
        STARTED,
        STOPPED
    }

}
