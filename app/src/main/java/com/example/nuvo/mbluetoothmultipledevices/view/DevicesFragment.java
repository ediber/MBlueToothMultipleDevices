package com.example.nuvo.mbluetoothmultipledevices.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nuvo.mbluetoothmultipledevices.PairedAdapter;
import com.example.nuvo.mbluetoothmultipledevices.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DevicesFragment extends Fragment {

    public static final String NAMES = "mNames";
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.devicesRecycler)
    RecyclerView recycler;
    @BindView(R.id.show)
    View mShow;

    private PairedAdapter mAdapter;
    private String[] mNames;

    public DevicesFragment() {
        // Required empty public constructor
    }

    public static DevicesFragment newInstance(String param1, String param2) {
        DevicesFragment fragment = new DevicesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNames = getArguments().getStringArray(NAMES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, view);


        ArrayList<String> namesList = new ArrayList<>(Arrays.asList(mNames));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycler.getContext(), linearLayoutManager.getOrientation());
        recycler.addItemDecoration(dividerItemDecoration);
        recycler.setLayoutManager(linearLayoutManager);
        mAdapter = new PairedAdapter(namesList, new SelectListen());
        recycler.setAdapter(mAdapter);

        mShow.setOnClickListener(v -> {
            mListener.onShow();
        });

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDeviceChecked(int position, boolean isChecked);
        void onShow();
    }

    private class SelectListen implements PairedAdapter.SelectListener {
        @Override
        public void onCheck(int position, boolean isChecked) {
            mListener.onDeviceChecked(position, isChecked);
        }
    }
}
