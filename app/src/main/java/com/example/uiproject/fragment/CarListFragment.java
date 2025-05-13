package com.example.uiproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.dialog.CarDetailsDialog;
import com.example.uiproject.dialog.FilterDialogFragment;
import com.example.uiproject.R;
import com.example.uiproject.adapter.CarAdapter;
import com.example.uiproject.dialog.SearchResultDialog;
import com.example.uiproject.entity.CarBrandDTO;
import com.example.uiproject.entity.CarDTO;
import com.example.uiproject.model.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarListFragment extends Fragment implements CarAdapter.OnCarClickListener, FilterDialogFragment.FilterDialogListener {

    private RecyclerView carsRecyclerView;
    private CarAdapter carAdapter;
    private List<CarDTO> carList;
    private EditText searchEditText;
    private ImageButton filterButton;
    private TextView recommendedTextView;
    CarBrandDTO brand;
    private ApiService apiService;

    Map<String,Object> params = new HashMap<>();

    // Filter parameters
    private String currentLine = "";
    private String currentBrand = "";
    private String currentLocation = "";
    private Long currentMinPrice = 0L;
    private Long currentMaxPrice = 30000000L;


    public CarListFragment() {
        // Required empty public constructor
    }

    public static CarListFragment newInstance() {
        return new CarListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        brand = getArguments() != null ? (CarBrandDTO) getArguments().getSerializable("brand") : null;
        carsRecyclerView = view.findViewById(R.id.carsRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        filterButton = view.findViewById(R.id.filterButton);
        recommendedTextView = view.findViewById(R.id.recommendedTextView);
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        if (brand != null){
            recommendedTextView.setText(brand.getName());
        }
        else {
            recommendedTextView.setText("All Car");
        }

        // Set up filter button click listener
        filterButton.setOnClickListener(v -> {
            // Show filter dialog
            showFilterDialog();
        });

        // Set up RecyclerView
        setupRecyclerView();

        if ( brand != null && brand.getId() != null){
            loadCarOfBrand();
        }
        else{
            loadAllCar();
        }
    }

    private void updateCarList(List<CarDTO> newCars) {
        carList.clear();
        if (newCars != null) {
            carList.addAll(newCars);
        }
        carAdapter.notifyDataSetChanged();
    }


    private void loadAllCar (){
         apiService.getAllCar().enqueue(new Callback<List<CarDTO>>() {
             @Override
             public void onResponse(Call<List<CarDTO>> call, Response<List<CarDTO>> response) {
                 if (response.isSuccessful() && response.body() != null) {
                     updateCarList(response.body());
                     Log.e("API_RESPONSE", "Số lượng xe: " + carList.size());
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

    private void showFilterDialog() {
        FilterDialogFragment dialog = FilterDialogFragment.newInstance();
        dialog.setListener(this);
        dialog.show(getParentFragmentManager(), "FilterDialog");
    }

    private void setupRecyclerView() {
        carList = new ArrayList<>();
        carAdapter = new CarAdapter(carList, this);
        
        // Set up grid layout with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        carsRecyclerView.setLayoutManager(layoutManager);
        carsRecyclerView.setAdapter(carAdapter);
    }

    private void loadCarOfBrand() {
        // Sample car data
       apiService.getAllCarOfBrand(brand.getId()).enqueue(new Callback<List<CarDTO>>() {
           @Override
           public void onResponse(Call<List<CarDTO>> call, Response<List<CarDTO>> response) {
               if (response.isSuccessful() && response.body() != null) {
                   updateCarList(response.body());
                   Log.e("API_RESPONSE", "Số lượng xe cua hang: " + carList.size());
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

    @Override
    public void onCarClick(int position) {
        CarDTO car = carList.get(position);
        // Show car details dialog
        CarDetailsDialog detailsDialog = CarDetailsDialog.newInstance(car);
        detailsDialog.show(getParentFragmentManager(), "car_detail");
    }

    @Override
    public void onFavoriteClick(int position) {
        CarDTO car = carList.get(position);
        // Toggle favorite status
        car.setFavorite(!car.isFavorite());
        carAdapter.notifyItemChanged(position);
        
        String message = car.isFavorite() ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFilterApplied(String model, String brand, String location, Long minPrice, Long maxPrice) {
        // Save current filter parameters
        this.currentLine = model;
        this.currentBrand = brand;
        this.currentLocation = location;
        this.currentMinPrice = minPrice;
        this.currentMaxPrice = maxPrice;

        if(currentBrand != null && !currentBrand.equals("")){
            recommendedTextView.setText(currentBrand);
        }
        else{
            recommendedTextView.setText("ALL CAR");
        }

        params.clear();
        params.put("line",currentLine);
        params.put("brand",currentBrand);
        params.put("location",currentLocation);
        params.put("carPriceFrom",currentMinPrice);
        params.put("carPriceTo",currentMaxPrice);


        apiService.findCar(params).enqueue(new Callback<List<CarDTO>>() {
            @Override
            public void onResponse(Call<List<CarDTO>> call, Response<List<CarDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateCarList(response.body());
                    Log.e("API_RESPONSE", "Số lượng xe sau khi tim kiem: " + carList.size());
                    String message = "Đã tìm thấy xe thỏa mãn";
                    if (carList.size() ==  0){
                        message = "Không tìm thấy xe thỏa mãn";
                    }
                    SearchResultDialog dialog = new SearchResultDialog(requireContext());
                    dialog.showResult(message, carList.size());
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