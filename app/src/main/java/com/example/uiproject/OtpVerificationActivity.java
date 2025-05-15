package com.example.uiproject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiproject.admin.model.ResultDTO2;
import com.example.uiproject.admin.model.UserRequest;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String TAG = "OtpVerificationActivity";
    private static final long OTP_TIMER_DURATION = 60000; // 60 seconds

    // UI components
    private TextView emailTextView;
    private EditText otpCodeEditText;
    private Button verifyButton;
    private TextView resendOtpTextView;
    private TextView timerTextView;

    // Data
    private UserRequest userRequest;
    private ApiService apiService;
    private CountDownTimer otpTimer;
    private boolean canResendOtp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification_activity);

        apiService = RetrofitClient.getInstance().create(ApiService.class);

        // Lấy user từ Intent
        userRequest = (UserRequest) getIntent().getSerializableExtra("userRequest");
        if (userRequest == null) {
            Toast.makeText(this, "Error: User data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        startOtpTimer();
    }

    private void initializeViews() {
        emailTextView = findViewById(R.id.email_text_view);
        otpCodeEditText = findViewById(R.id.otp_edit_text);
        verifyButton = findViewById(R.id.verify_button);
        resendOtpTextView = findViewById(R.id.resend_text_view);
        timerTextView = findViewById(R.id.timer_text_view);

        emailTextView.setText(userRequest.getEmail());
    }

    private void setupListeners() {
        verifyButton.setOnClickListener(v -> {
            if (validateOtp()) {
                verifyOtp();
            }
        });

        resendOtpTextView.setOnClickListener(v -> {
            if (canResendOtp) {
                resendOtp();
            }
        });
    }

    private boolean validateOtp() {
        String otpCode = otpCodeEditText.getText().toString().trim();
        if (TextUtils.isEmpty(otpCode)) {
            otpCodeEditText.setError("OTP code is required");
            return false;
        }
        if (otpCode.length() != 6) {
            otpCodeEditText.setError("OTP code must be 6 digits");
            return false;
        }
        return true;
    }

    private void verifyOtp() {
        verifyButton.setEnabled(false);
        verifyButton.setText("Verifying...");

        String otpCode = otpCodeEditText.getText().toString().trim();
        userRequest.setOtp(otpCode); // 👈 thêm dòng này để set otp vào UserRequest

        apiService.verifyOtp(userRequest).enqueue(new Callback<ResultDTO2>() {
            @Override
            public void onResponse(Call<ResultDTO2> call, Response<ResultDTO2> response) {
                verifyButton.setEnabled(true);
                verifyButton.setText("Verify");

                if (response.isSuccessful() && response.body() != null) {
                    ResultDTO2 result = response.body();
                    if (result.isStatus()) {
                        Toast.makeText(OtpVerificationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        // navigateToLogin();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Failed to verify OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO2> call, Throwable t) {
                verifyButton.setEnabled(true);
                verifyButton.setText("Verify");
                Log.e(TAG, "API call failed", t);
                Toast.makeText(OtpVerificationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOtp() {
        canResendOtp = false;
        resendOtpTextView.setEnabled(false);
        resendOtpTextView.setAlpha(0.5f);

        Toast.makeText(this, "Resending OTP...", Toast.LENGTH_SHORT).show();

        apiService.register(userRequest).enqueue(new Callback<ResultDTO2>() {
            @Override
            public void onResponse(Call<ResultDTO2> call, Response<ResultDTO2> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResultDTO2 result = response.body();
                    if (result.isStatus()) {
                        Toast.makeText(OtpVerificationActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                        startOtpTimer();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                        enableResendOtp();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Failed to resend OTP. Please try again.", Toast.LENGTH_SHORT).show();
                    enableResendOtp();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO2> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                Toast.makeText(OtpVerificationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                enableResendOtp();
            }
        });
    }

    private void startOtpTimer() {
        if (otpTimer != null) {
            otpTimer.cancel();
        }

        canResendOtp = false;
        resendOtpTextView.setEnabled(false);
        resendOtpTextView.setAlpha(0.5f);

        otpTimer = new CountDownTimer(OTP_TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                timerTextView.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                enableResendOtp();
            }
        }.start();
    }

    private void enableResendOtp() {
        canResendOtp = true;
        resendOtpTextView.setEnabled(true);
        resendOtpTextView.setAlpha(1.0f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (otpTimer != null) {
            otpTimer.cancel();
        }
    }
}

