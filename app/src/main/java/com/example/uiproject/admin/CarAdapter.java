package com.example.uiproject.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiproject.R;
import com.example.uiproject.admin.model.CarDTO;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<CarDTO> carList;

    public CarAdapter(List<CarDTO> carList) {
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
        CarDTO car = carList.get(position);

        holder.carNameTextView.setText(car.getName());

        // Format price to currency
        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(car.getPrice());
        holder.carPriceTextView.setText(formattedPrice);

        // Load image from first picture URL using Glide
        if (car.getPictures() != null && !car.getPictures().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(car.getPictures().get(0))
                    .placeholder(R.drawable.placeholder_image) // Optional placeholder
                    .into(holder.carImageView);
        } else {
            holder.carImageView.setImageResource(R.drawable.placeholder_image); // Default image
        }

        // Optional: handle favorite toggle (if needed)
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            private boolean isFavorite = false;

            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                holder.favoriteButton.setImageResource(
                        isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
                );
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(v.getContext(), com.example.uiproject.admin.EditCarActivity.class);
                intent.putExtra("car", new com.google.gson.Gson().toJson(car));
                v.getContext().startActivity(intent);
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
