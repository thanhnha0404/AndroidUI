package com.example.uiproject.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Car> carList;

    public CarAdapter(List<Car> carList) {
        this.carList = carList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        
        holder.carNameTextView.setText(car.getName());
        holder.carPriceTextView.setText(car.getPrice());
        holder.carImageView.setImageResource(car.getImageResource());


        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newFavoriteState = !car.isFavorite();
                car.setFavorite(newFavoriteState);
                
                if (newFavoriteState) {
                    holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                } else {
                    holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImageView;
        TextView carNameTextView, carPriceTextView;
        ImageButton favoriteButton;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImageView = itemView.findViewById(R.id.carImageView);
            carNameTextView = itemView.findViewById(R.id.carNameTextView);
            carPriceTextView = itemView.findViewById(R.id.carPriceTextView);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }
} 