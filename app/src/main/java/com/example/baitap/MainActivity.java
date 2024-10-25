package com.example.baitap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public MainActivity extends AppCompatActivity {
    private EditText editTextA, editTextB;
    private LinearLayout historyLayout;
    private SharedPreferences sharedPreferences;
    private static final String HISTORY_PREF = "history_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editTextA = findViewById(R.id.editTextText);
        editTextB = findViewById(R.id.editTextText2);
        historyLayout = findViewById(R.id.historyLayout);

        sharedPreferences = getSharedPreferences(HISTORY_PREF, Context.MODE_PRIVATE);

        // Lấy lịch sử từ SharedPreferences và hiển thị lên LinearLayout
        loadHistory();

        Button btnTong = findViewById(R.id.button);
        Button btnHieu = findViewById(R.id.button2);
        Button btnTich = findViewById(R.id.button3);
        Button btnThuong = findViewById(R.id.button4);
        Button btnXoaLichSu = findViewById(R.id.button5);

        btnTong.setOnClickListener(v -> calculateAndSave("Tổng", (a, b) -> a + b));
        btnHieu.setOnClickListener(v -> calculateAndSave("Hiệu", (a, b) -> a - b));
        btnTich.setOnClickListener(v -> calculateAndSave("Tích", (a, b) -> a * b));
        btnThuong.setOnClickListener(v -> calculateAndSave("Thương", (a, b) -> b != 0 ? a / b : 0));

        btnXoaLichSu.setOnClickListener(v -> clearHistory());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void calculateAndSave(String operation, Calculator calculator) {
        String inputA = editTextA.getText().toString();
        String inputB = editTextB.getText().toString();

        if (!inputA.isEmpty() && !inputB.isEmpty()) {
            try {
                double a = Double.parseDouble(inputA);
                double b = Double.parseDouble(inputB);
                double result = calculator.calculate(a, b);

                // Lưu vào SharedPreferences
                String historyEntry = operation + " của " + a + " và " + b + " = " + result;
                saveHistory(historyEntry);

                // Hiển thị lịch sử
                addHistoryEntryToLayout(historyEntry);
            } catch (NumberFormatException e) {
                // Handle invalid input if necessary
            }
        }
    }

    private void saveHistory(String historyEntry) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String previousHistory = sharedPreferences.getString(HISTORY_PREF, "");
        String newHistory = previousHistory + historyEntry + "\n";
        editor.putString(HISTORY_PREF, newHistory);
        editor.apply();
    }

    private void loadHistory() {
        String savedHistory = sharedPreferences.getString(HISTORY_PREF, "");
        if (!savedHistory.isEmpty()) {
            String[] historyEntries = savedHistory.split("\n");
            for (String entry : historyEntries) {
                addHistoryEntryToLayout(entry);
            }
        }
    }

    private void addHistoryEntryToLayout(String entry) {
        TextView historyTextView = new TextView(this);
        historyTextView.setText(entry);
        historyLayout.addView(historyTextView);
    }

    private void clearHistory() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        historyLayout.removeAllViews();
    }

    @FunctionalInterface
    interface Calculator {
        double calculate(double a, double b);
    }
}
