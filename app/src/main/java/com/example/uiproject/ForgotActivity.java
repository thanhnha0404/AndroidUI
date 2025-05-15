package com.example.uiproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.entity.CustomerDTO;
import com.example.uiproject.entity.ErrorResponseDTO;
import com.example.uiproject.entity.ResultDTO;
import com.example.uiproject.util.OpenProgressDialog;
import com.example.uiproject.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends AppCompatActivity {

    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button = findViewById(R.id.btn_verify);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_name = findViewById(R.id.et_password_1);

                Map<String,String> forgotRequest = new HashMap<>();
                forgotRequest.put("email",et_name.getText().toString());

                OpenProgressDialog.showProgressDialog(ForgotActivity.this ); // Hiển thị ProgressDialog

                forgot(forgotRequest);
            }
        });

    }


    private void forgot(Map<String,String> forgotRequest){

        apiService.forgot(forgotRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                OpenProgressDialog.hideProgressDialog();
                Gson gson = new Gson();
                if (response.isSuccessful() && response.body() != null) {

                    String json = gson.toJson(response.body());
                    //chuyen doi data cho dung
                    Type type = new TypeToken<ResultDTO<CustomerDTO>>() {}.getType();
                    ResultDTO<CustomerDTO> result = gson.fromJson(json, type);
                    String message = result.getMessage();
                    Toast.makeText(ForgotActivity.this,  message, Toast.LENGTH_SHORT).show();

                    if (result.isStatus()){
                        // lay email user chuyen len cho phien
                        CustomerDTO customerDTO = (CustomerDTO)result.getData();
                        SessionManager sessionManager = new SessionManager(ForgotActivity.this);
                        sessionManager.saveEmail(customerDTO.getEmail());

                        Intent intent = new Intent();
                        intent.setClass(ForgotActivity.this,OTPActivity.class);
                        startActivity(intent);
                    }

                } else {
                    try {
                        String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                        ErrorResponseDTO error = gson.fromJson(errorJson, ErrorResponseDTO.class);
                        // Hiển thị lỗi từ server
                        Toast.makeText(ForgotActivity.this, error.getError(), Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ForgotActivity.this, "Lỗi khi đọc phản hồi lỗi từ server", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                OpenProgressDialog.hideProgressDialog();
                Toast.makeText(ForgotActivity.this, "Lỗi kết nối: " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
