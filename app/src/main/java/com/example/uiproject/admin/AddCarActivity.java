package com.example.uiproject.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiproject.R;
import com.example.uiproject.admin.model.AddCarRequest;
import com.example.uiproject.admin.model.Brand;
import com.example.uiproject.admin.model.ImagesAdapter;
import com.example.uiproject.admin.model.Line;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCarActivity extends AppCompatActivity {

    private EditText nameEditText, priceEditText, descriptionEditText;
    private AutoCompleteTextView brandAutoComplete, modelAutoComplete, statusAutoComplete, locationAutoComplete;
    private Button uploadImagesButton, addCarButton;
    private ImageButton backButton;
    ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

    private List<String> uploadedImageUrls = new ArrayList<>();
    private ImagesAdapter imagesAdapter; // Bạn cần tự tạo adapter này nếu muốn hiển thị ảnh

    private List<Uri> selectedImageUris = new ArrayList<>();

    private List<Brand> brandList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();
    private Long selectedBrandId;
    private Long selectedLineId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcar_activity);



        initViews();
        setupDropdowns();
        setupClickListeners();
        loadBrands();
        loadLines();

        brandAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedBrandId = brandList.get(position).getId();
        });

        modelAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedLineId = lineList.get(position).getId();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedImageUris.clear();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                selectedImageUris.add(imageUri);
            }

            // Upload lên server
            uploadImages(selectedImageUris);
        }
    }

    private void loadBrands() {
        apiService.getAllBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brandList.clear();
                    brandList.addAll(response.body());

                    // Tạo mảng brand name
                    String[] brands = new String[brandList.size()];
                    for (int i = 0; i < brandList.size(); i++) {
                        brands[i] = brandList.get(i).getName();
                    }

                    // Gán adapter sau khi có dữ liệu
                    ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(
                            AddCarActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            brands
                    );
                    brandAutoComplete.setAdapter(brandAdapter);
                } else {
                    Toast.makeText(AddCarActivity.this,
                            "Failed to load brands: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                Toast.makeText(AddCarActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLines() {
        // Gọi API lấy danh sách các dòng xe
        apiService.getAllLines().enqueue(new Callback<List<Line>>() {
            @Override
            public void onResponse(Call<List<Line>> call, Response<List<Line>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lineList.clear();
                    lineList.addAll(response.body());

                    // Tạo mảng tên các dòng xe
                    String[] lines = new String[lineList.size()];
                    for (int i = 0; i < lineList.size(); i++) {
                        lines[i] = lineList.get(i).getName(); // Lấy tên dòng xe từ object Line
                    }

                    // Gán adapter sau khi có dữ liệu dòng xe
                    ArrayAdapter<String> lineAdapter = new ArrayAdapter<>(
                            AddCarActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            lines // Sử dụng mảng lines chứa tên các dòng xe
                    );
                    modelAutoComplete.setAdapter(lineAdapter); // Đặt adapter vào AutoCompleteTextView modelAutoComplete
                } else {
                    // Thông báo lỗi khi không tải được dữ liệu dòng xe
                    Toast.makeText(AddCarActivity.this,
                            "Failed to load lines: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Line>> call, Throwable t) {
                // Thông báo lỗi khi có sự cố trong quá trình gọi API
                Toast.makeText(AddCarActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void initViews() {
        // EditTexts
        nameEditText = findViewById(R.id.nameEditText);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        
        // AutoCompleteTextViews
        brandAutoComplete = findViewById(R.id.brandAutoCompleteTextView);
        modelAutoComplete = findViewById(R.id.modelAutoCompleteTextView);
        statusAutoComplete = findViewById(R.id.statusAutoCompleteTextView);
        locationAutoComplete = findViewById(R.id.locationAutoCompleteTextView);
        
        // Buttons
        uploadImagesButton = findViewById(R.id.uploadImagesButton);
        addCarButton = findViewById(R.id.addCarButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupDropdowns() {

        String[] statuses = new String[]{"Có sẵn", "Đang được thuê", "Đang bảo trì"};
        String[] locations = new String[]{"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia"};

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        statusAutoComplete.setAdapter(statusAdapter);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        locationAutoComplete.setAdapter(locationAdapter);
    }



    private void setupClickListeners() {
        // Back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

        // Upload images button click listener
        uploadImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }

        });


        // Add car button click listener
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    saveCar();
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 100);
    }


    private boolean validateForm() {
        boolean isValid = true;

        // Check if required fields are filled
        if (nameEditText.getText().toString().trim().isEmpty()) {
            nameEditText.setError("Name is required");
            isValid = false;
        }

        if (brandAutoComplete.getText().toString().trim().isEmpty()) {
            brandAutoComplete.setError("Brand is required");
            isValid = false;
        }

        if (modelAutoComplete.getText().toString().trim().isEmpty()) {
            modelAutoComplete.setError("Model is required");
            isValid = false;
        }

        if (statusAutoComplete.getText().toString().trim().isEmpty()) {
            statusAutoComplete.setError("Status is required");
            isValid = false;
        }

        if (priceEditText.getText().toString().trim().isEmpty()) {
            priceEditText.setError("Price is required");
            isValid = false;
        }

        return isValid;
    }

    private void uploadImages(List<Uri> imageUris) {
        List<MultipartBody.Part> imageParts = new ArrayList<>();

        for (Uri uri : imageUris) {
            File file = new File(FileUtils.getPath(this, uri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
            imageParts.add(body);
        }

        apiService.uploadMultipleImages(imageParts).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    uploadedImageUrls.clear();
                    uploadedImageUrls.addAll(response.body());
                    imagesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddCarActivity.this, "Failed to upload images", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(AddCarActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<MultipartBody.Part> prepareImageParts(List<Uri> imageUris) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (Uri uri : imageUris) {
            String filePath = FileUtils.getPath(this, uri); // FileUtils bạn sẽ tạo ở bước sau
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
            parts.add(body);
        }
        return parts;
    }



    private void saveCar() {

        AddCarRequest car = new AddCarRequest();
        car.setName(nameEditText.getText().toString().trim());
        car.setBrandId(selectedBrandId);
        car.setIndentify(descriptionEditText.getText().toString().trim());
        car.setLineId(selectedLineId);
        car.setStatus(statusAutoComplete.getText().toString().trim());
        car.setProvince(locationAutoComplete.getText().toString().trim());
        car.setPrice(Long.parseLong(priceEditText.getText().toString().trim()));
        car.setDescription(descriptionEditText.getText().toString().trim());
        car.setImageUrls(uploadedImageUrls);

        apiService.createCar(car).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(AddCarActivity.this, "Car added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddCarActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        // TODO: Save car data to database or send to server

        // Display success message
        Toast.makeText(this, "Car added successfully", Toast.LENGTH_SHORT).show();
        
        // Go back to car manager activity
        finish();
    }


} 