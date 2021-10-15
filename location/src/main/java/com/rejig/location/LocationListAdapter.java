package com.rejig.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    private final Context context;
    List<HWPosition> hwPositionList = new ArrayList<>();
    private boolean canUpdate = true;
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
        notifyDataSetChanged();
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
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
        HWPosition hwPosition = hwPositionList.get(position);
        viewHolder.locationTv.setText(hwPosition.getAddress());
        viewHolder.nameTv.setText(hwPosition.getName());
        if (position == 0){
            viewHolder.divider.setVisibility(View.GONE);
        } else {
            viewHolder.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (hwPositionList.size() == 0){
            return 1;
        }
        return hwPositionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (hwPositionList.size() == 0 && position == 0) {
            return EMPTY_TYPE;
        } else {
            return DEFAULT_TYPE;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView locationTv;
        TextView nameTv;
        View divider;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           locationTv = itemView.findViewById(R.id.location_tv);
           nameTv = itemView.findViewById(R.id.name_tv);
           divider = itemView.findViewById(R.id.divider_view);
       }
   }
}
