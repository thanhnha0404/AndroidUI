package com.example.uiproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.uiproject.R;
import com.example.uiproject.adapter.BrandAdapter;
import com.example.uiproject.adapter.VehicleAdapter;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.dialog.CarDetailsDialog;
import com.example.uiproject.entity.CarBrandDTO;
import com.example.uiproject.entity.CarDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private ApiService apiService;
    
    private ImageButton headerNotification, headerProfile;
    TextView seeAll1;
    TextView seeAll2;

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
        seeAll1 = view.findViewById(R.id.seeOn1);
        seeAll2 = view.findViewById(R.id.seeOn2);
        
        headerNotification = view.findViewById(R.id.iv_notification);
        headerProfile = view.findViewById(R.id.iv_profile);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        
        // Set click listeners
        headerNotification.setOnClickListener(v -> {
            // Handle notification click
        });
        
        headerProfile.setOnClickListener(v -> {
            // Handle profile click
        });

        seeAll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeAll1.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                OpenFragmentCar();
            }
        });

        seeAll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeAll1.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                OpenFragmentCar();
            }
        });

    }

    private void OpenFragmentCar (){
        Toast.makeText(getActivity(), "Bạn chọn ALL CAR ", Toast.LENGTH_SHORT).show();
        CarListFragment viewVehicles = new CarListFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, viewVehicles);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    
    private void setupBrandsRecyclerView() {
        brandList = new ArrayList<>();

        // Setup horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        brandsRecyclerView.setLayoutManager(layoutManager);
        
        // Create and set adapter
        brandAdapter = new BrandAdapter(getContext(), brandList);
        brandAdapter.setOnBrandClickListener(position -> {   // tuong duong voi viec goi ham  onBrandClick ben adapter
            // Handle brand click
            // You can filter vehicles by brand here
            // mo len cai fragment list car cua category
            Toast.makeText(getActivity(), "Bạn chọn: " + brandList.get(position).getName(), Toast.LENGTH_SHORT).show();

            CarBrandDTO brand = brandList.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("brand", brand);

            CarListFragment viewVehicles = new CarListFragment();
            viewVehicles.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, viewVehicles);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        });
        brandsRecyclerView.setAdapter(brandAdapter);

        LoadCarBrands();  // call api update brandList no bi bat dong bo
    }
    
    private void setupVehiclesRecyclerView() {
        vehicleList = new ArrayList<>();
        
        // Setup horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        
        // Create and set adapter
        vehicleAdapter = new VehicleAdapter(getContext(), vehicleList);
        vehicleAdapter.setOnVehicleClickListener(position -> {
            CarDTO carDTO = vehicleList.get(position);
            CarDetailsDialog dialog = CarDetailsDialog.newInstance(carDTO);
            dialog.show(getActivity().getSupportFragmentManager(), "car_detail");
        });
        recyclerView.setAdapter(vehicleAdapter);
        LoadSaleCar();
    }

    private void setupNewVehiclesRecyclerView() {
        newVehicleList = new ArrayList<>();
        
        // Add sample new vehicles

        
        // Setup horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        newVehiclesRecyclerView.setLayoutManager(layoutManager);
        
        // Create and set adapter
        newVehiclesAdapter = new VehicleAdapter(getContext(), newVehicleList);
        newVehiclesAdapter.setOnVehicleClickListener(position -> {
            CarDTO carDTO = newVehicleList.get(position);
            CarDetailsDialog dialog = CarDetailsDialog.newInstance(carDTO);
            dialog.show(getActivity().getSupportFragmentManager(), "car_detail");
        });
        newVehiclesRecyclerView.setAdapter(newVehiclesAdapter);

        LoadNewCar();
    }

    public void LoadSaleCar() {
        apiService.getSaleCar().enqueue(new Callback<List<CarDTO>>() {
            @Override
            public void onResponse(Call<List<CarDTO>> call, Response<List<CarDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vehicleList.clear(); // Xóa danh sách cũ trước khi cập nhật
                    vehicleList.addAll(response.body()); // Cập nhật danh sách mới
                    vehicleAdapter.notifyDataSetChanged();
                    Log.e("API_RESPONSE", "Số lượng xe sale: " + vehicleList.size());
                }
            }

            @Override
            public void onFailure(Call<List<CarDTO>> call, Throwable t) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("HomeFragment", "Activity is not attached");
                }
            }
        });
    }
    public void LoadCarBrands() {
        apiService.getAllCarBrandActive().enqueue(new Callback<List<CarBrandDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<CarBrandDTO>> call, @NonNull Response<List<CarBrandDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brandList.clear(); // Xóa danh sách cũ trước khi cập nhật
                    brandList.addAll(response.body()); // Cập nhật danh sách mới
                    brandAdapter.notifyDataSetChanged();
                    Log.e("API_RESPONSE", "Số lượng thương hiệu: " + brandList.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CarBrandDTO>> call, @NonNull Throwable t) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("HomeFragment", "Activity is not attached");
                }
            }
        });
    }

    public void LoadNewCar () {
        apiService.getNewCar().enqueue(new Callback<List<CarDTO>>() {
            @Override
            public void onResponse(Call<List<CarDTO>> call, Response<List<CarDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newVehicleList.clear(); // Xóa danh sách cũ trước khi cập nhật
                    newVehicleList.addAll(response.body()); // Cập nhật danh sách mới
                    newVehiclesAdapter.notifyDataSetChanged();
                    Log.e("API_RESPONSE", "Số lượng xe moi: " + newVehicleList.size());
                }
            }

            @Override
            public void onFailure(Call<List<CarDTO>> call, Throwable t) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("HomeFragment", "Activity is not attached");
                }
            }
        });
    }

} 