package com.example.uiproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.uiproject.R;
import com.example.uiproject.entity.CarDTO;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private Context context;
    private List<CarDTO> vehicleList;
    private OnVehicleClickListener listener;

    public interface OnVehicleClickListener {
        void onVehicleClick(int position);
    }

    public VehicleAdapter(Context context, List<CarDTO> vehicleList) {
        this.context = context;
        this.vehicleList = vehicleList;
    }

    public void setOnVehicleClickListener(OnVehicleClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vehicle_item, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        CarDTO vehicle = vehicleList.get(position);


        holder.vehicleName.setText(vehicle.getName());

        String discount = "";
        // Set the vehicle data
        if (vehicle.getDiscount() != null && !vehicle.getDiscount().equals("")){
            discount = "Sale: " + vehicle.getDiscount() + "%";
        }
        holder.vehicleSale.setText(discount);

        // Load ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(vehicle.getPictures().get(0)) // hoặc brand.getLogo() nếu là URL ảnh
                .placeholder(R.drawable.car_background) // ảnh tạm thời khi đang load
                .error(R.drawable.car_background)       // ảnh lỗi nếu load thất bại
                .into(holder.vehicleImage);             // ImageView bạn muốn load vào
        
        // Show or hide the selected indicator
        if (vehicle.isSelected()) {
            holder.selectedIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.selectedIndicator.setVisibility(View.INVISIBLE);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // Update selection
            for (int i = 0; i < vehicleList.size(); i++) {
                vehicleList.get(i).setSelected(i == position);
            }
            
            // Notify adapter of changes
            notifyDataSetChanged();
            
            // Trigger listener if set
            if (listener != null) {
                listener.onVehicleClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
       return (vehicleList != null ? vehicleList.size() : 0);
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView vehicleImage;
        TextView vehicleName,vehicleSale;
        View selectedIndicator;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleImage = itemView.findViewById(R.id.iv_vehicle);
            vehicleName = itemView.findViewById(R.id.tv_vehicle_name);
            vehicleSale = itemView.findViewById(R.id.tv_vehicle_sale);
            selectedIndicator = itemView.findViewById(R.id.selected_indicator);
        }
    }
} 