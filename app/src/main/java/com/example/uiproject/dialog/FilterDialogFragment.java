package com.example.uiproject.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.uiproject.R;
import com.example.uiproject.util.OpenProgressDialog;
import com.google.android.material.slider.RangeSlider;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class FilterDialogFragment extends DialogFragment {

    public interface FilterDialogListener {
        void onFilterApplied(String model, String brand, String location, Long minPrice, Long maxPrice);
    }

    // UI components
    private TextView tabAll, tabNew, tabUsed;
    private View tabIndicator;
    private LinearLayout modelLayout, brandLayout, locationLayout;
    private TextView priceRangeValue;
    private RangeSlider priceRangeSlider;
    private Button searchButton;
    
    // Data
    private float minPrice = 0;
    private float maxPrice = 30000000;
    private FilterDialogListener listener;
    
    private String selectedModel = "";
    private String selectedBrand = "";
    private String selectedLocation = "";
    private String selectedTab = "All"; // Default tab

    public static FilterDialogFragment newInstance() {
        return new FilterDialogFragment();
    }
    
    /**
     * Set the listener directly
     */
    public void setListener(FilterDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tabAll = view.findViewById(R.id.tabAll);
        tabNew = view.findViewById(R.id.tabNew);
        tabUsed = view.findViewById(R.id.tabUsed);
        tabIndicator = view.findViewById(R.id.tabIndicator);
        
        modelLayout = view.findViewById(R.id.modelLayout);
        brandLayout = view.findViewById(R.id.brandLayout);
        locationLayout = view.findViewById(R.id.locationLayout);
        
        priceRangeValue = view.findViewById(R.id.priceRangeValue);
        priceRangeSlider = view.findViewById(R.id.priceRangeSlider);
        searchButton = view.findViewById(R.id.searchButton);

        // Set up tab click listeners
        setupTabs();
        
        // Ensure "All" tab is selected by default
        initializeTabSelection();

        // Set initial values
        priceRangeSlider.setValues(minPrice, maxPrice);
        priceRangeSlider.setStepSize(500000f);
        updatePriceRangeText(minPrice, maxPrice);

        // Set up dropdown listeners
        modelLayout.setOnClickListener(v -> {
            showModelSelectionDialog();
        });
        
        brandLayout.setOnClickListener(v -> {
            showBrandSelectionDialog();
        });
        
        locationLayout.setOnClickListener(v -> {
            showLocationSelectionDialog();
        });

        // Set up price slider listener
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            if (values.size() >= 2) {
                updatePriceRangeText(values.get(0), values.get(1));
            }
        });

        // Set up search button
        searchButton.setOnClickListener(v -> {
            // Get range slider values
            List<Float> values = priceRangeSlider.getValues();
            Long selectedMinPrice = (long) Double.parseDouble(values.get(0).toString());
            Long selectedMaxPrice = (long) Double.parseDouble(values.get(1).toString());

            // Apply filters and dismiss dialog
            if (listener != null) {
                listener.onFilterApplied(
                    selectedModel, 
                    selectedBrand, 
                    selectedLocation, 
                    selectedMinPrice, 
                    selectedMaxPrice
                );
            }
            
            String message = "Filter Applied";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            dismiss();
            OpenProgressDialog.showProgressDialog(requireContext());
        });
    }
    
    /**
     * Initialize tab selection to "All" when dialog opens
     */
    private void initializeTabSelection() {
        // Set color for all tabs to inactive
        tabAll.setTextColor(Color.parseColor("#FF9800"));
        tabNew.setTextColor(Color.parseColor("#FF9800"));
        tabUsed.setTextColor(Color.parseColor("#FF9800"));
        
        // Activate the "All" tab
        tabAll.setTextColor(Color.parseColor("#FF5722"));
        
        // Position the indicator under the "All" tab
        tabIndicator.post(() -> {
            ViewGroup.LayoutParams params = tabIndicator.getLayoutParams();
            params.width = tabAll.getWidth();
            tabIndicator.setLayoutParams(params);
            tabIndicator.setTranslationX(0);
        });
        
        // Set selected tab
        selectedTab = "All";
    }
    
    private void setupTabs() {
        // Set click listeners for tabs
        tabAll.setOnClickListener(v -> {
            updateTabSelection("All");
        });
        
        tabNew.setOnClickListener(v -> {
            updateTabSelection("New");
        });
        
        tabUsed.setOnClickListener(v -> {
            updateTabSelection("Used");
        });
    }
    
    private void updateTabSelection(String tab) {
        // Reset all tabs text color
        tabAll.setTextColor(Color.parseColor("#FF9800"));
        tabNew.setTextColor(Color.parseColor("#FF9800"));
        tabUsed.setTextColor(Color.parseColor("#FF9800"));
        
        // Highlight selected tab
        selectedTab = tab;
        
        // Update tab indicator position and selected tab color
        ViewGroup.LayoutParams params = tabIndicator.getLayoutParams();
        
        if ("All".equals(tab)) {
            tabAll.setTextColor(Color.parseColor("#FF5722"));
            tabIndicator.setX(tabAll.getX());
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) tabIndicator.getLayoutParams();
            tabIndicator.post(() -> {
                ViewGroup.LayoutParams lp = tabIndicator.getLayoutParams();
                lp.width = tabAll.getWidth();
                tabIndicator.setLayoutParams(lp);
                tabIndicator.setTranslationX(0);
            });
        } else if ("New".equals(tab)) {
            tabNew.setTextColor(Color.parseColor("#FF5722"));
            tabIndicator.post(() -> {
                ViewGroup.LayoutParams lp = tabIndicator.getLayoutParams();
                lp.width = tabNew.getWidth();
                tabIndicator.setLayoutParams(lp);
                tabIndicator.setTranslationX(tabNew.getLeft());
            });
        } else if ("Used".equals(tab)) {
            tabUsed.setTextColor(Color.parseColor("#FF5722"));
            tabIndicator.post(() -> {
                ViewGroup.LayoutParams lp = tabIndicator.getLayoutParams();
                lp.width = tabUsed.getWidth();
                tabIndicator.setLayoutParams(lp);
                tabIndicator.setTranslationX(tabUsed.getLeft());
            });
        }
        
        Toast.makeText(requireContext(), "Selected: " + tab, Toast.LENGTH_SHORT).show();
    }
    
    private void showModelSelectionDialog() {
        final String[] models = {"Sedan", "SUV", "Hatchback", "Convertible", "Pickup", "Van", "Coupe", "Supercar"};
        // load tu api
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Select Model")
                .setItems(models, (dialog, which) -> {
                    selectedModel = models[which];
                    TextView modelText = (TextView) ((ViewGroup) modelLayout).getChildAt(0);
                    modelText.setText(selectedModel);
                    modelText.setTextColor(Color.BLACK);
                });
        builder.create().show();
    }
    
    private void showBrandSelectionDialog() {
        final String[] brands = {"Toyota", "BMW", "Mercedes", "Audi", "Nissan", "Mazda", "Hyundai", "Kia", "Porsche", "Lamborghini"};
        // load tu api
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Select Brand")
                .setItems(brands, (dialog, which) -> {
                    selectedBrand = brands[which];
                    TextView brandText = (TextView) ((ViewGroup) brandLayout).getChildAt(0);
                    brandText.setText(selectedBrand);
                    brandText.setTextColor(Color.BLACK);
                });
        builder.create().show();
    }
    
    private void showLocationSelectionDialog() {
        final String[] locations = {"HCM"};
        // load tu api
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Select Location")
                .setItems(locations, (dialog, which) -> {
                    selectedLocation = locations[which];
                    TextView locationText = (TextView) ((ViewGroup) locationLayout).getChildAt(1);
                    locationText.setText(selectedLocation);
                    locationText.setTextColor(Color.BLACK);
                });
        builder.create().show();
    }

    private void updatePriceRangeText(float min, float max) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        format.setCurrency(Currency.getInstance("INR"));
        
        // Remove the currency symbol to just keep "Rs."
        String formattedMin = "Fee: " + String.format(Locale.getDefault(), "%,.0f", min);
        String formattedMax = "Fee: " + String.format(Locale.getDefault(), "%,.2f", max) + "/day";
        
        priceRangeValue.setText(formattedMin + " - " + formattedMax);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Set transparent background for the dialog window
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                
                // Set dialog width to match parent with margins
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                
                // Apply layout parameters with horizontal margin
                window.setLayout(width, height);
                
                // 16dp margin on the sides (convert dp to pixels)
                int marginHorizontal = (int) (16 * getResources().getDisplayMetrics().density);
                window.getAttributes().width = width - (marginHorizontal * 2);
                window.getAttributes().horizontalMargin = marginHorizontal / (float) width;
            }
        }
    }
} 