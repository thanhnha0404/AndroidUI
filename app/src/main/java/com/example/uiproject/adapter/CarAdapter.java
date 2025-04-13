package com.example.uiproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiproject.R;
import com.example.uiproject.entity.CarDTO;
import com.example.uiproject.model.Car;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<CarDTO> carList;
    private OnCarClickListener listener;

    public interface OnCarClickListener {
        void onCarClick(int position);
        void onFavoriteClick(int position);
    }

    public CarAdapter(List<CarDTO> carList, OnCarClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarDTO car = carList.get(position);

        holder.carNameTextView.setText(car.getName());
        holder.priceTextView.setText("Fee: " +  car.getPrice().toString() + "/day");
        // Load ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(car.getPictures().get(0))         // hoặc brand.getLogo() nếu là URL ảnh
                .placeholder(R.drawable.car_background) // ảnh tạm thời khi đang load
                .error(R.drawable.car_background)       // ảnh lỗi nếu load thất bại
                .into(holder.carImageView);             // ImageView bạn muốn load vào

        // Set favorite icon based on car favorite status
        if (car.isFavorite()) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public int getItemCount() {
        return (carList != null ? carList.size() : 0);
    }

    public class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImageView;
        TextView carNameTextView, priceTextView;
        ImageButton favoriteButton;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            
            carImageView = itemView.findViewById(R.id.carImageView);
            carNameTextView = itemView.findViewById(R.id.carNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onCarClick(getAdapterPosition());
                }
            });
            
            favoriteButton.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onFavoriteClick(getAdapterPosition());
                }
            });
        }
    }

} 