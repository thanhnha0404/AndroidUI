package com.example.uiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // Handle home click
                    return true;
                } else if (itemId == R.id.nav_search) {
                    // Handle search click
                    return true;
                } else if (itemId == R.id.nav_favorites) {
                    // Handle favorites click
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Handle profile click
                    return true;
                }
                return false;
            }
        });

        // Add some sample brands (you can replace with your actual data)
        addSampleBrands();
    }

    private void addSampleBrands() {
        LinearLayout brandsContainer = findViewById(R.id.brandsContainer);
        String[] brands = {"Mazda", "Toyota", "Honda", "Ford", "BMW", "Mercedes"};

        for (String brand : brands) {
            TextView brandView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 32, 0);
            brandView.setLayoutParams(params);
            brandView.setText(brand);
            brandView.setTextSize(16);
            brandView.setPadding(16, 8, 16, 8);
            brandView.setBackgroundResource(R.drawable.brand_background);

            brandsContainer.addView(brandView);
        }
    }
}