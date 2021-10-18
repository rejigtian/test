package com.rejig.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChooseLocationDialog extends ConstraintLayout {
    private final EditText searchTv;
    private final RecyclerView locationRcv;
    private final ImageView topIv;
    private final ConstraintLayout rootLay;
    private LocationListAdapter adapter;
    private String keyword = "";
    private HWPosition myPoi = new HWPosition();
    private List<HWPosition> nearbyPoiList = new ArrayList<>();
    private Callback callback;
    private boolean hasInit = false;
    private float startY;
    private int totalPage;

    public ChooseLocationDialog(@NonNull Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.location_search_dialog, this);
        searchTv = findViewById(R.id.search_tv);
        locationRcv = findViewById(R.id.location_rcv);
        topIv = findViewById(R.id.top_iv);
        rootLay = findViewById(R.id.root_lay);
        initView();
        initData();
        setListener();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PositionHelper.getInstance().initSearch();
    }

    private void initView() {
        adapter = new LocationListAdapter(getContext());
        locationRcv.setLayoutManager(new LinearLayoutManager(getContext()));
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

    @SuppressLint("ClickableViewAccessibility")
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
                if (TextUtils.isEmpty(keyword)) {
                    showRecommendList();
                } else {
                    adapter.setCanUpdate(true);
                    PositionHelper.getInstance().searchPosition(myPoi.getLatitude(), myPoi.getLongitude(), s.toString(), 0);
                }
            }
        });
        topIv.setOnTouchListener((v, event) -> {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float y = event.getRawY() - startY;
                    if (y<0) y =0;
                    requestMoveLay(y);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    float lastY = event.getRawY() - startY;
                    if (lastY<0) lastY =0;
                    requestMoveLay(lastY);
                    judgePosition(lastY);
                    break;
            }
            return true;
        });
    }

    private void requestMoveLay(float y) {
        rootLay.setTranslationY(y);
    }

    private void judgePosition(float lastY) {
        if (lastY < rootLay.getMeasuredHeight()/3f){
            moveLayWithAnim(-lastY,false);
        } else {
            moveLayWithAnim(rootLay.getMeasuredHeight()-lastY, true);
        }
    }

    private void moveLayWithAnim(float y, boolean needClose) {
        rootLay.animate().setDuration(300).translationYBy(y).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (needClose && callback!=null){
                    callback.onCancel();
                }
            }
        });
    }

    private void showRecommendList() {
        adapter.updateList(nearbyPoiList);
        adapter.setCanUpdate(false);
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        PositionHelper.getInstance().unInitSearch();
    }

    public interface Callback {
        void onCancel();
    }
}
