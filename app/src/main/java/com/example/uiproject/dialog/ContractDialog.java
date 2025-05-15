package com.example.uiproject.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.uiproject.R;

public class ContractDialog extends Dialog {
    private Long contractId;
    private String customerName;
    private String carName;
    private String startDate;
    private String endDate;
    private String location;

    public ContractDialog(Context context, Long contractId, String customerName,
                         String carName, String startDate, String endDate, String location) {
        super(context);
        this.contractId = contractId;
        this.customerName = customerName;
        this.carName = carName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_contract_success);
        
        // Set transparent background
        // Set transparent background
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Set width = MATCH_PARENT, height = WRAP_CONTENT (hoặc theo nhu cầu)
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }

        // Initialize views
        TextView tvContractId = findViewById(R.id.tvContractId);
        TextView tvCustomerName = findViewById(R.id.tvCustomerName);
        TextView tvCarName = findViewById(R.id.tvCarName);
        TextView tvRentalPeriod = findViewById(R.id.tvRentalPeriod);
        TextView tvCarLocation = findViewById(R.id.tvCarAddress);
        Button btnClose = findViewById(R.id.btnClose);

        // Set data
        tvContractId.setText(contractId.toString());
        tvCustomerName.setText(customerName);
        tvCarName.setText(carName);
        tvRentalPeriod.setText(startDate + " - " + endDate);
        tvCarLocation.setText(location);
        // Set click listener for close button
        btnClose.setOnClickListener(v -> dismiss());
    }
} 