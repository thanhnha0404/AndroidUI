package com.example.uiproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.uiproject.R;
import com.example.uiproject.dialog.SettingsDialog;
import com.example.uiproject.ProfileEditActivity;
import com.example.uiproject.entity.CustomerDTO;

public class ProfileFragment extends Fragment {
    private TextView tvProfileName, tvProfileEmail, tvNotificationStatus;
    private LinearLayout itemMyProfile, itemSettings, itemNotification, itemLogout;
    private CustomerDTO customer;
    private ImageView iv_profile_pic;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null){
            customer = (CustomerDTO)getArguments().getSerializable("customer");
        }
        
        // Initialize views
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        tvNotificationStatus = view.findViewById(R.id.tv_notification_status);
        iv_profile_pic = view.findViewById(R.id.iv_profile_pic);
        
        itemMyProfile = view.findViewById(R.id.item_my_profile);
        itemSettings = view.findViewById(R.id.item_settings);
        itemNotification = view.findViewById(R.id.item_notification);
        itemLogout = view.findViewById(R.id.item_logout);


        // Set click listeners
        itemMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
            startActivity(intent);
        });

        itemSettings.setOnClickListener(v -> {
            SettingsDialog dialog = new SettingsDialog();
            dialog.show(getParentFragmentManager(), "settings_dialog");
        });

        itemNotification.setOnClickListener(v -> {
            // Toggle notification status
            boolean isEnabled = tvNotificationStatus.getText().toString().equals("Allow");
            tvNotificationStatus.setText(isEnabled ? "Deny" : "Allow");
        });

        itemLogout.setOnClickListener(v -> {
            // Handle logout
            // TODO: Implement logout logic
        });

        // Load user data
        loadUserData();
    }

    private void loadUserData() {
        if (customer != null){
            if (customer.getAvatar() != null && !customer.getAvatar().isEmpty()){
                Glide.with(requireContext())
                        .load(customer.getAvatar())
                        .placeholder(R.drawable.ic_person) // ảnh tạm khi đang load
                        .error(R.drawable.ic_person)         // ảnh nếu load lỗi
                        .into(iv_profile_pic); // ivProfile là ImageView bạn muốn hiển thị
            }
            tvProfileName.setText(customer.getName());
            tvProfileEmail.setText(customer.getEmail());
        }
    }
} 