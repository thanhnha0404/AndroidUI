package com.example.uiproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;

public class ContractShimmerAdapter extends RecyclerView.Adapter<ContractShimmerAdapter.ShimmerViewHolder> {

    private int shimmerItemCount = 5; // số item giả

    @NonNull
    @Override
    public ShimmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contract_shimmer, parent, false);
        return new ShimmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShimmerViewHolder holder, int position) {
        // Không cần bind dữ liệu thật
    }

    @Override
    public int getItemCount() {
        return shimmerItemCount;
    }

    public static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
