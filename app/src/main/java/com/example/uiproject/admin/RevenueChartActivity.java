package com.example.uiproject.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiproject.R;
import com.example.uiproject.admin.api.ApiServiceAdmin;
import com.example.uiproject.admin.api.RetrofitClientAdmin;
import com.example.uiproject.admin.model.Payment;
import com.example.uiproject.admin.model.PaymentDTO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RevenueChartActivity extends AppCompatActivity {

    private BarChart barChart;
    private Spinner yearSpinner;
    private List<Payment> allPayments = new ArrayList<>();
    private int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_chart);

        barChart = findViewById(R.id.barChart);
        yearSpinner = findViewById(R.id.yearSpinner);

        loadDummyData();
    }

    private void setupYearSpinner() {
        List<Integer> years = new ArrayList<>();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            years.add(y);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int selectedYear = years.get(pos);
                Map<Integer, Double> monthlyRevenue = getRevenuePerMonth(allPayments, selectedYear);
                showBarChart(monthlyRevenue, selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showBarChart(Map<Integer, Double> revenueMap, int year) {
        List<BarEntry> entries = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            entries.add(new BarEntry(month, revenueMap.get(month).floatValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu năm " + year);
        dataSet.setColor(Color.BLUE);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "T" + (int) value;
            }
        });

        barChart.invalidate(); // refresh
    }
    public String formatDateToSimpleString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }
    private void loadDummyData() {
        Retrofit retrofit = RetrofitClientAdmin.getInstance();
        ApiServiceAdmin apiServiceAdmin = retrofit.create(ApiServiceAdmin.class);
        apiServiceAdmin.getAllPayment().enqueue(new Callback<List<PaymentDTO>>() {
            @Override
            public void onResponse(Call<List<PaymentDTO>> call, Response<List<PaymentDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for(PaymentDTO paymentDTO : response.body()){
                        allPayments.add(new Payment(paymentDTO.getId(),paymentDTO.getPrice(),formatDateToSimpleString(paymentDTO.getDate()) ));
                        setupYearSpinner();
                    }
                } else {
                    Toast.makeText(RevenueChartActivity.this, "Không lấy được dữ liệu doanh thu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PaymentDTO>> call, Throwable t) {
                Toast.makeText(RevenueChartActivity.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Map<Integer, Double> getRevenuePerMonth(List<Payment> payments, int year) {
        Map<Integer, Double> revenueMap = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            revenueMap.put(month, 0.0);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Payment payment : payments) {
            try {
                Date date = sdf.parse(payment.payDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH) + 1; // tháng bắt đầu từ 0

                if (y == year) {
                    double currentRevenue = revenueMap.get(m);
                    revenueMap.put(m, currentRevenue + payment.price);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return revenueMap;
    }

}

