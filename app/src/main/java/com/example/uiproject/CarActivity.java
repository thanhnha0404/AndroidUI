package com.example.uiproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.uiproject.fragment.CarListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_activity);

        // Load the CarListFragment by default
        if (savedInstanceState == null) {
            loadFragment(CarListFragment.newInstance());
        }

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Load the CarListFragment when home is clicked
                loadFragment(CarListFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_search) {
                // Handle search navigation
                return true;
            } else if (itemId == R.id.nav_favorites) {
                // Handle favorites navigation
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Handle profile navigation
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
} 