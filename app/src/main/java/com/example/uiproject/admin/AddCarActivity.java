package com.example.uiproject.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;
import com.example.uiproject.admin.model.AddCarRequest;
import com.example.uiproject.admin.model.Brand;
import com.example.uiproject.admin.model.ImagesAdapter;
import com.example.uiproject.admin.model.Line;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCarActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CODE = 100;

    private EditText nameEditText, identifyEditText, priceEditText, descriptionEditText;
    private EditText streetEditText, wardEditText, districtEditText, provinceEditText;
    private AutoCompleteTextView brandAutoComplete, modelAutoComplete, statusAutoComplete;
    private Button uploadImagesButton, addCarButton;
    private ImageButton backButton;
    private RecyclerView imagesRecyclerView;
    private ProgressBar uploadProgressBar;

    ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

    private List<String> uploadedImageUrls = new ArrayList<>();
    private ImagesAdapter imagesAdapter;

    private List<Uri> selectedImageUris = new ArrayList<>();

    private List<Brand> brandList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();
    private Long selectedBrandId;
    private Long selectedLineId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcar_activity);

        try {
            initViews();
            setupDropdowns();
            setupClickListeners();
            setupImagesRecyclerView();


            loadBrands();
            loadLines();

            brandAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                selectedBrandId = brandList.get(position).getId();
            });

            modelAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                selectedLineId = lineList.get(position).getId();
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupImagesRecyclerView() {
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            try {
                // Clear previous selections if you want to replace them
                // selectedImageUris.clear();

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

                // Use server-side upload instead of Cloudinary
                uploadImages(selectedImageUris);
            } catch (Exception e) {
                Toast.makeText(this, "Error selecting images: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
        try {
            // EditTexts
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
            addCarButton = findViewById(R.id.addCarButton);
            backButton = findViewById(R.id.backButton);


            uploadProgressBar = findViewById(R.id.uploadProgressBar);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupDropdowns() {
        try {
            String[] statuses = new String[]{"Active", "Renting", "Maintenance"};
            //String[] locations = new String[]{"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia"};

            ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
            statusAutoComplete.setAdapter(statusAdapter);

//            ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
//            locationAutoComplete.setAdapter(locationAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up dropdowns: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        try {
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
                    checkPermissionAndOpenPicker();
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
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up click listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionAndOpenPicker() {
        // For Android 13 and higher (API 33+), we need READ_MEDIA_IMAGES permission
        // For older versions, we use READ_EXTERNAL_STORAGE
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
            // For Android 12 and lower
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

    private boolean validateForm() {
        try {
            // Validate form fields
            if (nameEditText.getText().toString().trim().isEmpty()) {
                nameEditText.setError("Name is required");
                return false;
            }

            if (priceEditText.getText().toString().trim().isEmpty()) {
                priceEditText.setError("Price is required");
                return false;
            }

            if (selectedBrandId == null) {
                Toast.makeText(this, "Please select a brand", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (selectedLineId == null) {
                Toast.makeText(this, "Please select a model", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Validate address fields
            if (streetEditText.getText().toString().trim().isEmpty()) {
                streetEditText.setError("Street is required");
                return false;
            }

            if (wardEditText.getText().toString().trim().isEmpty()) {
                wardEditText.setError("Ward is required");
                return false;
            }

            if (districtEditText.getText().toString().trim().isEmpty()) {
                districtEditText.setError("District is required");
                return false;
            }

            if (provinceEditText.getText().toString().trim().isEmpty()) {
                provinceEditText.setError("Province is required");
                return false;
            }

            if (selectedImageUris.isEmpty()) {
                Toast.makeText(this, "Please upload at least one image", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (Exception e) {
            Toast.makeText(this, "Error validating form: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Method to use ApiService for uploading images
    private void uploadImages(List<Uri> imageUris) {
        if (imageUris.isEmpty()) {
            return;
        }

        try {
            Toast.makeText(this, "Preparing images for upload...", Toast.LENGTH_SHORT).show();
            uploadProgressBar.setVisibility(View.VISIBLE);

            // Process images in a background thread to avoid ANR
            new Thread(() -> {
                try {
                    List<MultipartBody.Part> imageParts = prepareImageParts(imageUris);

                    if (imageParts.isEmpty()) {
                        runOnUiThread(() -> {
                            uploadProgressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "No valid images to upload", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    // Execute on main thread
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Uploading images...", Toast.LENGTH_SHORT).show();

                        // Set a longer timeout for the image upload request
                        apiService.uploadMultipleImages(imageParts).enqueue(new Callback<List<String>>() {
                            @Override
                            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                                uploadProgressBar.setVisibility(View.GONE);
                                if (response.isSuccessful() && response.body() != null) {
                                    uploadedImageUrls.clear();
                                    uploadedImageUrls.addAll(response.body());
                                    Toast.makeText(AddCarActivity.this,
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

                                    Toast.makeText(AddCarActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<String>> call, Throwable t) {
                                uploadProgressBar.setVisibility(View.GONE);
                                String errorMessage = "Error uploading: ";

                                // Handle specific connection errors
                                if (t instanceof IOException) {
                                    if (t.getMessage() != null && t.getMessage().contains("Broken pipe")) {
                                        errorMessage += "Connection interrupted. Try uploading smaller images or check your internet connection.";
                                    } else {
                                        errorMessage += "Network error. Please check your connection and try again.";
                                    }
                                } else {
                                    errorMessage += t.getMessage();
                                }

                                Toast.makeText(AddCarActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(AddCarActivity.this,
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

    private List<MultipartBody.Part> prepareImageParts(List<Uri> imageUris) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (Uri uri : imageUris) {
            try {
                // Check if image is too large
                long fileSize = getFileSize(uri);
                if (fileSize > 5 * 1024 * 1024) { // 5MB limit
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Image too large. Compressing...", Toast.LENGTH_SHORT).show();
                    });
                    // Compress the image
                    byte[] compressedImage = compressImage(uri);
                    if (compressedImage != null) {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), compressedImage);
                        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
                        MultipartBody.Part part = MultipartBody.Part.createFormData("images", fileName, requestFile);
                        parts.add(part);
                        continue;
                    }
                }

                // Get the actual file path from the URI
                String filePath = getRealPathFromURI(uri);
                if (filePath != null) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                        MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                        parts.add(part);
                    }
                } else {
                    // Try to use the URI directly if file path can't be resolved
                    byte[] imageData = readInputStreamToByteArray(getContentResolver().openInputStream(uri));

                    // Compress if needed
                    if (imageData.length > 2 * 1024 * 1024) { // 2MB
                        imageData = compressBytes(imageData);
                    }

                    RequestBody requestFile = RequestBody.create(
                            MediaType.parse(getContentResolver().getType(uri) != null ?
                                    getContentResolver().getType(uri) : "image/jpeg"),
                            imageData
                    );
                    String fileName = "image_" + System.currentTimeMillis() + ".jpg";
                    MultipartBody.Part part = MultipartBody.Part.createFormData("images", fileName, requestFile);
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
            InputStream inputStream = getContentResolver().openInputStream(uri);
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
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            // Try reducing quality until size is acceptable
            while (outputStream.toByteArray().length > 1024 * 1024 && quality > 20) { // 1MB limit
                outputStream.reset();
                quality -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            }

            return outputStream.toByteArray();
        } catch (Exception e) {
            return imageData; // Return original if compression fails
        }
    }

    // Get actual file path from URI
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
            // If we can't resolve the path, return null
            return null;
        }

        return result;
    }

    private byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void saveCar() {
        try {
            Long price = Long.parseLong(priceEditText.getText().toString());

            // Get all address fields
            String street = streetEditText.getText().toString().trim();
            String ward = wardEditText.getText().toString().trim();
            String district = districtEditText.getText().toString().trim();
            String province = provinceEditText.getText().toString().trim();


            // Use Strings from the form
            String name = nameEditText.getText().toString().trim();
            String indentify = identifyEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String status = statusAutoComplete.getText().toString().trim();

            // Create the request object
            AddCarRequest carRequest = new AddCarRequest(
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
                    uploadedImageUrls
            );

            // Set identify if needed
            if (indentify.isEmpty()) {
                carRequest.setIndentify(name);
            } else {
                carRequest.setIndentify(indentify);
            }

            // Show loading indicator
            uploadProgressBar.setVisibility(View.VISIBLE);

            // Send the request
            apiService.createCar(carRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    uploadProgressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(AddCarActivity.this, "Car added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddCarActivity.this, "Failed to add car: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    uploadProgressBar.setVisibility(View.GONE);
                    Toast.makeText(AddCarActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            priceEditText.setError("Invalid price format");
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 