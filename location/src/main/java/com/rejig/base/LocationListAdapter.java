package com.rejig.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示地理位置的列表item
 *
 * @author rejig
 * date 2021-10-15
 */
public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    private final Context context;
    List<HWPosition> hwPositionList = new ArrayList<>();
    private boolean canUpdate = true;
    private String selectId = "";
    private int selectPosition = 0;
    private Callback callback;

    private final static String EMPTY_ID = "-1";
    public final static String NONE_LOC_ID = "-2";

    private final static int DEFAULT_TYPE = 0;
    private final static int EMPTY_TYPE = 1;
    private final static int NONE_LOC_TYPE = 2;

    public LocationListAdapter(Context context) {
        this.context = context;
    }

    public void updateList(List<HWPosition> dataList) {
        if (!canUpdate) return;
        hwPositionList.clear();
        hwPositionList.addAll(dataList);
        if (dataList.size() != 0) {
            hwPositionList.add(0, new HWPosition(NONE_LOC_ID));
        } else {
            hwPositionList.add(0, new HWPosition(EMPTY_ID));
        }
        notifyDataSetChanged();
    }

    public void addList(List<HWPosition> dataList) {
        hwPositionList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setSelectId(@NonNull String selectId) {
        this.selectId = selectId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == EMPTY_TYPE) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.map_empty_view, viewGroup, false));
        }
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.location_item_view, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == EMPTY_TYPE) return;
        HWPosition hwPosition = hwPositionList.get(position);
        if (getItemViewType(position) == NONE_LOC_TYPE) {
            viewHolder.locationTv.setVisibility(View.GONE);
            viewHolder.nameTv.setText("不显示定位");
        } else {
            viewHolder.locationTv.setText(hwPosition.getAddress());
            viewHolder.nameTv.setText(hwPosition.getName());
            if (position == 0) {
                viewHolder.divider.setVisibility(View.GONE);
            } else {
                viewHolder.divider.setVisibility(View.VISIBLE);
            }
        }
        if (selectId.equals(hwPosition.getId())) {
            viewHolder.locationTv.setTextColor(0xff24c572);
            viewHolder.nameTv.setTextColor(0xff24c572);
            viewHolder.locIv.setImageResource(R.drawable.location_icon_select);
        } else {
            viewHolder.locationTv.setTextColor(0xff999999);
            viewHolder.nameTv.setTextColor(0xff4a4a4a);
            viewHolder.locIv.setImageResource(R.drawable.location_icon);
        }
        viewHolder.itemView.setOnClickListener(v -> {
            int oldPosition = selectPosition;
            selectId = hwPosition.getId();
            selectPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectPosition);
            if (callback != null) {
                callback.onClickPoi(hwPositionList.get(selectPosition));
            }
        });
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    @Override
    public int getItemCount() {
        return hwPositionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (EMPTY_ID.equals(hwPositionList.get(position).getId())) {
            return EMPTY_TYPE;
        } else if (NONE_LOC_ID.equals(hwPositionList.get(position).getId())) {
            return NONE_LOC_TYPE;
        } else {
            return DEFAULT_TYPE;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationTv;
        TextView nameTv;
        ImageView locIv;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTv = itemView.findViewById(R.id.location_tv);
            nameTv = itemView.findViewById(R.id.name_tv);
            divider = itemView.findViewById(R.id.divider_view);
            locIv = itemView.findViewById(R.id.location_iv);
        }
    }

    public interface Callback {
        void onClickPoi(HWPosition position);
    }
}
