package com.example.uiproject.admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiproject.R;
import com.example.uiproject.admin.model.ResultDTO;
import com.example.uiproject.admin.model.UserRequest;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    // UI components
    private EditText emailEditText, passwordEditText, dobEditText;
    private EditText streetEditText, wardEditText, districtEditText, provinceEditText;
    private RadioGroup sexRadioGroup;
    private Button avatarButton, registerButton;
    private ImageView avatarImageView;
    private ProgressBar avatarProgressBar;

    // Data
    private Uri avatarUri;
    private String avatarUrl; // To store the URL returned from server
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Initialize API service
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        // Initialize UI components
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        // EditTexts
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        dobEditText = findViewById(R.id.dobEditText);
        streetEditText = findViewById(R.id.streetEditText);
        wardEditText = findViewById(R.id.wardEditText);
        districtEditText = findViewById(R.id.districtEditText);
        provinceEditText = findViewById(R.id.provinceEditText);

        // Radio Group
        sexRadioGroup = findViewById(R.id.sexRadioGroup);

        // Buttons
        avatarButton = findViewById(R.id.avatarButton);
        registerButton = findViewById(R.id.registerButton);

        // ImageView
        avatarImageView = findViewById(R.id.avatarImageView);

        // ProgressBar
        avatarProgressBar = findViewById(R.id.avatarProgressBar);
        if (avatarProgressBar == null) {
            // If the progress bar doesn't exist in the layout, create it programmatically
            avatarProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
            avatarProgressBar.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        // Date picker for DoB field
        dobEditText.setOnClickListener(v -> showDatePickerDialog());

        // Avatar selection
        avatarButton.setOnClickListener(v -> openImagePicker());

        // Register button
        registerButton.setOnClickListener(v -> {
            if (validateForm()) {
                requestOtp();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Email validation
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            isValid = false;
        }

        // Password validation
        String password = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            isValid = false;
        }

        // DoB validation
        String dob = dobEditText.getText().toString().trim();
        if (TextUtils.isEmpty(dob)) {
            dobEditText.setError("Date of Birth is required");
            isValid = false;
        }

        // Sex validation
        if (sexRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your sex", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Address fields validation
        if (TextUtils.isEmpty(streetEditText.getText().toString().trim())) {
            streetEditText.setError("Street is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(wardEditText.getText().toString().trim())) {
            wardEditText.setError("Ward is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(districtEditText.getText().toString().trim())) {
            districtEditText.setError("District is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(provinceEditText.getText().toString().trim())) {
            provinceEditText.setError("Province is required");
            isValid = false;
        }

        // Avatar validation (optional in this case)
        if (avatarUri == null) {
            Toast.makeText(this, "Please select an avatar image", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
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
                    dobEditText.setText(dateFormatter.format(selectedDate.getTime()));
                },
                year, month, day);

        // Set maximum date to current date (no future dates)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            avatarUri = data.getData();
            try {
                // Display the selected image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), avatarUri);
                avatarImageView.setImageBitmap(bitmap);

                // Upload to server instead of converting to base64
                uploadAvatarToServer(avatarUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadAvatarToServer(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        avatarProgressBar.setVisibility(View.VISIBLE);
        avatarButton.setEnabled(false);
        Toast.makeText(this, "Uploading avatar...", Toast.LENGTH_SHORT).show();

        // Create a list with single image for the upload
        List<MultipartBody.Part> imageParts = new ArrayList<>();

        // Process image in a background thread to avoid ANR
        new Thread(() -> {
            try {
                // Get file path from URI
                String filePath = getRealPathFromURI(imageUri);

                if (filePath != null) {
                    // Create file from path
                    File file = new File(filePath);
                    if (file.exists()) {
                        // Check if the file is too large and compress if needed
                        if (file.length() > 1024 * 1024) { // If larger than 1MB
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream); // Compress to 70% quality
                            byte[] imageData = byteArrayOutputStream.toByteArray();

                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageData);
                            MultipartBody.Part part = MultipartBody.Part.createFormData("images", "avatar_" + System.currentTimeMillis() + ".jpg", requestFile);
                            imageParts.add(part);
                        } else {
                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                            MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                            imageParts.add(part);
                        }
                    }
                } else {
                    // If can't get file path, use URI directly
                    byte[] imageData = getBytesFromUri(imageUri);

                    // Compress the image if it's too large
                    if (imageData.length > 1024 * 1024) { // If larger than 1MB
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream); // Compress to 70% quality
                        imageData = byteArrayOutputStream.toByteArray();
                    }

                    RequestBody requestFile = RequestBody.create(
                            MediaType.parse(getContentResolver().getType(imageUri) != null ?
                                    getContentResolver().getType(imageUri) : "image/jpeg"),
                            imageData
                    );
                    String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
                    MultipartBody.Part part = MultipartBody.Part.createFormData("images", fileName, requestFile);
                    imageParts.add(part);
                }

                // Execute on main thread
                runOnUiThread(() -> {
                    // Send the image to server with a custom timeout
                    // Use server-side upload endpoint
                    Call<List<String>> call = apiService.uploadMultipleImages(imageParts);

                    call.enqueue(new Callback<List<String>>() {
                        @Override
                        public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                            avatarProgressBar.setVisibility(View.GONE);
                            avatarButton.setEnabled(true);

                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                // Get the URL of uploaded image
                                avatarUrl = response.body().get(0);
                                Toast.makeText(RegisterActivity.this, "Avatar uploaded successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = "Failed to upload avatar";
                                try {
                                    if (response.errorBody() != null) {
                                        errorMsg += ": " + response.errorBody().string();
                                    }
                                } catch (IOException e) {
                                    errorMsg += ": " + response.code();
                                }
                                Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<String>> call, Throwable t) {
                            avatarProgressBar.setVisibility(View.GONE);
                            avatarButton.setEnabled(true);
                            Log.e(TAG, "Upload failed", t);
                            Toast.makeText(RegisterActivity.this, "Error uploading avatar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    avatarProgressBar.setVisibility(View.GONE);
                    avatarButton.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        try (java.io.InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                throw new IOException("Failed to open input stream for URI");
            }

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        }
        return byteBuffer.toByteArray();
    }

    private Date parseDate(String dateStr) {
        try {
            return dateFormatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void requestOtp() {
        // Show loading indicator
        registerButton.setEnabled(false);
        registerButton.setText("Sending OTP...");

        // Create user request
        UserRequest userRequest = createUserRequest();

        // Call API to request OTP
        apiService.register(userRequest).enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                registerButton.setEnabled(true);
                registerButton.setText("Receive OTP");

                if (response.isSuccessful() && response.body() != null) {
                    ResultDTO result = response.body();
                    if (result.isStatus()) {
                        // OTP sent successfully, navigate to OTP verification screen
                        navigateToOtpVerification(userRequest);
                    } else {
                        // Show error message
                        Toast.makeText(RegisterActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle API error
                    Toast.makeText(RegisterActivity.this, "Failed to request OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                registerButton.setEnabled(true);
                registerButton.setText("Receive OTP");
                Log.e(TAG, "API call failed", t);
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private UserRequest createUserRequest() {
        UserRequest userRequest = new UserRequest();

        // Basic info
        userRequest.setEmail(emailEditText.getText().toString().trim());
        userRequest.setPassword(passwordEditText.getText().toString().trim());
        userRequest.setDateOfBirth(parseDate(dobEditText.getText().toString().trim()));

        // Sex
        int selectedId = sexRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        userRequest.setSex(selectedRadioButton.getText().toString());

        // Avatar - use URL instead of base64
        userRequest.setAvatar(avatarUrl);

        // Address
        userRequest.setStreet(streetEditText.getText().toString().trim());
        userRequest.setWard(wardEditText.getText().toString().trim());
        userRequest.setDistrict(districtEditText.getText().toString().trim());
        userRequest.setProvince(provinceEditText.getText().toString().trim());

        return userRequest;
    }

    private void navigateToOtpVerification(UserRequest userRequest) {
        Intent intent = new Intent(this, OtpVerificationActivity.class);
        intent.putExtra("userRequest", userRequest);
        startActivity(intent);
    }
}
