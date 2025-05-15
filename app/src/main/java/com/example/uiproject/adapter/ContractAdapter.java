package com.example.uiproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;
import com.example.uiproject.entity.ContractDTO;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.List;

public class ContractAdapter extends  RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {
    private List<ContractDTO> contractList;
    private Context context;
    private OnPayClickListener onPayClickListener;

    public interface OnPayClickListener {
        void onPayClicked(ContractDTO contract);
    }

    public ContractAdapter(Context context, List<ContractDTO> contractList, OnPayClickListener onPayClickListener) {
        this.context = context;
        this.contractList = contractList;
        this.onPayClickListener = onPayClickListener;
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contract_history, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        ContractDTO contract = contractList.get(position);

        String address = contract.getCars().get(0).getAddressDTO().getStreet() + ", " + contract.getCars().get(0).getAddressDTO().getDistrict();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fromDate = sdf.format(contract.getDateFrom());
        String toDate = sdf.format(contract.getDateTo());
        String raw = String.format("%,d", contract.getPrice());
        String priceFormatted = raw.replace(',', '.');


        holder.tvContractId.setText("Contract #" + contract.getId());
        holder.tvCustomerName.setText("Customer: " + contract.getCustomerDTO().getName());
        holder.tvCarName.setText("Car: " + contract.getCars().get(0).getName());
        holder.tvPickupLocation.setText(address);
        holder.tvDateRange.setText(fromDate + " - " + toDate);
        holder.tvPrice.setText(priceFormatted);

        if (contract.getPaymentDTO() == null) {
            holder.tvStatus.setText("Unpaid");
            holder.tvStatus.setBackgroundResource(R.drawable.status_unpaid_background);
            holder.btnPay.setVisibility(View.VISIBLE);
            holder.btnPay.setOnClickListener(v -> {
                if (onPayClickListener != null) {
                    onPayClickListener.onPayClicked(contract);
                }
            });
        } else {
            holder.tvStatus.setText("Paid");
            holder.tvStatus.setBackgroundResource(R.drawable.status_paid_background);
            holder.btnPay.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }

    static class ContractViewHolder extends RecyclerView.ViewHolder {
        TextView tvContractId, tvStatus, tvCustomerName, tvCarName, tvPickupLocation, tvDateRange, tvPrice;
        MaterialButton btnPay;

        ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContractId = itemView.findViewById(R.id.tvContractId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvPickupLocation = itemView.findViewById(R.id.tvPickupLocation);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            btnPay = itemView.findViewById(R.id.btnPay);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
