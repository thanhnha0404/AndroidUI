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

public class SearchResultDialog extends Dialog {
    private ImageView statusIcon;
    private TextView messageText;
    private TextView resultCount;
    private MaterialButton btnOk;

    public SearchResultDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_search_result);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        initViews();
    }

    private void initViews() {
        statusIcon = findViewById(R.id.statusIcon);
        messageText = findViewById(R.id.messageText);
        resultCount = findViewById(R.id.resultCount);
        btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> dismiss());
    }

    public void showResult(String message, int count) {
        messageText.setText(message);
        if (count > 0) {
            statusIcon.setImageResource(R.drawable.ic_search_result);
            resultCount.setText("Tìm thấy " + count + " xe");
        } else {
            statusIcon.setImageResource(R.drawable.ic_search_result);
            resultCount.setText("Không tìm thấy xe nào");
        }
        show();

        // set lai chieu rong sau khi show
        if (getWindow() != null){
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
} 