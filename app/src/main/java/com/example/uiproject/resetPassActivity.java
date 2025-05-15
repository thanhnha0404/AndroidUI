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
import com.example.uiproject.util.OpenProgressDialog;
import com.example.uiproject.util.SessionManager;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class resetPassActivity extends AppCompatActivity {

    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button resetButton = findViewById(R.id.btn_verify);
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText passMain = findViewById(R.id.et_password_1);
                EditText passCheck = findViewById(R.id.et_password_2);
                SessionManager sessionManager = new SessionManager(resetPassActivity.this);
                String email = sessionManager.getEmail();
                String otp = sessionManager.getOTP();

                if (passMain.getText().toString().equals(passCheck.getText().toString())){
                    Map<String,String> resetRequest = new HashMap<>();
                    resetRequest.put("newPassword",passMain.getText().toString());
                    resetRequest.put("otp",otp);
                    resetRequest.put("email",email);
                    OpenProgressDialog.showProgressDialog(resetPassActivity.this);
                    resetPass(resetRequest);
                }
                else{
                    Toast.makeText(resetPassActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void resetPass( Map<String,String> resetRequest){
        apiService.resetPass(resetRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                OpenProgressDialog.hideProgressDialog();
                Gson gson = new Gson();
                if (response.isSuccessful() && response.body() != null){
                    String json = gson.toJson(response.body());
                    ResultDTO resultDTO = gson.fromJson(json,ResultDTO.class);
                    String message = resultDTO.getMessage();
                    Toast.makeText(resetPassActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (resultDTO.isStatus())
                    {
                        SessionManager sessionManager = new SessionManager(resetPassActivity.this);
                        sessionManager.logout(); // xoa het tat ca gia tri
                        Intent i = new Intent();
                        i.setClass(resetPassActivity.this,LoginActivity.class);
                        startActivity(i);
                    }

                }
                else{
                    try {
                        String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                        if (!errorJson.isEmpty() && errorJson != null){
                            ErrorResponseDTO errorResponseDTO = gson.fromJson(errorJson,ErrorResponseDTO.class);
                            Toast.makeText(resetPassActivity.this, errorResponseDTO.getError(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(resetPassActivity.this, "Lỗi không xác định từ Server", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(resetPassActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                OpenProgressDialog.hideProgressDialog();
                Toast.makeText(resetPassActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}