package com.example.uiproject.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.uiproject.R;

public class PaymentFragment extends Fragment {

    private String paymentUrl;
    private ImageButton btnClose;


    public PaymentFragment() {
        // Constructor rỗng là bắt buộc với Fragment
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            paymentUrl = args.getString("payment_url");
        }
    }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_payment, container, false);
            btnClose = view.findViewById(R.id.btnClose);

            WebView webView = view.findViewById(R.id.webViewPayment);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());

            if (paymentUrl != null && !paymentUrl.isEmpty()) {
                webView.loadUrl(paymentUrl);
            } else {
                Toast.makeText(getContext(), "Không có URL thanh toán!", Toast.LENGTH_SHORT).show();
            }

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requireActivity().getSupportFragmentManager().popBackStack(); // Quay lại fragment trước đó
                }
            });


            return view;
        }
}