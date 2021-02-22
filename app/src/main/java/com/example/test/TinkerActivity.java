package com.example.test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.test.util.TextUtil;

public class TinkerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinker_v2);
        findViewById(R.id.back_iv).setOnClickListener(v -> {
            finish();
        });
        findViewById(R.id.imageview1).setOnClickListener(v -> {
            finish();
            Toast.makeText(this, "S:" + TextUtil.getString(""), Toast.LENGTH_LONG).show();
        });
    }
}