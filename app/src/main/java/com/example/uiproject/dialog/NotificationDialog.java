package com.example.uiproject.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.uiproject.R;

public class NotificationDialog extends Dialog {
    private String title;
    private String message;
    private String time;

    public NotificationDialog(@NonNull Context context, String title, String message, String time) {
        super(context);
        this.title = title;
        this.message = message;
        this.time = time;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_notification_detail);

        // Setup window
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 
                           WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            
            // Add animation
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
        }

        // Initialize views
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvMessage = findViewById(R.id.tvMessage);
        TextView tvTime = findViewById(R.id.tvTime);
        Button btnClose = findViewById(R.id.btnClose);

        // Set data
        tvTitle.setText(title);
        tvMessage.setText(message);
        tvTime.setText(time);

        // Set click listener
        btnClose.setOnClickListener(v -> dismiss());
    }
} 