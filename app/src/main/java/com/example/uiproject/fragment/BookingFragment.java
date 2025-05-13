package com.example.uiproject.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.uiproject.HomeActivity;
import com.example.uiproject.LoginActivity;
import com.example.uiproject.R;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.entity.AddressDTO;
import com.example.uiproject.entity.BookingRequest;
import com.example.uiproject.entity.CarDTO;
import com.example.uiproject.entity.ContractDTO;
import com.example.uiproject.entity.CustomerDTO;
import com.example.uiproject.entity.ErrorResponseDTO;
import com.example.uiproject.entity.PaymentResDTO;
import com.example.uiproject.entity.ResultDTO;
import com.example.uiproject.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingFragment extends Fragment {
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    
    private Date fromDate;
    private Date toDate;
    private CarDTO car;

    private ImageView ivCar;

    // Views
    private TextView tvCarName;
    private TextView tvPrice;
    private TextView tvFromDate;
    private TextView tvToDate;
    private TextView tvTotalPrice;
    private TextView tvTotalAmount;
    private TextView tvSelectedCount;
    private TextView tvDiscount;
    private RatingBar ratingBar;
    private Button btnConfirm;
    private TextView tvAddress;
    private ApiService apiService;
    private SessionManager sessionManager;
    private Long finalCost = 0L;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        tvCarName = view.findViewById(R.id.tvCarName);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvFromDate = view.findViewById(R.id.tvFromDate);
        tvToDate = view.findViewById(R.id.tvToDate);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvSelectedCount = view.findViewById(R.id.tvSelectedCount);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        ratingBar = view.findViewById(R.id.ratingBar);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        ivCar = view.findViewById(R.id.ivCar);

        if (getArguments() != null){
            car = (CarDTO)getArguments().getSerializable("carDTO");
        }

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupInitialUI();
        setupDatePickers();
    }

    private void setupInitialUI() {

        if (car == null){
            // Đóng fragment hiện tại nếu không có dữ liệu xe
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }
        // set up cho cai xe ne
        tvCarName.setText(car.getName());

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);  // Bật tính năng ngắt nhóm số
        String formattedPrice = numberFormat.format(car.getPrice());

        tvPrice.setText(formattedPrice + "/day");
        ratingBar.setRating(4f);
        tvDiscount.setText(car.getDiscount() + "%");
        AddressDTO addressDTO = car.getAddressDTO();
        tvAddress.setText(addressDTO.getStreet() + " " + addressDTO.getDistrict());

        if (!car.getPictures().isEmpty()){
            Glide.with(requireContext())
                    .load(car.getPictures().get(0))
                    .placeholder(R.drawable.car_background)
                    .error(R.drawable.car_background)
                    .into(ivCar);
        }

        // Set initial prices
        updateTotalAmount();

        btnConfirm.setOnClickListener(v -> {
            // goi API tao hop dong
            createContract();
        });

    }

    private void createContract (){
        Map<String,Object> headers = new HashMap<>();
        String authToken = sessionManager.getCustomerToken();
        headers.put("Authorization",authToken);

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setCarId(car.getId());
        bookingRequest.setDateFrom(fromDate);
        bookingRequest.setDateTo(toDate);
        bookingRequest.setPrice(finalCost);

        apiService.book(bookingRequest,headers).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Gson gson = new Gson();
                if (response.isSuccessful() && response.body() != null){
                    // Phản hồi thành công
                    String json = gson.toJson(response.body());
                    Type type = new TypeToken<ResultDTO<ContractDTO>>() {}.getType();
                    ResultDTO<ContractDTO> result = gson.fromJson(json, type);

                    String message = result.getMessage();
                    if (message == null || message.trim().isEmpty()) {
                        message = "Có lỗi xảy ra!"; // thông báo mặc định
                    }
                    Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show();

                    if (result.isStatus()) {
                        ContractDTO contractDTO = (ContractDTO) result.getData();
                        // Hiển thị dialog xác nhận
                        new AlertDialog.Builder(getContext())
                                .setTitle("Bạn có muốn thanh toán ngay?")
                                .setMessage("Nếu bạn không thanh toán trước ngày nhận xe thì hợp đồng sẽ bị hủy.")
                                .setPositiveButton("Thanh toán", (dialog, which) -> {
                                    goToPayment(contractDTO);
                                    dialog.dismiss();
                                })
                                .setNegativeButton("Không", (dialog, which) -> {
                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .remove(BookingFragment.this)  // Loại bỏ fragment
                                            .commit();
                                    dialog.dismiss();
                                })
                                .show();
                    }
                }
                else{
                    // Nếu phản hồi không thành công, đọc từ errorBody
                    try {
                        String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                        if (!errorJson.isEmpty() && errorJson != null){
                            ErrorResponseDTO error = gson.fromJson(errorJson, ErrorResponseDTO.class);
                            // Hiển thị lỗi từ server
                            String message = error.getError();
                            if (message == null || message.trim().isEmpty()) {
                                message = "Có lỗi xảy ra!"; // thông báo mặc định
                            }
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(requireContext(), "Lỗi không xác định từ Server", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToPayment (ContractDTO contractDTO){
        Long id = contractDTO.getId();
        Long price = contractDTO.getPrice();
        apiService.getPayment(id,price).enqueue(new Callback<PaymentResDTO>() {
            @Override
            public void onResponse(Call<PaymentResDTO> call, Response<PaymentResDTO> response) {
                if (response.isSuccessful() && response.body() != null){
                    Log.d("DEBUG_URL", new Gson().toJson(response.body()));
                    PaymentResDTO paymentResDTO = response.body();

                    // tooi muon goi PaymentFragment
                    PaymentFragment paymentFragment = new PaymentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("payment_url", paymentResDTO.getURL());
                    paymentFragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, paymentFragment) // `fragment_container` là ID layout chứa Fragment
                            .commit();  // KHÔNG gọi addToBackStack(null)
                }
            }

            @Override
            public void onFailure(Call<PaymentResDTO> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupDatePickers() {
        tvFromDate.setOnClickListener(v -> showDatePicker((TextView) v, true));
        tvToDate.setOnClickListener(v -> showDatePicker((TextView) v, false));
    }


    private void showDatePicker(TextView textView, boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, day) -> {
                calendar.set(year, month, day);
                Date date = calendar.getTime();
                
                if (isFromDate) {
                    fromDate = date;
                    if (toDate != null && date.after(toDate)) {
                        calendar.setTime(date);
                        calendar.add(Calendar.DAY_OF_MONTH,1);
                        toDate = calendar.getTime();
                        tvToDate.setText(dateFormatter.format(calendar.getTime()));
                    }
                } else {
                    if (fromDate != null && date.before(fromDate)) {
                        calendar.setTime(fromDate);
                        calendar.add(Calendar.DAY_OF_MONTH,1);
                        toDate = calendar.getTime();
                        date = calendar.getTime();
                    }
                    toDate = date;
                }
                
                textView.setText(dateFormatter.format(date));
                updateTotalAmount();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        if (isFromDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else if (fromDate != null) {
            datePickerDialog.getDatePicker().setMinDate(fromDate.getTime());
        }
        
        datePickerDialog.show();
    }

    private void updateTotalAmount() {
        if (fromDate == null || toDate == null) return;


        Long daysCount = (toDate.getTime() - fromDate.getTime()) / (24 * 60 * 60 * 1000) + 1;
        Long total = car.getPrice() * daysCount;
        Long finalPrice = (total * (100 - Long.parseLong(car.getDiscount())))/100;
        finalCost = finalPrice;

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);  // Bật tính năng ngắt nhóm số
        String formattedTotalPrice = numberFormat.format(total);
        String formattedFinalPrice = numberFormat.format(finalPrice);

        tvSelectedCount.setText(String.valueOf(daysCount));
        tvTotalPrice.setText(formattedTotalPrice);
        tvTotalAmount.setText(formattedFinalPrice);

    }
} 