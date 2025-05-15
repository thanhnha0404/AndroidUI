package com.example.uiproject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.entity.CustomerDTO;
import com.example.uiproject.util.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {

    private ImageView background;
    private AppCompatButton button;
    SessionManager sessionManager;
    ApiService apiService;
    CustomerDTO cus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        background = findViewById(R.id.backgroundImage);
        button = findViewById(R.id.btnStart);
        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getInstance().create(ApiService.class);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCustomer();
            }
        });
    }


    private void loadCustomer() {
        Map<String, Object> headers = new HashMap<>();
        String authToken = sessionManager.getCustomerToken();
        if (authToken != null && !authToken.equals("")) {
            headers.put("Authorization", authToken);
            apiService.getUser(headers).enqueue(new Callback<CustomerDTO>() {
                @Override
                public void onResponse(Call<CustomerDTO> call, Response<CustomerDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        cus = response.body();
                        Intent i = new Intent();
                        i.setClass(StartActivity.this, HomeActivity.class);
                        startActivity(i);
                    } else {
                        GoToLogin();
                    }
                }

                @Override
                public void onFailure(Call<CustomerDTO> call, Throwable t) {
                   GoToLogin();
                }
            });
        } else {
            GoToLogin();
        }
    }

    private void GoToLogin (){
        sessionManager.logout();
        Intent i = new Intent();
        i.setClass(StartActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }
}


