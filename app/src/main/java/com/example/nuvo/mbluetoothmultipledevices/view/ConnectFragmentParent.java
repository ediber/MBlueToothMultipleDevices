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

import static android.R.attr.id;
import static com.example.nuvo.mbluetoothmultipledevices.view.ConnectFragmentParent.State.DISCONNECTED;


public class ConnectFragmentParent extends Fragment {


    public static final String NAME0 = "name0";
    public static final String NAME1 = "name1";

    private OnFragmentInteractionListener mListener;
    private String mName0;
    private State mState;
    private FragmentSwapper mFragmentSwapper;
    private List<ConnectFragment> mFragments;
    private String mName1;

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
            mName0 = getArguments().getString(NAME0);
            mName1 = getArguments().getString(NAME1);
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
        bundle0.putString(ConnectFragment.NAME, mName0);
        bundle0.putInt(ConnectFragment.ID, 0);
        ConnectFragment connectFragment0 = (ConnectFragment) mFragmentSwapper.swapToFragment(ConnectFragment.class, bundle0, R.id.frame0, true, true);
        connectFragment0.setListener(new ChildFragmentListener());

        Bundle bundle1 = new Bundle();
        bundle1.putString(ConnectFragment.NAME, mName1);
        bundle1.putInt(ConnectFragment.ID, 1);
        ConnectFragment connectFragment1 = (ConnectFragment) mFragmentSwapper.swapToFragment(ConnectFragment.class, bundle1, R.id.frame1, true, true);
        connectFragment1.setListener(new ChildFragmentListener());

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
        mFragments.get(id).updateUILOD(hex, binary);
    }

    public void updateUIVersion(String hex, String payload, int id) {
        mFragments.get(id).updateUIVersion(hex, payload);
    }



    public void setUIState(State state, int id) {
        mFragments.get(id).setUIState(state);
    }



    @Override
    public void onStop() {
        mListener.onStopRequest(0);
        mListener.onDisConnect(0);
        mListener.onStopRequest(1);
        mListener.onDisConnect(1);
        setUIState(DISCONNECTED, id);
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

    private class ChildFragmentListener implements ConnectFragment.OnFragmentInteractionListener {

        @Override
        public void onConnect(int id) {
            mListener.onConnect(id);
        }

        @Override
        public void onDisConnect(int id) {
            mListener.onDisConnect(id);
        }

        @Override
        public void onStartRequest(int selectedLevel, int selectedCurrent, int id) {
            mListener.onStartRequest( selectedLevel,  selectedCurrent,  id);
        }

        @Override
        public void onStopRequest(int id) {
            mListener.onStopRequest(id);
        }
    }
}
