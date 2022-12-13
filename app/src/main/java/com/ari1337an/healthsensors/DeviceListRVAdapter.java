package com.ari1337an.healthsensors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeviceListRVAdapter extends RecyclerView.Adapter<DeviceListRVAdapter.DeviceListViewHolder> {

    ArrayList<Device> dataset;

    DeviceListRVAdapter(ArrayList<Device> datasetList){
        dataset = datasetList;
    }

    public static class DeviceListViewHolder extends RecyclerView.ViewHolder{
        private final TextView deviceName;

        public DeviceListViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
        }

        public TextView getDeviceName() {
            return deviceName;
        }
    }

    @NonNull
    @Override
    public DeviceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_item, parent, false);
        return new DeviceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListViewHolder holder, int position) {
        holder.getDeviceName().setText(dataset.get(position).devicename);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addNewDevice(Device newDevice){
        this.dataset.add(newDevice);
        notifyDataSetChanged();
    }
}
