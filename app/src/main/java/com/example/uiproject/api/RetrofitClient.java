package com.example.uiproject.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Tích hợp CookieJar vào OkHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS) // Thời gian kết nối tối đa
                    .readTimeout(120, TimeUnit.SECONDS)    // Thời gian đọc dữ liệu tối đa
                    .writeTimeout(120, TimeUnit.SECONDS)   // Thời gian ghi dữ liệu tối đa
                    .cookieJar(new MyCookieJar())  // Thêm CookieJar vào OkHttpClient để quản lý cookies
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))  // Hiển thị headers và body khi debug
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            // Cấu hình Retrofit với OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://45a0-14-169-67-150.ngrok-free.app/") // Địa chỉ server
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Sử dụng Gson để chuyển đổi JSON
                    .build();
        }
        return retrofit;
    }
}
