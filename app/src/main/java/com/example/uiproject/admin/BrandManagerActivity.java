package com.example.uiproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uiproject.R;
import com.example.uiproject.admin.api.ApiServiceAdmin;
import com.example.uiproject.admin.api.RetrofitClientAdmin;
import com.example.uiproject.admin.model.Brand;
import com.example.uiproject.admin.model.BrandsAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrandManagerActivity extends AppCompatActivity implements BrandsAdapter.OnBrandClickListener {

    private RecyclerView brandsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button addBrandButton;
    
    private BrandsAdapter brandsAdapter;
    private ApiServiceAdmin apiServiceAdmin;
    private List<Brand> brandList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brand_manager);
        
        initViews();
        setupRecyclerView();
        apiServiceAdmin = RetrofitClientAdmin.getInstance().create(ApiServiceAdmin.class);
        setupListeners();
        loadBrands();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload brands when returning to this activity
        loadBrands();
    }

    private void initViews() {
        brandsRecyclerView = findViewById(R.id.brandsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        searchView = findViewById(R.id.searchView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        addBrandButton = findViewById(R.id.addBrandButton);
    }

    private void setupRecyclerView() {
        brandsAdapter = new BrandsAdapter(this);
        brandsAdapter.setOnBrandClickListener(this);
        brandsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        brandsRecyclerView.setAdapter(brandsAdapter);
    }

    private void setupListeners() {
        addBrandButton.setOnClickListener(v -> {
            Intent intent = new Intent(BrandManagerActivity.this, AddBrandActivity.class);
            startActivity(intent);
        });
        
        swipeRefreshLayout.setOnRefreshListener(this::loadBrands);
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBrands(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBrands(newText);
                return true;
            }
        });
    }

    private void loadBrands() {
        showLoading(true);
        apiServiceAdmin.getAllBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);

                List<Brand> responseBrands = response.body();
                Gson gson = new Gson();
                String json = gson.toJson(responseBrands);
                Log.d("BrandResponse", json);
                
                if (response.isSuccessful() && response.body() != null) {
                    brandList = response.body();
                    brandsAdapter.setBrands(brandList);
                    updateEmptyView();
                } else {
                    Toast.makeText(BrandManagerActivity.this, 
                            "Failed to load brands: " + response.message(), 
                            Toast.LENGTH_SHORT).show();
                    updateEmptyView();
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);
                Toast.makeText(BrandManagerActivity.this, 
                        "Error: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                updateEmptyView();
            }
        });
    }

    private void filterBrands(String query) {
        if (query.isEmpty()) {
            brandsAdapter.setBrands(brandList);
        } else {
            List<Brand> filteredList = new ArrayList<>();
            for (Brand brand : brandList) {
                if (brand.getName().toLowerCase().contains(query.toLowerCase()) ||
                    (brand.getDescription() != null && brand.getDescription().toLowerCase().contains(query.toLowerCase()))) {
                    filteredList.add(brand);
                }
            }
            brandsAdapter.filterList(filteredList);
            updateEmptyView();
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        brandsRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyView() {
        emptyView.setVisibility(brandsAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDetailClick(Brand brand, int position) {
        // Navigate to EditBrandActivity with brand data
        Intent intent = new Intent(BrandManagerActivity.this, EditBrandActivity.class);
        intent.putExtra("brand", new Gson().toJson(brand));
        startActivity(intent);
    }

    @Override
    public void onItemClick(Brand brand, int position) {
        // Same behavior as detail click
        onDetailClick(brand, position);
    }
}
