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
import com.example.uiproject.entity.AddressDTO;
import com.example.uiproject.entity.CarDTO;
import com.example.uiproject.fragment.BookingFragment;
import com.example.uiproject.model.Car;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CarDetailsDialog extends DialogFragment {

    private CarDTO car;
    private ImageView mainCarImage, thumbnail1, thumbnail2;
    private TextView carNameTextView, priceTextView, descriptionTextView, ratingTextView, carLine, carBrand, carAdd;
    private ImageButton favoriteButton, backButton, shareButton;
    private MaterialButton bookButton;
    private View contactDealerButton, carDetailsButton, locationButton;
    
    // Sample images for demonstration
    private List carImages = new ArrayList<>(); // Add more images as needed
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
        carLine = view.findViewById(R.id.carType);
        carBrand = view.findViewById(R.id.carBrand);
        carAdd = view.findViewById(R.id.carAdd);

        // Populate data from car object
        if (car != null) {
            carNameTextView.setText(car.getName());
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(true);  // Bật tính năng ngắt nhóm số
            String formattedPrice = numberFormat.format(car.getPrice());
            priceTextView.setText(formattedPrice + "/day");

            carLine.setText(car.getLine());
            carBrand.setText(car.getBrand());

            AddressDTO addressDTO = car.getAddressDTO();

            carAdd.setText(addressDTO.getDistrict());

            // Set a default description for demonstration
            descriptionTextView.setText(car.getDescription());
            
            ratingTextView.setText("4.5/5"); // Example rating

            carImages = car.getPictures();
            ImageView[] imageViews = {mainCarImage, thumbnail1, thumbnail2};

            for (int i = 0; i < 3 && i < carImages.size(); i++) {
                Glide.with(requireContext())
                        .load(car.getPictures().get(i))
                        .placeholder(R.drawable.car_background)
                        .error(R.drawable.car_background)
                        .into(imageViews[i]);
            }
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
            if (carImages.size() > 1) {
                currentImageIndex = (currentImageIndex + 1) % carImages.size();
                Glide.with(requireContext())
                        .load(carImages.get(currentImageIndex))         // hoặc brand.getLogo() nếu là URL ảnh
                        .placeholder(R.drawable.car_background) // ảnh tạm thời khi đang load
                        .error(R.drawable.car_background)       // ảnh lỗi nếu load thất bại
                        .into(mainCarImage);
            }
        });

        // Thumbnail clicks change the main image
        thumbnail1.setOnClickListener(v -> {
            if (carImages.size() >= 2){
                Glide.with(requireContext())
                        .load(carImages.get(1))         // hoặc brand.getLogo() nếu là URL ảnh
                        .placeholder(R.drawable.car_background) // ảnh tạm thời khi đang load
                        .error(R.drawable.car_background)       // ảnh lỗi nếu load thất bại
                        .into(mainCarImage);
            }

        });

        thumbnail2.setOnClickListener(v -> {
            if (carImages.size() >= 3){
                Glide.with(requireContext())
                        .load(carImages.get(2))         // hoặc brand.getLogo() nếu là URL ảnh
                        .placeholder(R.drawable.car_background) // ảnh tạm thời khi đang load
                        .error(R.drawable.car_background)       // ảnh lỗi nếu load thất bại
                        .into(mainCarImage);
            }
        });

        // Book button
        bookButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Booking " + car.getName(), Toast.LENGTH_SHORT).show();
            BookingFragment bookingFragment = new BookingFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("carDTO",car);
            bookingFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, bookingFragment) // container trong activity
                    .addToBackStack(null) // Thêm vào back stack để quay lại được
                    .commit();

            dismiss();
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