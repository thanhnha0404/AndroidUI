package com.example.uiproject.admin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.uiproject.R;
import com.example.uiproject.admin.api.ApiServiceAdmin;
import com.example.uiproject.admin.api.RetrofitClientAdmin;
import com.example.uiproject.admin.model.Brand;
import com.example.uiproject.admin.model.ResultDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBrandActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CODE = 100;

    private ImageButton backButton;
    private ImageView brandImageView;
    private Button uploadImageButton, updateBrandButton, deleteBrandButton;
    private EditText nameEditText, codeEditText, descriptionEditText;
    private AutoCompleteTextView statusAutoCompleteTextView;
    private ProgressBar uploadProgressBar;

    private ApiServiceAdmin apiServiceAdmin;
    private Uri selectedImageUri;
    private String uploadedImageUrl;
    private Brand brand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_brand_activity);

        initViews();
        apiServiceAdmin = RetrofitClientAdmin.getInstance().create(ApiServiceAdmin.class);
        
        // Get brand data from intent
        String brandJson = getIntent().getStringExtra("brand");
        if (brandJson != null) {
            brand = new Gson().fromJson(brandJson, Brand.class);
            bindBrandToViews();
        } else {
            Toast.makeText(this, "Error: No brand data found", Toast.LENGTH_SHORT).show();
            finish();
        }
        
        setupListeners();
        setupStatusDropdown();
        Log.d("Brand", "ID: " + brand.getId());
        Log.d("Brand", "Name: " + brand.getName());
        Log.d("Brand", "Description: " + brand.getDescription());
        Log.d("Brand", "Code: " + brand.getCode());
        Log.d("Brand", "Status: " + brand.getStatus());

    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        brandImageView = findViewById(R.id.brandImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        updateBrandButton = findViewById(R.id.updateBrandButton);
        deleteBrandButton = findViewById(R.id.deleteBrandButton);
        nameEditText = findViewById(R.id.nameEditText);
        codeEditText = findViewById(R.id.codeEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        statusAutoCompleteTextView = findViewById(R.id.statusAutoCompleteTextView);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);
    }
    
    private void bindBrandToViews() {
        nameEditText.setText(brand.getName());
        codeEditText.setText(brand.getCode());
        descriptionEditText.setText(brand.getDescription());
        statusAutoCompleteTextView.setText(brand.getStatus(), false);
        uploadedImageUrl = brand.getLogo();
        
        // Load brand logo
        if (brand.getLogo() != null && !brand.getLogo().isEmpty()) {
            Glide.with(this)
                    .load(brand.getLogo())
                    .into(brandImageView);
        }
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());

        uploadImageButton.setOnClickListener(v -> checkPermissionAndOpenPicker());

        updateBrandButton.setOnClickListener(v -> validateAndUpdateBrand());
        
        deleteBrandButton.setOnClickListener(v -> confirmAndDeleteBrand());
    }

    private void setupStatusDropdown() {
        String[] statuses = new String[]{"Active", "InActive"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        statusAutoCompleteTextView.setAdapter(statusAdapter);
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
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Show selected image in ImageView
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(brandImageView);
                
                // Upload the image
                uploadImage(selectedImageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        uploadProgressBar.setVisibility(View.VISIBLE);
        
        try {
            String filePath = getRealPathFromURI(imageUri);
            java.io.File file;
            RequestBody requestFile;
            
            if (filePath != null) {
                file = new java.io.File(filePath);
                requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
            } else {
                byte[] imageData = readInputStreamToByteArray(getContentResolver().openInputStream(imageUri));
                file = createTempFileFromBytes(imageData);
                requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
            }
            
            MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
            
            // Create a list to use with uploadMultipleImages
            List<MultipartBody.Part> parts = new ArrayList<>();
            parts.add(body);

            apiServiceAdmin.uploadMultipleImages(parts).enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    uploadProgressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        uploadedImageUrl = response.body().get(0);
                        Toast.makeText(EditBrandActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditBrandActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    uploadProgressBar.setVisibility(View.GONE);
                    Toast.makeText(EditBrandActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            uploadProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error preparing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private java.io.File createTempFileFromBytes(byte[] bytes) throws IOException {
        java.io.File tempFile = java.io.File.createTempFile("image", ".jpg", getCacheDir());
        java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile);
        fos.write(bytes);
        fos.close();
        return tempFile;
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

    private byte[] readInputStreamToByteArray(java.io.InputStream inputStream) throws IOException {
        java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void validateAndUpdateBrand() {
        String name = nameEditText.getText().toString().trim();
        String code = codeEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String status = statusAutoCompleteTextView.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            return;
        }

        if (code.isEmpty()) {
            codeEditText.setError("Code is required");
            return;
        }

        if (status.isEmpty()) {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uploadedImageUrl == null || uploadedImageUrl.isEmpty()) {
            Toast.makeText(this, "Please upload a brand logo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update brand object
        brand.setName(name);
        brand.setCode(code);
        brand.setDescription(description);
        brand.setStatus(status);
        brand.setLogo(uploadedImageUrl);
        
        // Show progress
        uploadProgressBar.setVisibility(View.VISIBLE);
        
        // Submit to API
        apiServiceAdmin.updateBrand(brand).enqueue(new Callback<ResultDTO<Brand>>() {
            @Override
            public void onResponse(Call<ResultDTO<Brand>> call, Response<ResultDTO<Brand>> response) {
                uploadProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(EditBrandActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to update brand";
                    Toast.makeText(EditBrandActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO<Brand>> call, Throwable t) {
                uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(EditBrandActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void confirmAndDeleteBrand() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa hãng xe " + brand.getName() + "?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBrand();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void deleteBrand() {
        uploadProgressBar.setVisibility(View.VISIBLE);
        
        apiServiceAdmin.deleteBrand(brand.getId()).enqueue(new Callback<ResultDTO<Brand>>() {
            @Override
            public void onResponse(Call<ResultDTO<Brand>> call, Response<ResultDTO<Brand>> response) {
                uploadProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(EditBrandActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to delete brand";
                    Toast.makeText(EditBrandActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO<Brand>> call, Throwable t) {
                uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(EditBrandActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 