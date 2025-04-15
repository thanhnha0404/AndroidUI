package com.example.uiproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiproject.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        // Set up click listener for vehicle management option
        TextView vehicleManagementOption = findViewById(R.id.vehicleManagementOption);
        vehicleManagementOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the CarManagerActivity
                Intent intent = new Intent(AdminActivity.this, CarManagerActivity.class);
                startActivity(intent);
            }
        });

        // Set up click listeners for other options
        TextView brandManagementOption = findViewById(R.id.brandManagementOption);
        brandManagementOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to brand management screen
            }
        });

        TextView contractListOption = findViewById(R.id.contractListOption);
        contractListOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to contract list screen
            }
        });
    }
} 