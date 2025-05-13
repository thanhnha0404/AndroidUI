package com.example.uiproject;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.entity.ErrorResponseDTO;
import com.example.uiproject.entity.ResultDTO;
import com.example.uiproject.util.SessionManager;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {

    private ApiService apiService;
    EditText et_otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn_otp = findViewById(R.id.btn_verify);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_otp = findViewById(R.id.et_password_1);
                Map<String,String> verifyRequest = new HashMap<>();
                verifyRequest.put("otp",et_otp.getText().toString());

                SessionManager sessionManager = new SessionManager(OTPActivity.this);
                String email = sessionManager.getEmail();
                verifyRequest.put("email",email);

                optCheck(verifyRequest);
            }
        });

    }

    public void optCheck(Map<String, String> otpRequest) {


        // Tiến hành gửi request tới /api/otp với cookies
        apiService.otpCheck(otpRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Gson gson = new Gson();
                if (response.isSuccessful() && response.body() != null) {
                   String json = gson.toJson(response.body());
                   ResultDTO resultDTO = gson.fromJson(json, ResultDTO.class);
                   String message = resultDTO.getMessage();
                   Toast.makeText(OTPActivity.this, message, Toast.LENGTH_SHORT).show();
                   if (resultDTO.isStatus())
                   {
                       // luu OTP dung vao session
                       SessionManager sessionManager = new SessionManager(OTPActivity.this);
                       sessionManager.saveOTP(et_otp.getText().toString());
                       Intent intent = new Intent();
                       intent.setClass(OTPActivity.this,resetPassActivity.class);
                       startActivity(intent);
                   }
                }
                else{
                    try {
                        String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                        if (!errorJson.isEmpty() && errorJson != null){
                            ErrorResponseDTO errorResponseDTO = gson.fromJson(errorJson,ErrorResponseDTO.class);
                            Toast.makeText(OTPActivity.this, errorResponseDTO.getError(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(OTPActivity.this, "Lỗi không xác định từ Server", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(OTPActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}