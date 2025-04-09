package com.example.uiproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.uiproject.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        // Set up bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_favorite) {
                // TODO: Replace with FavoriteFragment when created
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_notification) {
                // TODO: Replace with NotificationFragment when created
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_profile) {
                // TODO: Replace with ProfileFragment when created
                selectedFragment = new HomeFragment();
            }
            
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            
            return true;
        });

        // Set default fragment
        loadFragment(new HomeFragment());
        
        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
} 