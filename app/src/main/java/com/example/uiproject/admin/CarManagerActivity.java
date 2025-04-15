package com.example.uiproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;

import java.util.ArrayList;
import java.util.List;

public class CarManagerActivity extends AppCompatActivity {

    private RecyclerView carsRecyclerView;
    private CarAdapter carAdapter;
    private EditText searchEditText;
    private Button addButton;
    private List<Car> carList;
    private List<Car> filteredCarList;

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
        
        // Initialize car list with sample data
        initSampleCarData();
        
        // Initialize adapter
        filteredCarList = new ArrayList<>(carList);
        carAdapter = new CarAdapter(filteredCarList);
        carsRecyclerView.setAdapter(carAdapter);

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

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh car list when returning from add car screen
        // This would typically involve fetching updated data from the database
    }

    private void initSampleCarData() {
        carList = new ArrayList<>();
        carList.add(new Car("Audi e-tron Premium", "Rs. 54,77,823.73", R.drawable.ic_launcher_background, 360, true));
        carList.add(new Car("Suzuki Swift", "Rs. 9,95,000", R.drawable.ic_launcher_background, 210, false));
        carList.add(new Car("Mercedes G-Class", "Rs. 1,35,00,000", R.drawable.ic_launcher_background, 150, true));
        carList.add(new Car("Hyundai Grand i10", "Rs. 5,99,000", R.drawable.ic_launcher_background, 280, false));
    }

    private void filterCars(String query) {
        filteredCarList.clear();
        
        if (query.isEmpty()) {
            filteredCarList.addAll(carList);
        } else {
            query = query.toLowerCase();
            for (Car car : carList) {
                if (car.getName().toLowerCase().contains(query)) {
                    filteredCarList.add(car);
                }
            }
        }
        
        carAdapter.notifyDataSetChanged();
    }
} 