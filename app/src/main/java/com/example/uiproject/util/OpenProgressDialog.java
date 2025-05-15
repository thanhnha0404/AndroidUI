package com.example.uiproject.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.uiproject.R;

public class OpenProgressDialog {
    private static AlertDialog progressDialog;
    public static void showProgressDialog(Context context) {
        // Tạo ProgressDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false); // Không cho phép đóng Dialog
        builder.setView(R.layout.progress_dialog); // Chỉ định layout của ProgressDialog (nơi chứa ProgressBar)
        progressDialog = builder.create();
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss(); // Ẩn ProgressDialog
        }
    }
}
