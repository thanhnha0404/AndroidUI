package com.example.uiproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.uiproject.R;
import com.example.uiproject.adapter.BrandAdapter;
import com.example.uiproject.adapter.VehicleAdapter;
import com.example.uiproject.entity.CarBrandDTO;
import com.example.uiproject.entity.CarDTO;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView newVehiclesRecyclerView;
    private RecyclerView brandsRecyclerView;
    private VehicleAdapter vehicleAdapter;
    private VehicleAdapter newVehiclesAdapter;
    private BrandAdapter brandAdapter;
    private List<CarDTO> vehicleList;
    private List<CarDTO> newVehicleList;
    private List<CarBrandDTO> brandList;
    
    private ImageButton headerNotification, headerProfile;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize UI components
        initUI(view);
        
        // Set up the recyclerviews with data
        setupBrandsRecyclerView();
        setupVehiclesRecyclerView();
        setupNewVehiclesRecyclerView();
        
        return view;
    }
    
    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.rv_vehicles);
        newVehiclesRecyclerView = view.findViewById(R.id.rv_new_vehicles);
        brandsRecyclerView = view.findViewById(R.id.rv_brands);
        
        headerNotification = view.findViewById(R.id.iv_notification);
        headerProfile = view.findViewById(R.id.iv_profile);
        
        // Set click listeners
        headerNotification.setOnClickListener(v -> {
            // Handle notification click
        });
        
        headerProfile.setOnClickListener(v -> {
            // Handle profile click
        });
    }
    
    private void setupBrandsRecyclerView() {
        brandList = new ArrayList<>();

        // them data o day
        
        // Setup horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        brandsRecyclerView.setLayoutManager(layoutManager);
        
        // Create and set adapter
        brandAdapter = new BrandAdapter(getContext(), brandList);
        brandAdapter.setOnBrandClickListener(position -> {
            // Handle brand click
            // You can filter vehicles by brand here
        });
        brandsRecyclerView.setAdapter(brandAdapter);
    }
    
    private void setupVehiclesRecyclerView() {
        vehicleList = new ArrayList<>();
        
        // Add sample vehicles
        
        // Setup horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        
        // Create and set adapter
        vehicleAdapter = new VehicleAdapter(getContext(), vehicleList);
        recyclerView.setAdapter(vehicleAdapter);
    }

    private void setupNewVehiclesRecyclerView() {
        newVehicleList = new ArrayList<>();
        
        // Add sample new vehicles

        
        // Setup horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        newVehiclesRecyclerView.setLayoutManager(layoutManager);
        
        // Create and set adapter
        newVehiclesAdapter = new VehicleAdapter(getContext(), newVehicleList);
        newVehiclesRecyclerView.setAdapter(newVehiclesAdapter);
    }
} 