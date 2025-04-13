package com.example.uiproject.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.uiproject.R;
import com.example.uiproject.entity.CarDTO;
import com.example.uiproject.model.Car;
import com.google.android.material.button.MaterialButton;

public class CarDetailsDialog extends DialogFragment {

    private CarDTO car;
    private ImageView mainCarImage, thumbnail1, thumbnail2;
    private TextView carNameTextView, priceTextView, descriptionTextView, ratingTextView;
    private ImageButton favoriteButton, backButton, shareButton;
    private MaterialButton bookButton;
    private View contactDealerButton, carDetailsButton, locationButton;
    
    // Sample images for demonstration
    private int[] carImages = {R.drawable.mazda6}; // Add more images as needed
    private int currentImageIndex = 0;
    private boolean isFavorite = false;

    public static CarDetailsDialog newInstance(CarDTO car) {
        CarDetailsDialog dialog = new CarDetailsDialog();
        dialog.car = car;
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_car_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        mainCarImage = view.findViewById(R.id.mainCarImage);
        thumbnail1 = view.findViewById(R.id.thumbnail1);
        thumbnail2 = view.findViewById(R.id.thumbnail2);
        carNameTextView = view.findViewById(R.id.carNameTextView);
        priceTextView = view.findViewById(R.id.priceTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        ratingTextView = view.findViewById(R.id.ratingTextView);
        favoriteButton = view.findViewById(R.id.favoriteButton);
        backButton = view.findViewById(R.id.backButton);
        shareButton = view.findViewById(R.id.shareButton);
        bookButton = view.findViewById(R.id.bookButton);
        contactDealerButton = view.findViewById(R.id.contactDealerButton);
        carDetailsButton = view.findViewById(R.id.carDetailsButton);
        locationButton = view.findViewById(R.id.locationButton);

        // Populate data from car object
        if (car != null) {
            carNameTextView.setText(car.getName());
            priceTextView.setText(car.getPrice().toString());
            
            // Set a default description for demonstration
            descriptionTextView.setText(car.getDescription());
            
            ratingTextView.setText("4.5/5"); // Example rating

            Glide.with(requireContext())
                    .load(car.getPictures().get(0))         // hoặc brand.getLogo() nếu là URL ảnh
                    .placeholder(R.drawable.car_background) // ảnh tạm thời khi đang load
                    .error(R.drawable.car_background)       // ảnh lỗi nếu load thất bại
                    .into(mainCarImage);

            // Set initial image
//            thumbnail1.setImageResource(car.getImageResource());
//            thumbnail2.setImageResource(car.getImageResource());
        }

        // Set up click listeners
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Back button closes the dialog
        backButton.setOnClickListener(v -> dismiss());

        // Share button
        shareButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Share feature clicked", Toast.LENGTH_SHORT).show();
        });

        // Favorite button toggle
        favoriteButton.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            favoriteButton.setImageResource(isFavorite ? 
                    R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            
            String message = isFavorite ? "Added to favorites" : "Removed from favorites";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });

        // Main image click cycles through available images
        mainCarImage.setOnClickListener(v -> {
            // Cycle to next image if multiple images are available
            if (carImages.length > 1) {
                currentImageIndex = (currentImageIndex + 1) % carImages.length;
                mainCarImage.setImageResource(carImages[currentImageIndex]);
            }
        });

//        // Thumbnail clicks change the main image
//        thumbnail1.setOnClickListener(v -> {
//            mainCarImage.setImageResource(car.getImageResource());
//        });
//
//        thumbnail2.setOnClickListener(v -> {
//            // If you have additional images, set them here
//            mainCarImage.setImageResource(car.getImageResource());
//        });

        // Book button
        bookButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Booking " + car.getName(), Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // Bottom action buttons
        contactDealerButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Contacting dealer", Toast.LENGTH_SHORT).show();
        });

        carDetailsButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Viewing car details", Toast.LENGTH_SHORT).show();
        });

        locationButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Viewing location", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                window.setWindowAnimations(R.style.DialogAnimation);
            }
        }
    }
} 