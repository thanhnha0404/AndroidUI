package com.example.uiproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uiproject.R;
import com.example.uiproject.adapter.ContractAdapter;
import com.example.uiproject.adapter.ContractShimmerAdapter;
import com.example.uiproject.api.ApiService;
import com.example.uiproject.api.RetrofitClient;
import com.example.uiproject.entity.ContractDTO;
import com.example.uiproject.entity.CustomerDTO;
import com.example.uiproject.entity.PaymentResDTO;
import com.example.uiproject.util.OpenProgressDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout emptyState;
    private ContractAdapter contractAdapter;
    private CustomerDTO customerDTO;
    private List<ContractDTO> list;
    private ApiService apiService;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Lấy dữ liệu từ arguments
        if (getArguments() != null){
            customerDTO = (CustomerDTO) getArguments().getSerializable("customer");
        }
        // Initialize views
        rvHistory = view.findViewById(R.id.rvHistory);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        emptyState = view.findViewById(R.id.emptyState);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        list = new ArrayList<>();

        // Setup RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setHasFixedSize(true);
        contractAdapter = new ContractAdapter(getContext(), list, contract -> {
            // Xử lý thanh toán tại đây
            goToPayment(contract);

        });

        // Setup SwipeRefreshLayout
        swipeRefresh.setOnRefreshListener(this::loadContract);

        loadContract();

        return view;
    }


    private void loadContract (){
        if (customerDTO == null ) return;
        Long id = Long.parseLong(customerDTO.getId().toString());
        rvHistory.setAdapter(new ContractShimmerAdapter());
        apiService.getContractByCusId(id).enqueue(new Callback<List<ContractDTO>>() {
            @Override
            public void onResponse(Call<List<ContractDTO>> call, Response<List<ContractDTO>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    rvHistory.setAdapter(contractAdapter);
                    contractAdapter.notifyDataSetChanged();
                    toggleEmptyState(list.isEmpty());
                }
                else {
                    toggleEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<List<ContractDTO>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                toggleEmptyState(true);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToPayment (ContractDTO contractDTO){
        Long id = contractDTO.getId();
        Long price = contractDTO.getPrice();
        apiService.getPayment(id,price).enqueue(new Callback<PaymentResDTO>() {
            @Override
            public void onResponse(Call<PaymentResDTO> call, Response<PaymentResDTO> response) {
                if (response.isSuccessful() && response.body() != null){
                    Log.d("DEBUG_URL", new Gson().toJson(response.body()));
                    PaymentResDTO paymentResDTO = response.body();

                    // tooi muon goi PaymentFragment
                    PaymentFragment paymentFragment = new PaymentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("payment_url", paymentResDTO.getURL());
                    paymentFragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, paymentFragment)
                            .addToBackStack(null)// `fragment_container` là ID layout chứa Fragment
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<PaymentResDTO> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            rvHistory.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvHistory.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

}