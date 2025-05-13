package com.example.uiproject.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.uiproject.R;

public class SettingsDialog extends DialogFragment {
    private TextView tvThemeValue, tvLanguageValue;
    private ImageButton btnClose;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvThemeValue = view.findViewById(R.id.tv_theme_value);
        tvLanguageValue = view.findViewById(R.id.tv_language_value);
        btnClose = view.findViewById(R.id.btn_close);

        // Set click listeners
        btnClose.setOnClickListener(v -> dismiss());

        tvThemeValue.setOnClickListener(v -> {
            // Toggle between Light and Dark theme
            String currentTheme = tvThemeValue.getText().toString();
            tvThemeValue.setText(currentTheme.equals("Light") ? "Dark" : "Light");
            // TODO: Implement theme change logic
        });

        tvLanguageValue.setOnClickListener(v -> {
            // Toggle between Eng and other languages
            String currentLang = tvLanguageValue.getText().toString();
            tvLanguageValue.setText(currentLang.equals("Eng") ? "Other" : "Eng");
            // TODO: Implement language change logic
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
} 