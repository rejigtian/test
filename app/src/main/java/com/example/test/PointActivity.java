package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.util.StatusBarUtil;

public class PointActivity extends AppCompatActivity {
    private EditText pointEt;
    private Button showBtn;
    private Button editBtn;
    private TextView pointTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.initStatusBar(this);
        setContentView(R.layout.activity_point);
        pointEt = findViewById(R.id.point_et);
        showBtn = findViewById(R.id.show_btn);
        editBtn = findViewById(R.id.edit_btn);
        pointTv = findViewById(R.id.point_tv);

        showBtn.setOnClickListener(v -> {
            pointTv.setText(pointEt.getText().toString());
            pointTv.setVisibility(View.VISIBLE);
            showBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
            pointEt.setVisibility(View.GONE);
        });

        editBtn.setOnClickListener(v -> {
            pointEt.setText("");
            pointTv.setVisibility(View.GONE);
            showBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);
            pointEt.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
