package com.example.uiproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.entity.CustomerDTO;
import com.example.uiproject.fragment.HistoryFragment;
import com.example.uiproject.fragment.HomeFragment;
import com.example.uiproject.fragment.NotificationFragment;
import com.example.uiproject.fragment.ProfileFragment;
import com.example.uiproject.util.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ApiService apiService;
    private CustomerDTO cus = null;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        sessionManager = new SessionManager(HomeActivity.this);
        loadCustomer();

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

    private void loadCustomer (){
        Map<String,Object> headers = new HashMap<>();
        String authToken = sessionManager.getCustomerToken();
        if (authToken != null && !authToken.equals("")){
            headers.put("Authorization",authToken);
            apiService.getUser(headers).enqueue(new Callback<CustomerDTO>() {
                @Override
                public void onResponse(Call<CustomerDTO> call, Response<CustomerDTO> response) {
                    if (response.isSuccessful() && response.body() != null){
                        cus = response.body();
                        sessionManager.saveUser(cus.getId(),cus.getEmail());
                        setUpBottomNavigation();
                    }
                }

                @Override
                public void onFailure(Call<CustomerDTO> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
                    setUpBottomNavigation();
                }
            });
        }
        else{
            Toast.makeText(HomeActivity.this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            setUpBottomNavigation();
        }
    }

    private void setUpBottomNavigation (){
        // Set up bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_favorite) {
                // TODO: Replace with FavoriteFragment when created
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_notification) {
                // TODO: Replace with NotificationFragment when created
                selectedFragment = new NotificationFragment();
            } else if (itemId == R.id.nav_profile) {
                // TODO: Replace with ProfileFragment when created
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("customer", cus);
                selectedFragment.setArguments(bundle);
                loadFragment(selectedFragment);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
} 