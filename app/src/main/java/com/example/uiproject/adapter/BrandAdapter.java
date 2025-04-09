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
import com.example.uiproject.entity.CarBrandDTO;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private Context context;
    private List<CarBrandDTO> brandList;
    private OnBrandClickListener listener;

    public interface OnBrandClickListener {
        void onBrandClick(int position);
    }

    public BrandAdapter(Context context, List<CarBrandDTO> brandList) {
        this.context = context;
        this.brandList = brandList;
    }

    public void setOnBrandClickListener(OnBrandClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.brand_item, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        CarBrandDTO brand = brandList.get(position);
        
        holder.brandName.setText(brand.getName());

        // Load ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(brand.getLogo()) // hoặc brand.getLogo() nếu là URL ảnh
                .placeholder(R.drawable.ic_tesla) // ảnh tạm thời khi đang load
                .error(R.drawable.ic_tesla)       // ảnh lỗi nếu load thất bại
                .into(holder.brandLogo);             // ImageView bạn muốn load vào

        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBrandClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (brandList != null ? brandList.size() : 0);
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {
        ImageView brandLogo;
        TextView brandName;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            brandLogo = itemView.findViewById(R.id.iv_brand_logo);
            brandName = itemView.findViewById(R.id.tv_brand_name);
        }
    }
} 