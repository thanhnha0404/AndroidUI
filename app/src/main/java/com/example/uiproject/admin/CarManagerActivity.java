package com.example.uiproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;
import com.example.uiproject.admin.api.ApiServiceAdmin;
import com.example.uiproject.admin.api.RetrofitClientAdmin;
import com.example.uiproject.admin.model.CarDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarManagerActivity extends AppCompatActivity {

    private RecyclerView carsRecyclerView;
    private CarAdapter carAdapter;
    private EditText searchEditText;
    private Button addButton;
    private List<CarDTO> carList;
    private List<CarDTO> filteredCarList;
    private ApiServiceAdmin apiServiceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_manager);

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText);
        addButton = findViewById(R.id.addButton);
        carsRecyclerView = findViewById(R.id.carsRecyclerView);

        // Set up RecyclerView
        carsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize lists
        carList = new ArrayList<>();
        filteredCarList = new ArrayList<>();

        // Initialize adapter
        carAdapter = new CarAdapter(filteredCarList);
        carsRecyclerView.setAdapter(carAdapter);

        // Initialize API service
        apiServiceAdmin = RetrofitClientAdmin.getInstance().create(ApiServiceAdmin.class);

        // Fetch cars from API
        fetchCars();

        // Set up search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCars(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddCarActivity
                Intent intent = new Intent(CarManagerActivity.this, AddCarActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchCars() {
        apiServiceAdmin.getAllCar().enqueue(new Callback<List<CarDTO>>() {
            @Override
            public void onResponse(Call<List<CarDTO>> call, Response<List<CarDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    carList.clear();
                    carList.addAll(response.body());
                    filteredCarList.clear();
                    filteredCarList.addAll(carList);
                    carAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CarManagerActivity.this, "Failed to fetch cars", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CarDTO>> call, Throwable t) {
                Toast.makeText(CarManagerActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCars(String query) {
        filteredCarList.clear();

        if (query.isEmpty()) {
            filteredCarList.addAll(carList);
        } else {
            query = query.toLowerCase();
            for (CarDTO car : carList) {
                if (car.getName().toLowerCase().contains(query)) {
                    filteredCarList.add(car);
                }
            }
        }

        carAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh car list when returning from add car screen
        fetchCars();
    }
} 