package com.example.uiproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.dialog.CarDetailsDialog;
import com.example.uiproject.dialog.FilterDialogFragment;
import com.example.uiproject.R;
import com.example.uiproject.adapter.CarAdapter;
import com.example.uiproject.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarListFragment extends Fragment implements CarAdapter.OnCarClickListener, FilterDialogFragment.FilterDialogListener {

    private RecyclerView carsRecyclerView;
    private CarAdapter carAdapter;
    private List<Car> carList;
    private EditText searchEditText;
    private ImageButton filterButton;

    // Filter parameters
    private String currentModel = "";
    private String currentBrand = "";
    private String currentLocation = "";
    private float currentMinPrice = 0;
    private float currentMaxPrice = 3000000;

    public CarListFragment() {
        // Required empty public constructor
    }

    public static CarListFragment newInstance() {
        return new CarListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        carsRecyclerView = view.findViewById(R.id.carsRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        filterButton = view.findViewById(R.id.filterButton);

        // Set up filter button click listener
        filterButton.setOnClickListener(v -> {
            // Show filter dialog
            showFilterDialog();
        });

        // Set up RecyclerView
        setupRecyclerView();
        
        // Load sample car data
        loadSampleCarData();
    }

    private void showFilterDialog() {
        FilterDialogFragment dialog = FilterDialogFragment.newInstance();
        dialog.setListener(this);
        dialog.show(getParentFragmentManager(), "FilterDialog");
    }

    private void setupRecyclerView() {
        carList = new ArrayList<>();
        carAdapter = new CarAdapter(carList, this);
        
        // Set up grid layout with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        carsRecyclerView.setLayoutManager(layoutManager);
        carsRecyclerView.setAdapter(carAdapter);
    }

    private void loadSampleCarData() {
        // Sample car data
        carList.add(new Car("Audi e-tron Premium", "Rs.63,900/day", R.drawable.mazda6, 340));
        carList.add(new Car("Suzuki Swift", "Rs.35,000/day", R.drawable.mazda6, 290));
        carList.add(new Car("Mercedes G-Wagon", "Rs.75,500/day", R.drawable.mazda6, 180));
        carList.add(new Car("Tesla Model 3", "Rs.16,00,000.00", R.drawable.mazda6, 210));
        
        // Notify adapter
        carAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCarClick(int position) {
        Car car = carList.get(position);
        
        // Show car details dialog
        CarDetailsDialog detailsDialog = CarDetailsDialog.newInstance(car);
        detailsDialog.show(getParentFragmentManager(), "CarDetailsDialog");
    }

    @Override
    public void onFavoriteClick(int position) {
        Car car = carList.get(position);
        // Toggle favorite status
        car.setFavorite(!car.isFavorite());
        carAdapter.notifyItemChanged(position);
        
        String message = car.isFavorite() ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFilterApplied(String model, String brand, String location, float minPrice, float maxPrice) {
        // Save current filter parameters
        this.currentModel = model;
        this.currentBrand = brand;
        this.currentLocation = location;
        this.currentMinPrice = minPrice;
        this.currentMaxPrice = maxPrice;
        
        // Apply filters to car list
        applyFilters();
    }
    
    private void applyFilters() {
        // In a real app, you would likely query a database or API with these filters
        // For this example, we'll just filter the existing list based on price
        
        List<Car> filteredList = new ArrayList<>();
        
        for (Car car : carList) {
            // Extract numeric price from string like "Rs.63,900/day"
            String priceStr = car.getPrice().replaceAll("[^\\d.]", "");
            float price = 0;
            try {
                price = Float.parseFloat(priceStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            
            boolean matchesModel = currentModel.isEmpty() || car.getName().contains(currentModel);
            boolean matchesBrand = currentBrand.isEmpty() || car.getName().contains(currentBrand);
            boolean matchesPrice = price >= currentMinPrice && price <= currentMaxPrice;
            
            if (matchesModel && matchesBrand && matchesPrice) {
                filteredList.add(car);
            }
        }
        
        // Update the adapter with filtered list
        carAdapter.updateCarList(filteredList);
        
        // Show a message with filter results
        String message = String.format(Locale.getDefault(), 
                "Found %d cars matching your filters", filteredList.size());
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
} 