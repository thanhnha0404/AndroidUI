package com.example.uiproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;

public class CarShimerAdapter extends RecyclerView.Adapter<CarShimerAdapter.ShimerViewHolder> {

    int shimerItemCount = 5;

    @NonNull
    @Override
    public CarShimerAdapter.ShimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle_shimmer, parent, false);
        return new CarShimerAdapter.ShimerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarShimerAdapter.ShimerViewHolder holder, int position) {
        // khong can gan du lieu gi ca
    }

    @Override
    public int getItemCount() {
        return shimerItemCount;
    }

    static class ShimerViewHolder extends  RecyclerView.ViewHolder {

        public ShimerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
