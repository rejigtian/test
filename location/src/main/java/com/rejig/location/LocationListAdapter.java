package com.rejig.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示地理位置的列表item
 * @author rejig
 * date 2021-10-15
 */
public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    private final Context context;
    List<HWPosition> hwPositionList = new ArrayList<>();
    private boolean canUpdate = true;
    private int selectPosition = 0;

    private final static int EMPTY_ID= -1;
    private final static int NONE_LOC_ID = -2;

    private final static int DEFAULT_TYPE = 0;
    private final static int EMPTY_TYPE = 1;
    private final static int NONE_LOC_TYPE = 2;


    public LocationListAdapter(Context context) {
        this.context = context;
    }

    public void updateList(List<HWPosition> dataList){
        if (!canUpdate) return;
        hwPositionList.clear();
        hwPositionList.addAll(dataList);
        if (dataList.size() != 0){
            hwPositionList.add(0,new HWPosition(NONE_LOC_ID));
        } else {
            hwPositionList.add(0,new HWPosition(EMPTY_ID));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == EMPTY_TYPE){
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.map_empty_view, viewGroup, false));
        }
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.location_item_view, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == EMPTY_TYPE) return;
        if (getItemViewType(position) == NONE_LOC_TYPE){
            viewHolder.locationTv.setVisibility(View.GONE);;
            viewHolder.nameTv.setText("不显示定位");
        } else {
            HWPosition hwPosition = hwPositionList.get(position);
            viewHolder.locationTv.setText(hwPosition.getAddress());
            viewHolder.nameTv.setText(hwPosition.getName());
            if (position == 0) {
                viewHolder.divider.setVisibility(View.GONE);
            } else {
                viewHolder.divider.setVisibility(View.VISIBLE);
            }
        }
        if (position == selectPosition){
            viewHolder.locationTv.setTextColor(0xffdfc08c);
            viewHolder.nameTv.setTextColor(0xffdfc08c);
            viewHolder.locIv.setImageResource(R.drawable.location_icon_select);
        } else {
            viewHolder.locationTv.setTextColor(0xff999999);
            viewHolder.nameTv.setTextColor(0xff333333);
            viewHolder.locIv.setImageResource(R.drawable.location_icon);
        }
        viewHolder.itemView.setOnClickListener(v -> {
            int oldPosition = selectPosition;
            selectPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectPosition);
        });
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    /**
     * 获取选择的地理位置
     * @return 不显示定位返回null
     */
    @Nullable
    public HWPosition getSelectPoi(){
        if (selectPosition > 0 && selectPosition < hwPositionList.size()){
            return hwPositionList.get(selectPosition);
        }
        else return null;
    }

    @Override
    public int getItemCount() {
        return hwPositionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (hwPositionList.get(position).getId() == EMPTY_ID) {
            return EMPTY_TYPE;
        } else if (hwPositionList.get(position).getId() == NONE_LOC_ID){
            return NONE_LOC_TYPE;
        }else {
            return DEFAULT_TYPE;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
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
}
