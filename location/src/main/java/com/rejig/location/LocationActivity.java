package com.rejig.location;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationActivity extends Activity {
    private EditText searchTv;
    private RecyclerView locationRcv;
    private LocationListAdapter adapter;
    private String keyword = "";
    private String TAG= "LocationActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        searchTv = findViewById(R.id.search_tv);
        locationRcv = findViewById(R.id.location_rcv);
        initView();
        setListener();
        PositionHelper.getInstance().initSearch();
        Log.e(TAG, "onCreate: " );
    }

    private void initView() {
        adapter = new LocationListAdapter(this);
        locationRcv.setLayoutManager(new LinearLayoutManager(this));
        locationRcv.setAdapter(adapter);
    }

    private void setListener() {
        PositionHelper.getInstance().setCallback(hwPositionList -> adapter.updateList(hwPositionList));
        searchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
                if (TextUtils.isEmpty(keyword)){
                    clearData();
                } else {
                    adapter.setCleared(false);
                    PositionHelper.getInstance().searchPosition("武汉", s.toString(), 0);
                }
            }
        });
    }

    private void clearData() {
        adapter.updateList(new ArrayList<>());
        adapter.setCleared(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PositionHelper.getInstance().unInitSearch();
    }
}
