package com.example.test.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.collection.ListWrapper;
import com.example.test.util.ContextUtil;
import com.example.test.util.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author rejig
 * date 2020-08-10
 * 自定义弹窗选择功能列表
 */
public class CustomListPopupWindow extends ConstraintLayout {
    private Context mContext;
    private RecyclerView window_rcv;
    private ConstraintLayout window_cl;
    private ListPopupWindowAdapter adapter = new ListPopupWindowAdapter();
    private float x=0,y=0;//记录触摸时的手指位置


    public CustomListPopupWindow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public void setPosition(float x, float y){
        this.x=x;
        this.y=y;
    }



    public void hideView(){
        adapter.refresh(Collections.emptyList());
        window_cl.setVisibility(GONE);
    }


    public void showView(boolean hideByClick, List<TaskItem> taskList){
        if (hideByClick) {
            taskList = new ListWrapper<>(taskList).map(item-> new TaskItem(item.desc, ()->{
                item.run();
                hideView();
            }));
        }
        changeViewPosition(taskList);
        showView(taskList);
        setListener();
    }

    private void showView(List<TaskItem> taskList){
        adapter.refresh(taskList);
        adapter.notifyDataSetChanged();
        window_cl.setVisibility(VISIBLE);
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.popwindow_chat_list, this);
        window_rcv =findViewById(R.id.chat_popwindow_rcv);
        window_cl =findViewById(R.id.chat_popwindow_cl);
        adapter.refresh(Collections.emptyList());
        window_rcv.setLayoutManager(new LinearLayoutManager(mContext));
        window_rcv.setAdapter(adapter);
    }

    private void changeViewPosition(List<TaskItem> taskList) {
        ConstraintSet cs=new ConstraintSet();
        cs.clone(window_cl);
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        x=outMetrics.widthPixels>2*x ? x : x- ScreenUtil.dip2px(mContext,120);
        y=outMetrics.heightPixels>2*y ? y : y- ScreenUtil.dip2px(mContext,46* taskList.size());
        cs.connect(window_rcv.getId(),ConstraintSet.START,ConstraintSet.PARENT_ID,ConstraintSet.START, (int) x);
        cs.connect(window_rcv.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP, (int) y);
        cs.applyTo(window_cl);
    }

    private void setListener() {
        window_cl.setOnClickListener(v -> hideView());
    }

    public interface PopupWindowInterface{
        CustomListPopupWindow getListPopupWindow();
    }

    @Nullable
    public static CustomListPopupWindow getListPopupWindow(Context context) {
        Activity activity = ContextUtil.getActivityFromContext(context);
        if (activity instanceof PopupWindowInterface) {
            return ((PopupWindowInterface)activity).getListPopupWindow();
        }
        return null;
    }

    /**
     * 长按弹窗的按钮
     * @author rejig
     * date 2020-09-10
     */
    public static class ListPopupWindowAdapter extends RecyclerView.Adapter<ListPopupWindowAdapter.ViewHolder> {
        protected List<TaskItem> dataList = new ArrayList<>();
        @NonNull
        @Override
        public ListPopupWindowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            return new ListPopupWindowAdapter.ViewHolder(inflater.inflate(R.layout.chat_popwindow_item, viewGroup, false));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public void refresh(List<TaskItem> newDataList) {
            dataList.clear();
            dataList.addAll(newDataList);
            notifyDataSetChanged();
        }

        public void add(List<TaskItem> moreData) {
            int start = dataList.size();
            dataList.addAll(moreData);
            notifyItemRangeInserted(start, moreData.size());
        }

        @Override
        public void onBindViewHolder(@NonNull ListPopupWindowAdapter.ViewHolder viewHolder, int i) {
            switch (getItemType(i)){
                case 0:
                    viewHolder.button.setBackgroundResource(R.drawable.transparent_button_top);
                    break;
                case 2:
                    viewHolder.button.setBackgroundResource(R.drawable.transparent_button_bottom);
                    viewHolder.line.setVisibility(View.GONE);
                    break;
                case 3:
                    viewHolder.button.setBackgroundResource(R.drawable.transparent_button_bottom);
                    viewHolder.line.setVisibility(View.GONE);
                    break;
                default:
                    viewHolder.button.setBackgroundResource(R.drawable.transparent_button);
                    break;
            }
            TaskItem item = dataList.get(i);
            viewHolder.button.setText(item.desc);
            viewHolder.button.setOnClickListener(v -> item.run());
        }

        public int getItemType(int i){
            if (i==0 && i<getItemCount()-1) {
                return 3;
            } else if (i ==0){
                return 0;
            } else if (i<getItemCount()-1){
                return 1;
            } else return 2;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private Button button;
            private View line;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                button=itemView.findViewById(R.id.popup_window_btn);
                line=itemView.findViewById(R.id.popup_window_line);
            }
        }

    }

}
