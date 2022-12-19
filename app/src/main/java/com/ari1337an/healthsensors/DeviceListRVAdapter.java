package com.ari1337an.healthsensors;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeviceListRVAdapter extends RecyclerView.Adapter<DeviceListRVAdapter.DeviceListViewHolder> {

    ArrayList<Device> dataset;
    ItemsClicked itemsClicked;

    DeviceListRVAdapter(ArrayList<Device> datasetList, ItemsClicked ic){
        dataset = datasetList;
        itemsClicked = ic;
    }

    public static class DeviceListViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceName;
        private final TextView macAddress;

        public DeviceListViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            macAddress = itemView.findViewById(R.id.macAddress);
        }

        public TextView getDeviceName() {
            return deviceName;
        }
        public TextView getMacAddress() { return macAddress; }
    }

    @NonNull
    @Override
    public DeviceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_item, parent, false);
        DeviceListViewHolder viewHolder = new DeviceListViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsClicked.onClickedItem(dataset.get(viewHolder.getAdapterPosition()));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListViewHolder holder, int position) {
        holder.getDeviceName().setText(dataset.get(position).devicename);
        holder.getMacAddress().setText(dataset.get(position).macaddress);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}

interface ItemsClicked {
    void onClickedItem(Device d);
}
