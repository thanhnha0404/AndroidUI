package com.example.uiproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;

public class BrandShimmerAdapter extends RecyclerView.Adapter<BrandShimmerAdapter.ShimmerViewHolder> {
    private int shimmerItemCount = 6; // số item giả lập

    @NonNull
    @Override
    public ShimmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shimmer_brand_item, parent, false);
        return new ShimmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShimmerViewHolder holder, int position) {
        // Không cần bind gì cả vì chỉ là placeholder
    }

    @Override
    public int getItemCount() {
        return shimmerItemCount;
    }

    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
