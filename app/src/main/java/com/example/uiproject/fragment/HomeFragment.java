package com.example.uiproject.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.uiproject.R;
import com.example.uiproject.adapter.BrandAdapter;
import com.example.uiproject.adapter.BrandShimmerAdapter;
import com.example.uiproject.adapter.CarShimerAdapter;
import com.example.uiproject.adapter.VehicleAdapter;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.dialog.CarDetailsDialog;
import com.example.uiproject.entity.CarBrandDTO;
import com.example.uiproject.entity.CarDTO;
import com.example.uiproject.entity.CustomerDTO;
import com.example.uiproject.util.DataBaseHandler;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

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
    private DataBaseHandler dataBaseHandler;
    
    private ImageButton headerNotification, btnNameSearch;
    private ImageView iv_profile;
    private EditText editTextName;
    TextView seeAll1;
    TextView tv_location;
    TextView seeAll2;
    TextView soThongBaoMoi;
    CustomerDTO customer;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (getArguments() != null){
            customer = (CustomerDTO) getArguments().getSerializable("customer");
        }

        // Initialize UI components
        initUI(view);
        
        // Set up the recyclerviews with data
        setupBrandsRecyclerView();
        setupVehiclesRecyclerView();
        setupNewVehiclesRecyclerView();
        setUpNotification();
        
        return view;
    }

    private void setUpNotification (){
        String sql = " SELECT * FROM Notifications WHERE is_read = 0 AND idUser = " + customer.getId() + " ";
        Cursor cursor = dataBaseHandler.GetData(sql);
        Integer notifications = cursor.getCount();
        soThongBaoMoi.setText(notifications.toString());
    }
    
    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.rv_vehicles);
        newVehiclesRecyclerView = view.findViewById(R.id.rv_new_vehicles);
        brandsRecyclerView = view.findViewById(R.id.rv_brands);
        seeAll1 = view.findViewById(R.id.seeOn1);
        seeAll2 = view.findViewById(R.id.seeOn2);
        btnNameSearch = view.findViewById(R.id.btnNameSearch);
        editTextName = view.findViewById(R.id.editTextName);
        soThongBaoMoi = view.findViewById(R.id.badge_notification);
        iv_profile = view.findViewById(R.id.iv_profile);
        
        headerNotification = view.findViewById(R.id.iv_notification);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        dataBaseHandler = new DataBaseHandler(requireContext());

        if (customer != null){
            if (customer.getAvatar() != null && !customer.getAvatar().isEmpty()){
                String url = customer.getAvatar();
                Glide.with(requireContext())
                        .load(customer.getAvatar())
                        .placeholder(R.drawable.ic_person) // ảnh tạm khi đang load
                        .error(R.drawable.ic_person)
                        .override(50, 50)
                        .transform(new CircleCrop())// ảnh nếu load lỗi
                        .into(iv_profile); // ivProfile là ImageView bạn muốn    hiển thị
            }
        }


        tv_location = view.findViewById(R.id.tv_location);
        if (customer != null){
            String location = customer.getAddressDTO().getStreet() + ", " + customer.getAddressDTO().getDistrict();
            tv_location.setText(location);
        }
        
        // Set click listeners
        headerNotification.setOnClickListener(v -> {
            // Handle notification click
            NotificationFragment notificationFragment = new NotificationFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("customer", customer); // Đảm bảo customerDTO implements Serializable
            notificationFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, notificationFragment)
                    .addToBackStack(null)
                    .commit();
        });


        btnNameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFragmentCarWithName(editTextName.getText().toString());
            }
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

    private void OpenFragmentCarWithName (String name){
        Toast.makeText(getActivity(), "Tìm xe: " + name, Toast.LENGTH_SHORT).show();
        CarListFragment viewVehicles = new CarListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("nameKey",name);
        viewVehicles.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, viewVehicles);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

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

        // Hiển thị shimmer trong lúc chờ
        BrandShimmerAdapter shimmerAdapter = new BrandShimmerAdapter();
        brandsRecyclerView.setAdapter(shimmerAdapter);

        LoadCarBrands();  // call api update brandList no bi bat dong bo
    }
    
    private void setupVehiclesRecyclerView() {
        vehicleList = new ArrayList<>();
        
        // Setup horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        CarShimerAdapter carShimerAdapter = new CarShimerAdapter();
        recyclerView.setAdapter(carShimerAdapter);

        LoadSaleCar();
    }

    private void setupNewVehiclesRecyclerView() {
        newVehicleList = new ArrayList<>();
        
        // Add sample new vehicles

        
        // Setup horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        newVehiclesRecyclerView.setLayoutManager(layoutManager);

        CarShimerAdapter carShimerAdapter = new CarShimerAdapter();
        newVehiclesRecyclerView.setAdapter(carShimerAdapter);

        LoadNewCar();
    }

    public void LoadSaleCar() {
        apiService.getSaleCar().enqueue(new Callback<List<CarDTO>>() {
            @Override
            public void onResponse(Call<List<CarDTO>> call, Response<List<CarDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vehicleList.clear(); // Xóa danh sách cũ trước khi cập nhật
                    vehicleList.addAll(response.body()); // Cập nhật danh sách mới

                    // Create and set adapter
                    vehicleAdapter = new VehicleAdapter(getContext(), vehicleList);
                    vehicleAdapter.setOnVehicleClickListener(position -> {
                        CarDTO carDTO = vehicleList.get(position);
                        CarDetailsDialog dialog = CarDetailsDialog.newInstance(carDTO);
                        dialog.show(getActivity().getSupportFragmentManager(), "car_detail");
                    });

                    recyclerView.setAdapter(vehicleAdapter);
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

                    // Create and set adapter
                    newVehiclesAdapter = new VehicleAdapter(getContext(), newVehicleList);
                    newVehiclesAdapter.setOnVehicleClickListener(position -> {
                        CarDTO carDTO = newVehicleList.get(position);
                        CarDetailsDialog dialog = CarDetailsDialog.newInstance(carDTO);
                        dialog.show(getActivity().getSupportFragmentManager(), "car_detail");
                    });
                    newVehiclesRecyclerView.setAdapter(newVehiclesAdapter);

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