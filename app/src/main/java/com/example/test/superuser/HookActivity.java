package com.example.test.superuser;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.rejig.base.widget.ScreenUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HookActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static final String HOOK_IMAGE = "HOOK_IMAGE";
    private static final String HOOK_ANDROID_ID = "HOOK_ANDROID_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook);
        recyclerView = findViewById(R.id.recycle_view);
        StringAdapter stringAdapter = new StringAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(stringAdapter);

        List<String> dataList = new ArrayList<>();
        dataList.add(HOOK_IMAGE);
        dataList.add(HOOK_ANDROID_ID);
        stringAdapter.refresh(dataList);
        stringAdapter.setItemClick(param -> {
            switch (param) {
                case HOOK_IMAGE:
                    break;
                case HOOK_ANDROID_ID:
                    break;
                default:
                    break;
            }
        });
    }

    private static class StringAdapter extends RecyclerView.Adapter<StringHolder> {

        List<String> dataList = new ArrayList<>();
        private Context context;
        private ItemClick itemClick;

        public StringAdapter(Context context) {
            this.context = context;
        }

        public void refresh(List<String> text) {
            dataList.clear();
            dataList.addAll(text);
            notifyDataSetChanged();
        }

        public void setItemClick(ItemClick itemClick) {
            this.itemClick = itemClick;
        }

        @NonNull
        @NotNull
        @Override
        public StringHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(context);
            textView.setTextColor(0xff333333);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(60));
            textView.setLayoutParams(layoutParams);
            return new StringHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull HookActivity.StringHolder holder, int position) {
            holder.textView.setText(dataList.get(position));
            holder.textView.setOnClickListener(v -> itemClick.click(dataList.get(position)));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    private static class StringHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public StringHolder(@NonNull @NotNull TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }

    interface ItemClick {
        void click(String param);
    }
}
