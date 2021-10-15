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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.location_item_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        HWPosition hwPosition = hwPositionList.get(i);
        viewHolder.locationTv.setText(hwPosition.toString());
    }

    @Override
    public int getItemCount() {
        return hwPositionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView locationTv;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           locationTv = itemView.findViewById(R.id.location_tv);
       }
   }
}
