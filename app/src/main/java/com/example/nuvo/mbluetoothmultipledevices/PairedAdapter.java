package com.example.nuvo.mbluetoothmultipledevices;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PairedAdapter extends RecyclerView.Adapter<PairedAdapter.CustomViewHolder>{



    public interface SelectListener{
        void onCheck(int position, boolean isChecked);
    }


    private List<String> mDevices;
    private SelectListener mListener;

    public PairedAdapter(List<String> mDevices, SelectListener selectListener) {
        this.mDevices = mDevices;
        this.mListener = selectListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paired_adapter_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final String device = mDevices.get(position);
        holder.mName.setText(device);
        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mListener.onCheck(position, isChecked);
        });

      /*  holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCheck(position);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
//        protected TextView name;
        @BindView(R.id.name) TextView mName;
        @BindView(R.id.checkBox) CheckBox mCheckBox;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
