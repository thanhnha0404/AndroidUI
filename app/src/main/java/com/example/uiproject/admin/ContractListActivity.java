package com.example.uiproject.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;
import com.example.uiproject.admin.api.ApiServiceAdmin;
import com.example.uiproject.admin.api.RetrofitClientAdmin;
import com.example.uiproject.admin.model.ContractInfor;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContractListActivity extends AppCompatActivity {
    private RecyclerView contractRecyclerView;
    private ContractAdapter contractAdapter;
    private List<ContractInfor> contractList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_list_activity);

        contractRecyclerView = findViewById(R.id.contractRecyclerView);
        contractRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contractAdapter = new ContractAdapter(this, contractList);
        contractRecyclerView.setAdapter(contractAdapter);

        fetchContracts();
    }

    private void fetchContracts() {
        Retrofit retrofit = RetrofitClientAdmin.getInstance();
        ApiServiceAdmin apiService = retrofit.create(ApiServiceAdmin.class);
        apiService.getAllContract().enqueue(new Callback<List<ContractInfor>>() {
            @Override
            public void onResponse(Call<List<ContractInfor>> call, Response<List<ContractInfor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contractList.clear();
                    contractList.addAll(response.body());
                    contractAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ContractListActivity.this, "Không lấy được dữ liệu hợp đồng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ContractInfor>> call, Throwable t) {
                Toast.makeText(ContractListActivity.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 