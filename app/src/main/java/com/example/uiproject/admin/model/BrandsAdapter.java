package com.example.uiproject.admin.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.uiproject.R;

import java.util.ArrayList;
import java.util.List;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.BrandViewHolder> {

    private List<Brand> brandList = new ArrayList<>();
    private final Context context;
    private OnBrandClickListener listener;

    public interface OnBrandClickListener {
        void onDetailClick(Brand brand, int position);
        void onItemClick(Brand brand, int position);
    }

    public BrandsAdapter(Context context) {
        this.context = context;
    }

    public void setOnBrandClickListener(OnBrandClickListener listener) {
        this.listener = listener;
    }

    public void setBrands(List<Brand> brands) {
        this.brandList = brands;
        notifyDataSetChanged();
    }

    public void filterList(List<Brand> filteredList) {
        this.brandList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brandList.get(position);
        holder.brandName.setText(brand.getName());

        holder.brandDescription.setText(brand.getDescription());

        // Load brand logo using Glide
        if (brand.getLogo() != null && !brand.getLogo().isEmpty()) {
            Glide.with(context)
                    .load(brand.getLogo())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .centerCrop())
                    .into(holder.brandLogo);
        } else {
            holder.brandLogo.setImageResource(R.drawable.placeholder_image);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(brand, position);
            }
        });

        holder.detailButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClick(brand, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        ImageView brandLogo;
        TextView brandName, brandDescription;
        Button detailButton;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            brandLogo = itemView.findViewById(R.id.brandLogo);
            brandName = itemView.findViewById(R.id.brandName);
            brandDescription = itemView.findViewById(R.id.brandDescription);
            detailButton = itemView.findViewById(R.id.detailButton);
        }
    }
} 