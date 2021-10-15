package com.example.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.example.test.util.DeviceInfoUtil;
import com.example.test.view.GiftPlayView;
import com.example.test.widget.CustomListPopupWindow;
import com.example.test.widget.SimpleTextView;
import com.example.test.widget.TaskItem;
import com.rejig.location.LocationActivity;
import com.wepie.libpermission.PermissionCallback;
import com.wepie.libpermission.WPPermission;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.test.util.DeviceInfoUtil.REQUEAST_PERMISSION;

public class MainActivity extends Activity {
    List<TaskItem> menu = new ArrayList<>();
    CustomListPopupWindow actionPopwindow;
    Button button;
    Button locationBtn;
    String TAG = "MainActivity";
    SimpleTextView simpleTextView;
    GiftPlayView giftPlayView;
    TextView versionTv;
    SpannableStringBuilder spanTest = new SpannableStringBuilder();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        giftPlayView = new GiftPlayView(this);
        addContentView(giftPlayView, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        initView();
        setListener();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        versionTv = findViewById(R.id.version_tv);
        button = findViewById(R.id.button);
        simpleTextView = findViewById(R.id.test_stv);
        actionPopwindow = findViewById(R.id.popup_window);
        simpleTextView = findViewById(R.id.test_stv);
        locationBtn = findViewById(R.id.location_test_btn);

        versionTv.setText(BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE);
        TaskItem taskItem = new TaskItem("取消", () -> {
            Toast.makeText(this, "点击了取消", Toast.LENGTH_SHORT).show();
        });
        menu.add(taskItem);

        int start = spanTest.length();
        spanTest.append("\n哈哈哈哈哈\n");
        spanTest.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(getApplicationContext(), "哈哈哈哈哈", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),TinkerActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NotNull TextPaint ds) {
                super.updateDrawState(ds);
                //删除下划线，设置字体颜色为蓝色
                ds.setColor(0xffcfb08c);
                ds.setUnderlineText(false);
            }
        }, start, spanTest.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        simpleTextView.setText(spanTest);
        locationBtn.setOnClickListener(v -> {
            WPPermission.with((Activity) getContext())
                    .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .request(new PermissionCallback() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll, boolean alreadyHas) {
                            Intent intent = new Intent(getContext(), LocationActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            Toast.makeText(getContext(), "未开启权限", Toast.LENGTH_LONG).show();
                        }
                    });
        });

    }

    private void setListener() {
        button.setOnClickListener(v -> {
            giftPlayView.playGiftAnim();
            simpleTextView.setPadding(8, 8, 8, 8);
            simpleTextView.setGravity(SimpleTextView.ALIGN_CENTER);
            DeviceInfoUtil.getDeviceInfo(getContext(), advertisingIdInfo -> {
                spanTest.append(DeviceInfoUtil.deviceInfo.toString());
                simpleTextView.setText(spanTest);
                Log.e(TAG, "onCreate: " + spanTest);
            });
            actionPopwindow.showView(true, menu);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            actionPopwindow.setPosition(ev.getRawX(), ev.getRawY());
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {

        return super.dispatchGenericMotionEvent(ev);
    }

    public void requestPermission(final @NonNull String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, REQUEAST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEAST_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (permission.equals(Manifest.permission.READ_PHONE_STATE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), DeviceInfoUtil.getIMEI(getContext()), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "权限申请失败", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }

    public Context getContext() {
        return this;
    }

}