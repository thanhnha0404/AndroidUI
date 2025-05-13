package com.example.uiproject.admin.model;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.uiproject.R;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    private List<Uri> imageUris;
    private Context context;
    private OnRemoveClickListener onRemoveClickListener;

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        this.onRemoveClickListener = listener;
    }

    public ImagesAdapter(Context context, List<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.item_uploaded_image, parent, false);
            return new ImageViewHolder(view);
        } catch (Exception e) {
            Toast.makeText(context, "Error creating view: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Fallback view - should never happen but prevents crashes
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        try {
            if (position < imageUris.size() && imageUris.get(position) != null) {
                Uri imageUri = imageUris.get(position);

                // Use Glide with error handling
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(context)
                        .load(imageUri)
                        .apply(options)
                        .centerCrop()
                        .into(holder.imageView);

                // Set remove button click listener
                if (holder.removeButton != null) {
                    holder.removeButton.setOnClickListener(v -> {
                        if (onRemoveClickListener != null) {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                onRemoveClickListener.onRemoveClick(adapterPosition);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            // Log error but don't crash
            Toast.makeText(context, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            if (holder.imageView != null) {
                holder.imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageUris != null ? imageUris.size() : 0;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton removeButton;

        public ImageViewHolder(View itemView) {
            super(itemView);
            try {
                imageView = itemView.findViewById(R.id.uploadedImageView);
                removeButton = itemView.findViewById(R.id.removeImageButton);
            } catch (Exception e) {
                // Handle exception to prevent crashes in extreme cases
            }
        }
    }
}

