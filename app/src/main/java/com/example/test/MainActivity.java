package com.example.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.ReplacementTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.example.test.util.DeviceInfoUtil;
import com.example.test.widget.CustomListPopupWindow;
import com.example.test.widget.SimpleTextView;
import com.example.test.widget.TaskItem;
import com.wepie.emoji.view.EmojiHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.example.test.util.DeviceInfoUtil.REQUEAST_PERMISSION;

public class MainActivity extends Activity {
    List<TaskItem> menu= new ArrayList<>();
    CustomListPopupWindow actionPopwindow;
    Button button;
    private String TAG="MainActivity";
    SimpleTextView simpleTextView;
    EditText editText;
    CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: " );
        super.onCreate(savedInstanceState);
        List<String> aaa = Collections.synchronizedList(list);
        setContentView(R.layout.activity_main);
        final GiftPlayView giftPlayView=new GiftPlayView(this);
        addContentView(giftPlayView,new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        button=findViewById(R.id.button);
        actionPopwindow = findViewById(R.id.popup_window);
        TaskItem taskItem = new TaskItem("取消", () -> {
            Toast.makeText(this,"点击了取消", Toast.LENGTH_SHORT).show();
        });
        menu.add(taskItem);

        simpleTextView = findViewById(R.id.test_stv);
        SpannableStringBuilder span = new SpannableStringBuilder();
        EmojiHelper.parseEmoji2Ssb(this,span,"\uD83D\uDE0C\uD83D\uDE0F\uD83D\uDE11\uD83D\uDE0C\uD83D\uDE0F\uD83D\uDE11\uD83D\uDE0C\uD83D\uDE0F\uD83D\uDE11\uD83D\uDE0C\uD83D\uDE0F\uD83D\uDE11\uD83D\uDE0C",100,16);
        int start = span.length();
//        span.append("哈哈哈哈哈");
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(getApplicationContext(),"hahahahahah",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //删除下划线，设置字体颜色为蓝色
                ds.setColor(0xffcfb08c);
                ds.setUnderlineText(false);
            }
        }, start, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        simpleTextView.setText(span);




        button.setOnClickListener(v -> {
            giftPlayView.payGiftAnim();
            simpleTextView.setPadding(8,8,8,8);
            simpleTextView.setGravity(SimpleTextView.ALIGN_CENTER);
            span.append(DeviceInfoUtil.getDeviceInfo(getContext()).toString());
            simpleTextView.setText(span);
            Log.e(TAG, "onCreate: "+span );
            actionPopwindow.showView(true,menu);
        });

        editText = findViewById(R.id.editTextTextPersonName);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        editText.setFilters(new InputFilter[]{new CapsLetterFilter(),new InputFilter.LengthFilter(4)});
        //限制只输入大写，自动小写转大写
        editText.setTransformationMethod(new AllCapTransformationMethod());


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
    public boolean dispatchTouchEvent(MotionEvent ev){
        super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            actionPopwindow.setPosition(ev.getRawX(),ev.getRawY());
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
        Log.e(TAG, "onRestart: " );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: " );
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {

        return super.dispatchGenericMotionEvent(ev);
    }

    public void requestPermission(final @NonNull String[] permissions){
        ActivityCompat.requestPermissions(this, permissions,REQUEAST_PERMISSION );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEAST_PERMISSION){
            for (int i=0;i<permissions.length;i++){
                String permission = permissions[i];
                if (permission.equals(Manifest.permission.READ_PHONE_STATE)){
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getContext(), DeviceInfoUtil.getIMEI(getContext()),Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getContext(),"权限申请失败",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }

    public Context getContext(){
        return this;
    }


    public static class AllCapTransformationMethod extends ReplacementTransformationMethod {

        @Override
        protected char[] getOriginal() {
            char[] aa = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z' };
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] cc = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z' };
            return cc;
        }

    }
}