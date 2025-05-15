package com.example.uiproject.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;
import com.example.uiproject.admin.api.ApiServiceAdmin;
import com.example.uiproject.admin.api.RetrofitClientAdmin;
import com.example.uiproject.admin.model.ContractInfor;
import com.example.uiproject.admin.model.ResultDTO2;
import com.example.uiproject.admin.model.UpdateContractRequest;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {
    private final List<ContractInfor> contractList;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final ApiServiceAdmin apiService;

    public ContractAdapter(Context context, List<ContractInfor> contractList) {
        this.context = context;
        this.contractList = contractList;
        this.apiService = RetrofitClientAdmin.getInstance().create(ApiServiceAdmin.class);
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contract, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        ContractInfor contract = contractList.get(position);
        holder.tvCustomerName.setText("Tên khách hàng: " + contract.getNameCustomer());
        holder.tvCarName.setText("Xe: " + contract.getNameCar());
        String dateStr = "Từ: " + dateFormat.format(contract.getDateFrom()) + " - Đến: " + dateFormat.format(contract.getDateTo());
        holder.tvDate.setText(dateStr);
        holder.tvPrice.setText("Giá: " + String.format("%,d VND", contract.getPrice()));
        
        // Trạng thái
        holder.tvStatus.setText("Trạng thái:");
        
        // Spinner trạng thái
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.contract_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerStatus.setAdapter(adapter);

        // Set spinner selection theo status
        int spinnerPosition = adapter.getPosition(contract.getStatus());
        if (spinnerPosition >= 0) {
            holder.spinnerStatus.setSelection(spinnerPosition);
        }

        // Xử lý sự kiện thay đổi trạng thái
        holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newStatus = parent.getItemAtPosition(position).toString();
                if (!newStatus.equals(contract.getStatus())) {
                    updateContractStatus(contract.getId(), newStatus);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần xử lý
            }
        });
    }

    private void updateContractStatus(Long contractId, String newStatus) {
        UpdateContractRequest request = new UpdateContractRequest();
        request.setId(contractId);
        request.setStatus(newStatus);

        apiService.updateStatus(request).enqueue(new Callback<ResultDTO2>() {
            @Override
            public void onResponse(Call<ResultDTO2> call, Response<ResultDTO2> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO2> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }

    public static class ContractViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCarName, tvDate, tvPrice, tvStatus;
        Spinner spinnerStatus;
        public ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
        }
    }
} 