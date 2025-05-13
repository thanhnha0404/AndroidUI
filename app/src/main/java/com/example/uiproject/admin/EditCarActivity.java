package com.example.uiproject.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;
import com.example.uiproject.admin.api.ApiServiceAdmin;
import com.example.uiproject.admin.api.RetrofitClientAdmin;
import com.example.uiproject.admin.model.AddCarRequest;
import com.example.uiproject.admin.model.Brand;
import com.example.uiproject.admin.model.CarDTO;
import com.example.uiproject.admin.model.ImagesAdapter;
import com.example.uiproject.admin.model.Line;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCarActivity extends AppCompatActivity {
    private EditText nameEditText, identifyEditText, priceEditText, descriptionEditText;
    private EditText streetEditText, wardEditText, districtEditText, provinceEditText;
    private AutoCompleteTextView brandAutoComplete, modelAutoComplete, statusAutoComplete;
    private Button uploadImagesButton, updateCarButton, deleteCarButton;
    private ImageButton backButton;
    private RecyclerView imagesRecyclerView;
    private ProgressBar uploadProgressBar;
    private ImagesAdapter imagesAdapter;
    private List<String> imageUrls = new ArrayList<>();
    private CarDTO car;
    private ApiServiceAdmin apiServiceAdmin;
    private List<Brand> brandList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();
    private Long selectedBrandId;
    private Long selectedLineId;
    private String selectedStatus;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CODE = 100;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private List<String> uploadedImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_car_activity);
        initViews();
        apiServiceAdmin = RetrofitClientAdmin.getInstance().create(ApiServiceAdmin.class);
        loadBrands();
        loadLines();
        setupStatusDropdown();
        setupImagesRecyclerView();
        // Nhận dữ liệu CarDTO từ intent
        String carJson = getIntent().getStringExtra("car");
        if (carJson != null) {
            car = new Gson().fromJson(carJson, CarDTO.class);
            bindCarToViews();
        }
        setupClickListeners();
    }

    private void initViews() {
        nameEditText = findViewById(R.id.nameEditText);
        identifyEditText = findViewById(R.id.identifyEditText);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        streetEditText = findViewById(R.id.streetEditText);
        wardEditText = findViewById(R.id.wardEditText);
        districtEditText = findViewById(R.id.districtEditText);
        provinceEditText = findViewById(R.id.provinceEditText);
        brandAutoComplete = findViewById(R.id.brandAutoCompleteTextView);
        modelAutoComplete = findViewById(R.id.modelAutoCompleteTextView);
        statusAutoComplete = findViewById(R.id.statusAutoCompleteTextView);
        uploadImagesButton = findViewById(R.id.uploadImagesButton);
        updateCarButton = findViewById(R.id.updateCarButton);
        deleteCarButton = findViewById(R.id.deleteCarButton);
        backButton = findViewById(R.id.backButton);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void bindCarToViews() {
        nameEditText.setText(car.getName());
        identifyEditText.setText(car.getIndentify());
        priceEditText.setText(car.getPrice() != null ? String.valueOf(car.getPrice()) : "");
        descriptionEditText.setText(car.getDescription());
        // Hiển thị địa chỉ từ 4 trường mới
        provinceEditText.setText(car.getProvince() != null ? car.getProvince() : "");
        districtEditText.setText(car.getDistrict() != null ? car.getDistrict() : "");
        wardEditText.setText(car.getWard() != null ? car.getWard() : "");
        streetEditText.setText(car.getStreet() != null ? car.getStreet() : "");
        // brand, line, status
        brandAutoComplete.setText(car.getBrand(), false);
        modelAutoComplete.setText(car.getLine(), false);
        statusAutoComplete.setText(car.getStatus(),false);
        // Ảnh
        selectedImageUris.clear();
        uploadedImageUrls.clear();
        if (car.getPictures() != null) {
            for (String url : car.getPictures()) {
                if (url != null && !url.isEmpty()) {
                    selectedImageUris.add(android.net.Uri.parse(url));
                    uploadedImageUrls.add(url);
                }
            }
        }
        if (imagesAdapter != null) imagesAdapter.notifyDataSetChanged();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        updateCarButton.setOnClickListener(v -> updateCar());
        uploadImagesButton.setOnClickListener(v -> checkPermissionAndOpenPicker());
        deleteCarButton.setOnClickListener(v -> deleteCar());
    }

    private void loadBrands() {
        apiServiceAdmin.getAllBrands().enqueue(new retrofit2.Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, retrofit2.Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brandList.clear();
                    brandList.addAll(response.body());
                    String[] brands = new String[brandList.size()];
                    for (int i = 0; i < brandList.size(); i++) {
                        brands[i] = brandList.get(i).getName();
                        if (car != null && car.getBrand() != null && car.getBrand().equals(brandList.get(i).getName())) {
                            selectedBrandId = brandList.get(i).getId();
                        }
                    }
                    ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(EditCarActivity.this, android.R.layout.simple_dropdown_item_1line, brands);
                    brandAutoComplete.setAdapter(brandAdapter);
                    brandAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                        selectedBrandId = brandList.get(position).getId();
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {}
        });
    }

    private void loadLines() {
        apiServiceAdmin.getAllLines().enqueue(new retrofit2.Callback<List<Line>>() {
            @Override
            public void onResponse(Call<List<Line>> call, retrofit2.Response<List<Line>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lineList.clear();
                    lineList.addAll(response.body());
                    String[] lines = new String[lineList.size()];
                    for (int i = 0; i < lineList.size(); i++) {
                        lines[i] = lineList.get(i).getName();
                        if (car != null && car.getLine() != null && car.getLine().equals(lineList.get(i).getName())) {
                            selectedLineId = lineList.get(i).getId();
                        }
                    }
                    ArrayAdapter<String> lineAdapter = new ArrayAdapter<>(EditCarActivity.this, android.R.layout.simple_dropdown_item_1line, lines);
                    modelAutoComplete.setAdapter(lineAdapter);
                    modelAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                        selectedLineId = lineList.get(position).getId();
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Line>> call, Throwable t) {}
        });
    }

    private void setupStatusDropdown() {
        String[] statuses = new String[]{"Active", "Renting", "Maintenance"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        statusAutoComplete.setAdapter(statusAdapter);
        statusAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedStatus = statuses[position];
        });
    }

    private void setupImagesRecyclerView() {
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ImagesAdapter(this, selectedImageUris);
        imagesAdapter.setOnRemoveClickListener(position -> {
            if (position >= 0 && position < selectedImageUris.size()) {
                selectedImageUris.remove(position);
                if (position < uploadedImageUrls.size()) {
                    uploadedImageUrls.remove(position);
                }
                imagesAdapter.notifyDataSetChanged();
            }
        });
        imagesRecyclerView.setAdapter(imagesAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            try {
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
                imagesAdapter.notifyDataSetChanged();
                uploadImages(selectedImageUris);
            } catch (Exception e) {
                Toast.makeText(this, "Error selecting images: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermissionAndOpenPicker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), IMAGE_PICK_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening image picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImages(List<Uri> imageUris) {
        if (imageUris.isEmpty()) {
            return;
        }
        try {
            Toast.makeText(this, "Preparing images for upload...", Toast.LENGTH_SHORT).show();
            uploadProgressBar.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    List<okhttp3.MultipartBody.Part> imageParts = prepareImageParts(imageUris);
                    if (imageParts.isEmpty()) {
                        runOnUiThread(() -> {
                            uploadProgressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "No valid images to upload", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Uploading images...", Toast.LENGTH_SHORT).show();
                        apiServiceAdmin.uploadMultipleImages(imageParts).enqueue(new Callback<List<String>>() {
                            @Override
                            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                                uploadProgressBar.setVisibility(View.GONE);
                                if (response.isSuccessful() && response.body() != null) {
                                    uploadedImageUrls.clear();
                                    uploadedImageUrls.addAll(response.body());
                                    Toast.makeText(EditCarActivity.this,
                                            "Images uploaded successfully",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    String errorMsg = "Failed to upload images";
                                    try {
                                        if (response.errorBody() != null) {
                                            errorMsg += ": " + response.errorBody().string();
                                        } else {
                                            errorMsg += ": " + response.code() + " " + response.message();
                                        }
                                    } catch (Exception e) {
                                        errorMsg += ": " + response.code();
                                    }
                                    Toast.makeText(EditCarActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<List<String>> call, Throwable t) {
                                uploadProgressBar.setVisibility(View.GONE);
                                Toast.makeText(EditCarActivity.this, "Error uploading: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(EditCarActivity.this,
                                "Error preparing images: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        } catch (Exception e) {
            uploadProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Upload error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private List<okhttp3.MultipartBody.Part> prepareImageParts(List<Uri> imageUris) {
        List<okhttp3.MultipartBody.Part> parts = new ArrayList<>();
        for (Uri uri : imageUris) {
            try {
                long fileSize = getFileSize(uri);
                if (fileSize > 5 * 1024 * 1024) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Image too large. Compressing...", Toast.LENGTH_SHORT).show();
                    });
                    byte[] compressedImage = compressImage(uri);
                    if (compressedImage != null) {
                        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), compressedImage);
                        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
                        okhttp3.MultipartBody.Part part = okhttp3.MultipartBody.Part.createFormData("images", fileName, requestFile);
                        parts.add(part);
                        continue;
                    }
                }
                String filePath = getRealPathFromURI(uri);
                if (filePath != null) {
                    java.io.File file = new java.io.File(filePath);
                    if (file.exists()) {
                        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), file);
                        okhttp3.MultipartBody.Part part = okhttp3.MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                        parts.add(part);
                    }
                } else {
                    byte[] imageData = readInputStreamToByteArray(getContentResolver().openInputStream(uri));
                    if (imageData.length > 2 * 1024 * 1024) {
                        imageData = compressBytes(imageData);
                    }
                    okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(
                            okhttp3.MediaType.parse(getContentResolver().getType(uri) != null ?
                                    getContentResolver().getType(uri) : "image/jpeg"),
                            imageData
                    );
                    String fileName = "image_" + System.currentTimeMillis() + ".jpg";
                    okhttp3.MultipartBody.Part part = okhttp3.MultipartBody.Part.createFormData("images", fileName, requestFile);
                    parts.add(part);
                }
            } catch (Exception e) {
                final String errorMsg = e.getMessage();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error preparing image: " + errorMsg, Toast.LENGTH_SHORT).show();
                });
            }
        }
        return parts;
    }

    private long getFileSize(Uri uri) {
        try {
            return getContentResolver().openFileDescriptor(uri, "r").getStatSize();
        } catch (Exception e) {
            return -1;
        }
    }

    private byte[] compressImage(Uri uri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            byte[] inputData = readInputStreamToByteArray(inputStream);
            return compressBytes(inputData);
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] compressBytes(byte[] imageData) {
        try {
            int quality = 80;
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, outputStream);
            while (outputStream.toByteArray().length > 1024 * 1024 && quality > 20) {
                outputStream.reset();
                quality -= 10;
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, outputStream);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            return imageData;
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String result = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    private byte[] readInputStreamToByteArray(java.io.InputStream inputStream) throws java.io.IOException {
        java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void deleteCar(){
        Long id = car.getId();
        apiServiceAdmin.deleteCar(id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditCarActivity.this, "Xóa xe thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditCarActivity.this, "Xóa xe thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(EditCarActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCar() {

        String name = nameEditText.getText().toString().trim();
        String indentify = identifyEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String status = statusAutoComplete.getText().toString().trim();
        String province = provinceEditText.getText().toString().trim();
        String street = streetEditText.getText().toString().trim();
        String ward = wardEditText.getText().toString().trim();
        String district = districtEditText.getText().toString().trim();
        Long price = Long.parseLong(priceEditText.getText().toString().trim());

        List<String> pictures = new ArrayList<>(uploadedImageUrls);
        AddCarRequest request = new AddCarRequest(
            car.getId(),
            name,
            description,
            status,
            indentify,
            province,
            street,
            district,
            ward,
            selectedBrandId,
            selectedLineId,
            price,
            pictures
        );
        uploadProgressBar.setVisibility(android.view.View.VISIBLE);
        apiServiceAdmin.updateCar(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                uploadProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(EditCarActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditCarActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(EditCarActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
