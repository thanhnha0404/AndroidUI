package com.example.uiproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.entity.ErrorResponseDTO;
import com.example.uiproject.entity.GGLoginRequest;
import com.example.uiproject.entity.ResultDTO;
import com.example.uiproject.util.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ApiService apiService;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private static final int RC_SIGN_IN = 1001;
    private Button btnGoogleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        apiService = RetrofitClient.getInstance().create(ApiService.class);
        Button loginButton = findViewById(R.id.btn_login);
        btnGoogleLogin = findViewById(R.id.btn_google_login);
        TextView forgotButton = findViewById(R.id.forgot_password);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_name = findViewById(R.id.et_email);
                EditText et_pass = findViewById(R.id.et_password);

                Map<String,String> loginRequest = new HashMap<>();
                loginRequest.put("email",et_name.getText().toString());
                loginRequest.put("password",et_pass.getText().toString());
                login(loginRequest);
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this,ForgotActivity.class);
                startActivity(intent);
            }
        });

        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signIn();
            }
        });


    }

    private void signIn (){
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Lấy ID token để gửi về server Spring Boot
            String personName = account.getDisplayName();
            String imgLink = account.getPhotoUrl().toString();
            String email = account.getEmail();

            GGLoginRequest ggLoginRequest = new GGLoginRequest();
            ggLoginRequest.setName(personName);
            ggLoginRequest.setAvt(imgLink);
            ggLoginRequest.setEmail(email);

            // TODO: Gửi idToken về server xác thực (qua Retrofit hoặc cách khác)
            loginGG(ggLoginRequest);

        } catch (ApiException e) {
            Toast.makeText(this,"Fail, code: " + e.getStatusCode(),Toast.LENGTH_LONG).show();
        }
    }

    private void loginGG (GGLoginRequest ggLoginRequest){
        apiService.loginGG(ggLoginRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Gson gson = new Gson();
                if (response.isSuccessful() && response.body() != null) {
                    // Phản hồi thành công
                    String json = gson.toJson(response.body());

                    //chuyen doi data cho dung
                    Type type = new TypeToken<ResultDTO<String>>() {}.getType();
                    ResultDTO<String> result = gson.fromJson(json, type);


                    String message = result.getMessage();
                    Toast.makeText(LoginActivity.this,message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();

                    String customerToken = (String) result.getData();
                    // luu customerToken vao session
                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                    sessionManager.saveToken(customerToken);

                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                    if (result.isStatus()) {
                        // mo form giao dien thue xe
                        intent.setClass(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    else{
                        // mo activity cap nhat thong tin dia chi do luoon
                        intent.setClass(LoginActivity.this, ProfileEditActivity.class);
                        startActivity(intent);
                    }
                } else {
                    // Nếu phản hồi không thành công, đọc từ errorBody
                    try {
                        String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                        if (!errorJson.isEmpty() && errorJson != null){
                            ErrorResponseDTO error = gson.fromJson(errorJson, ErrorResponseDTO.class);
                            // Hiển thị lỗi từ server
                            Toast.makeText(LoginActivity.this, error.getError(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Lỗi không xác định từ Server", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(Map<String,String> loginRequest){

        apiService.login(loginRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Gson gson = new Gson();
                if (response.isSuccessful() && response.body() != null) {
                    // Phản hồi thành công
                    String json = gson.toJson(response.body());

                    //chuyen doi data cho dung
                    Type type = new TypeToken<ResultDTO<String>>() {}.getType();
                    ResultDTO<String> result = gson.fromJson(json, type);


                    String message = result.getMessage();
                    Toast.makeText(LoginActivity.this,message, Toast.LENGTH_SHORT).show();

                    if (result.isStatus()) {

                        String customerToken = (String) result.getData();
                        // luu customerToken vao session
                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                        sessionManager.saveToken(customerToken);
                        // mo form giao dien thue xe

                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                    }
                } else {
                    // Nếu phản hồi không thành công, đọc từ errorBody
                    try {
                        String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                        if (!errorJson.isEmpty() && errorJson != null){
                            ErrorResponseDTO error = gson.fromJson(errorJson, ErrorResponseDTO.class);
                            // Hiển thị lỗi từ server
                            Toast.makeText(LoginActivity.this, error.getError(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Lỗi không xác định từ Server", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}