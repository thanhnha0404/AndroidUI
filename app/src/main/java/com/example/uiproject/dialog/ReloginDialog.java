package com.example.uiproject.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.example.uiproject.R;

public class ReloginDialog extends Dialog {
    private ImageView statusIcon;
    private TextView messageText;
    private MaterialButton btnRelogin;
    private OnReloginClickListener reloginClickListener;

    // Interface để handle sự kiện click nút đăng nhập lại
    public interface OnReloginClickListener {
        void onReloginClick();
    }

    public ReloginDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_relogin);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false); // Không cho phép cancel dialog
        setCanceledOnTouchOutside(false);

        initViews();
    }

    private void initViews() {
        statusIcon = findViewById(R.id.statusIcon);
        messageText = findViewById(R.id.messageText);
        btnRelogin = findViewById(R.id.btnRelogin);

        btnRelogin.setOnClickListener(v -> {
            if (reloginClickListener != null) {
                reloginClickListener.onReloginClick();
            }
            dismiss();
        });
    }

    public void setOnReloginClickListener(OnReloginClickListener listener) {
        this.reloginClickListener = listener;
    }

    public void showReloginMessage() {
        statusIcon.setImageResource(R.drawable.ic_warning); // Giả sử bạn có icon warning
        messageText.setText("Vui lòng đăng nhập lại để tiếp tục");
        btnRelogin.setText("Đăng nhập lại");
        
        show();

        // Set lại kích thước dialog
        if (getWindow() != null) {
            getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }
} 