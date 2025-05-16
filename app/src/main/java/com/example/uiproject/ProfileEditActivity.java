package com.example.uiproject;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.dialog.ReloginDialog;
import com.example.uiproject.entity.CustomerDTO;
import com.example.uiproject.entity.ErrorResponseDTO;
import com.example.uiproject.entity.ResultDTO;
import com.example.uiproject.entity.updateProfileRequest;
import com.example.uiproject.util.SessionManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private EditText emailEditText, tdobEditText, streetEditText, wardEditText, districtEditText, provinceEditText;
    private Button avatarButton, btnSave;
    private RadioGroup sexs;
    private ImageView avatarImageView;

    private Uri selectedImageUri;
    private ProgressBar progressBar;
    private String urlImg;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private CustomerDTO cus = null;
    SessionManager sessionManager;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_information);

        initViews();
        setupClickListeners();
        loadUserData();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        tdobEditText = findViewById(R.id.dobEditText);
        streetEditText = findViewById(R.id.streetEditText);
        wardEditText = findViewById(R.id.wardEditText);
        districtEditText = findViewById(R.id.districtEditText);
        provinceEditText = findViewById(R.id.provinceEditText);
        avatarButton = findViewById(R.id.avatarButton);
        btnSave = findViewById(R.id.saveButton);
        sexs = findViewById(R.id.sexRadioGroup);
        avatarImageView = findViewById(R.id.avatarImageView);
        progressBar = findViewById(R.id.avatarProgressBar);
        sessionManager = new SessionManager(ProfileEditActivity.this);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    private void setupClickListeners() {

        tdobEditText.setOnClickListener(v -> showDatePickerDialog());

        avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 chooseImage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

    }

    private void performUpdateProfile (){
        updateProfileRequest userRequest = new updateProfileRequest();
        // Basic info
        userRequest.setEmail(emailEditText.getText().toString().trim());
        userRequest.setDateOfBirth(parseDate(tdobEditText.getText().toString().trim()));

        // Sex
        int selectedId = sexs.getCheckedRadioButtonId();
        if (selectedId  != -1){
            RadioButton selectedRadioButton = findViewById(selectedId);
            userRequest.setSex(selectedRadioButton.getText().toString());
        }


        // Avatar - use URL instead of base64
        userRequest.setAvatar(urlImg);

        // Address
        userRequest.setStreet(streetEditText.getText().toString().trim());
        userRequest.setWard(wardEditText.getText().toString().trim());
        userRequest.setDistrict(districtEditText.getText().toString().trim());
        userRequest.setProvince(provinceEditText.getText().toString().trim());

        Map<String,Object> headers = new HashMap<>();
        String authToken = sessionManager.getCustomerToken();

        if (authToken != null && !authToken.equals("")){
            headers.put("Authorization",authToken);
            apiService.updateProfile(userRequest,headers).enqueue(new Callback<ResultDTO>() {
                @Override
                public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                    Gson gson = new Gson();
                    if (response.isSuccessful() && response.body() != null){
                        ResultDTO resultDTO = response.body();
                        String message = resultDTO.getMessage();
                        Toast.makeText(ProfileEditActivity.this, message , Toast.LENGTH_SHORT).show();
                        if (resultDTO.isStatus()){
                            // vao trang chinh
                            Intent i = new Intent();
                            i.setClass(ProfileEditActivity.this,HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                    else{
                        try {
                            String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                            int statusCode = response.code();
                            if (!errorJson.isEmpty() && errorJson != null){
                                ErrorResponseDTO error = gson.fromJson(errorJson, ErrorResponseDTO.class);
                                // Hiển thị lỗi từ server
                                Toast.makeText(ProfileEditActivity.this, error.getError(), Toast.LENGTH_SHORT).show();
                                if (statusCode == 401) {
                                    // Gọi logout hoặc điều hướng về màn đăng nhập
                                    Logout();
                                }
                            }
                            else{
                                Toast.makeText(ProfileEditActivity.this, "Lỗi không xác định từ Server", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResultDTO> call, Throwable t) {
                    Toast.makeText(ProfileEditActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(ProfileEditActivity.this, "Phiên đăng nhập hết hạn vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Logout();
        }


        // call API o day
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    tdobEditText.setText(dateFormatter.format(selectedDate.getTime()));
                },
                year, month, day);

        // Set maximum date to current date (no future dates)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateProfile (){
        if (selectedImageUri != null) {
            uploadImage(selectedImageUri);
        }
        else{
            performUpdateProfile();
        }

    }
    private Date parseDate(String dateStr) {
        try {
            return dateFormatter.parse(dateStr);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE);
    }

    private void uploadImage(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);

        MediaManager.get().upload(imageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        progressBar.setVisibility(View.GONE);
                        urlImg = resultData.get("url").toString();
                        urlImg = updateURL (urlImg);
                        performUpdateProfile();

                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileEditActivity.this, "Lỗi upload hình ảnh: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    private String updateURL (String url){
        if (url == null) return null;
        return url.replaceFirst("^http://", "https://");
    }

    private void loadUserData() {
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
                        setInforCus(cus);
                    }
                    else{
                        Toast.makeText(ProfileEditActivity.this, "Phiên đăng nhập hết hạn vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                        Logout();
                    }
                }

                @Override
                public void onFailure(Call<CustomerDTO> call, Throwable t) {
                    Toast.makeText(ProfileEditActivity.this, "Phiên đăng nhập hết hạn vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                    Logout();
                }
            });
        }
        else{
            Toast.makeText(ProfileEditActivity.this, "Phiên đăng nhập hết hạn vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Logout();
        }
    }

    private void setInforCus (CustomerDTO cus){
        if (cus.getEmail() != null) {
            emailEditText.setText(cus.getEmail());
        }

        urlImg = cus.getAvatar();
        if (urlImg != null) {
            Glide.with(ProfileEditActivity.this)
                    .load(urlImg)
                    .override(500, 500)
                    .placeholder(R.drawable.car_background)
                    .error(R.drawable.car_background)
                    .into(avatarImageView);
        }

        if (cus.getDateOfBirth() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formatted = dateFormat.format(cus.getDateOfBirth());
            tdobEditText.setText(formatted);
        }

        String sex = cus.getSex();
        if (sex != null && !sex.isEmpty()) {
            for (int i = 0; i < sexs.getChildCount(); i++) {
                View view = sexs.getChildAt(i);
                if (view instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) view;
                    if (radioButton.getText().toString().equalsIgnoreCase(sex)) {
                        radioButton.setChecked(true);
                        break;
                    }
                }
            }
        }

        if (cus.getAddressDTO() != null) {
            if (cus.getAddressDTO().getStreet() != null)
                streetEditText.setText(cus.getAddressDTO().getStreet());

            if (cus.getAddressDTO().getWard() != null)
                wardEditText.setText(cus.getAddressDTO().getWard());

            if (cus.getAddressDTO().getDistrict() != null)
                districtEditText.setText(cus.getAddressDTO().getDistrict());

            if (cus.getAddressDTO().getProvince() != null)
                provinceEditText.setText(cus.getAddressDTO().getProvince());
        }

    }

    private void Logout (){
        ReloginDialog dialog = new ReloginDialog(this);
        dialog.setOnReloginClickListener(() -> {
            sessionManager.logout();
            Intent i = new Intent();
            i.setClass(ProfileEditActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        });
        dialog.showReloginMessage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            avatarImageView.setImageURI(selectedImageUri);
        }
    }


} 