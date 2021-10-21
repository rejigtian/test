package com.rejig.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rejig.base.widget.DragDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择地理位置的弹窗
 * @author rejig
 * date 2021-10-20
 */
public class ChooseLocationDialog extends DragDialog {
    private final EditText searchTv;
    private final RecyclerView locationRcv;
    private final ImageView closeIv;
    private LocationListAdapter adapter;
    private final SmartRefreshLayout smartRefreshLayout;

    private List<HWPosition> nearbyPoiList = new ArrayList<>();
    private HWPosition myPoi = new HWPosition();
    private String keyword = "";
    private Callback callback;
    private int totalPage;
    private int curPage;
    private HWPosition selectPosition;

    public ChooseLocationDialog(@NonNull Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.location_search_dialog, this);
        searchTv = findViewById(R.id.search_tv);
        locationRcv = findViewById(R.id.location_rcv);
        smartRefreshLayout = findViewById(R.id.refresh_lay);
        closeIv = findViewById(R.id.close_iv);
        ImageView topIv = findViewById(R.id.top_iv);
        ConstraintLayout rootLay = findViewById(R.id.root_lay);
        setControlView(topIv, rootLay);
        initView();
        initData();
        setListener();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PoiSearchHelper.getInstance().initSearch();
    }

    private void initView() {
        adapter = new LocationListAdapter(getContext());
        locationRcv.setLayoutManager(new LinearLayoutManager(getContext()));
        locationRcv.setAdapter(adapter);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setReboundDuration(150);//回弹动画时长
    }

    private void initData() {
        LocationPoiHelper.getInstance(getContext()).startLocate(false, new LocationPoiHelper.Callback() {
            @Override
            public void onLocationSuc(HWPosition hwPosition, List<HWPosition> positions) {
                myPoi = hwPosition;
                nearbyPoiList.addAll(positions);
                nearbyPoiList.add(0, myPoi);
                showRecommendList();
            }

            @Override
            public void onLocationFail(String msg) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        PoiSearchHelper.getInstance().setCallback((hwPositionList, totalPage) -> {
            if (curPage > 0){
                adapter.addList(hwPositionList);
                smartRefreshLayout.finishLoadmore();
            } else {
                adapter.updateList(hwPositionList);
            }
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
                    closeIv.setVisibility(GONE);
                } else {
                    closeIv.setVisibility(VISIBLE);
                    adapter.setCanUpdate(true);
                    curPage = 0;
                    PoiSearchHelper.getInstance().searchPosition(myPoi.getLatitude(), myPoi.getLongitude(), keyword, curPage);
                }
            }
        });
        smartRefreshLayout.setOnLoadmoreListener(refreshLayout -> {
            if (totalPage >= curPage) {
                adapter.setCanUpdate(true);
                curPage++;
                PoiSearchHelper.getInstance().searchPosition(myPoi.getLatitude(), myPoi.getLongitude(), keyword, curPage);
            } else {
                Toast.makeText(getContext(), "没有更多了～", Toast.LENGTH_LONG).show();
                smartRefreshLayout.finishLoadmore();
            }
        });
        adapter.setCallback(position -> {
            if (callback != null){
                callback.onSelectPoi(position);
            }
        });
        closeIv.setOnClickListener(v -> searchTv.setText(""));
    }

    @Override
    public void onClose() {
        if (callback!=null){
            callback.onCancel();
        }
    }

    private void showRecommendList() {
        adapter.updateList(nearbyPoiList);
        adapter.setCanUpdate(false);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setSelectPosition(HWPosition selectPosition) {
        this.selectPosition = selectPosition;
        if (selectPosition == null){
            adapter.setSelectId(LocationListAdapter.NONE_LOC_ID);
        } else if (HWPosition.MY_ID.equals(selectPosition.getId())){
            adapter.setSelectId(selectPosition.getId());
        } else {
            nearbyPoiList.add(1, selectPosition);
            adapter.setSelectId(selectPosition.getId());
        }
        if (searchTv.getText().length() <= 0) {
            showRecommendList();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        PoiSearchHelper.getInstance().unInitSearch();
    }

    public interface Callback {
        void onCancel();
        void onSelectPoi(HWPosition position);
    }
}
