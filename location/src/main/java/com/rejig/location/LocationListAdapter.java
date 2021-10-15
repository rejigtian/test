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
    private Context context;
    List<HWPosition> hwPositionList = new ArrayList<>();
    private boolean cleared = false;
    private final static int DEFAULT_TYPE = 0;
    private final static int EMPTY_TYPE = 1;

    public LocationListAdapter(Context context) {
        this.context = context;
    }

    public void updateList(List<HWPosition> dataList){
        if (cleared) return;
        hwPositionList.clear();
        hwPositionList.addAll(dataList);
        notifyDataSetChanged();
    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
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
