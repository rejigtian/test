package com.rejig.location;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 定位列表页面支持搜索
 * @author rejig
 * date 2021-10-15
 */
public class LocationActivity extends Activity {
    private EditText searchTv;
    private RecyclerView locationRcv;
    private LocationListAdapter adapter;
    private String keyword = "";
    private HWPosition myPoi = new HWPosition();
    private List<HWPosition> nearbyPoiList = new ArrayList<>();
    private int totalPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        searchTv = findViewById(R.id.search_tv);
        locationRcv = findViewById(R.id.location_rcv);
        initView();
        initData();
        setListener();
        PositionHelper.getInstance().initSearch();
        String TAG = "LocationActivity";
        Log.e(TAG, "onCreate: " );
    }

    private void initView() {
        adapter = new LocationListAdapter(this);
        locationRcv.setLayoutManager(new LinearLayoutManager(this));
        locationRcv.setAdapter(adapter);
    }

    private void initData() {
        LocationHelper.getInstance(getContext()).startLocate(false, new LocationHelper.Callback() {
            @Override
            public void onLocationSuc(HWPosition hwPosition, List<HWPosition> positions) {
                myPoi = hwPosition;
                nearbyPoiList = positions;
                nearbyPoiList.add(0, myPoi);
                if (searchTv.getText().length() <= 0) {
                    showRecommendList();
                }
            }

            @Override
            public void onLocationFail(String msg) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setListener() {
        PositionHelper.getInstance().setCallback((hwPositionList, totalPage) -> {
            adapter.updateList(hwPositionList);
            this.totalPage = totalPage;
        });
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
                    showRecommendList();
                } else {
                    adapter.setCanUpdate(true);
                    PositionHelper.getInstance().searchPosition(myPoi.getLatitude(), myPoi.getLongitude(), s.toString(), 0);
                }
            }
        });
    }

    private void showRecommendList() {
        adapter.updateList(nearbyPoiList);
        adapter.setCanUpdate(false);
    }

    private Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PositionHelper.getInstance().unInitSearch();
    }
}
