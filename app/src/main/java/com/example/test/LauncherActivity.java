package com.example.test;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleRegistry;

import com.example.test.animview.SimpleAnimeListener;
import com.example.test.lifecycle.LifecycleHandler;
import com.example.test.lifecycle.ReportFragment;
import com.tencent.qgame.animplayer.AnimView;
import com.tencent.qgame.animplayer.util.ALog;
import com.tencent.qgame.animplayer.util.IALog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class LauncherActivity extends AppCompatActivity {
    private LauncherVideoView launcherVideoView;
    private Button button;
    private int position=0;
    private String TAG = "LauncherActivity";
    private String ANIME = "AnimeView";
    private long exitTime=0;
    public static final int PLAYTIME=20000;
    private CountDownTimer countDownTimer;
    private Handler handler = new Handler();
    LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launcher);
        ReportFragment.injectIfNeededIn(this);
        if (!isTaskRoot()) {
            Intent mainIntent = getIntent();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                Log.i("999", "----->StartActivity not root mainIntent return");
                finish();
                return;
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(LauncherActivity.this,Empty.class));
            }
        },60*1000);
        launcherVideoView = findViewById(R.id.launcher_lvv);
        button = findViewById(R.id.btn_close);
        ALog.INSTANCE.setLog(new IALog() {
            @Override
            public void i(@NotNull String s, @NotNull String s1) {
                Log.i(ANIME,s1+"---------"+s1);
            }

            @Override
            public void d(@NotNull String s, @NotNull String s1) {
                Log.d(ANIME,s1+"---------"+s1);
            }

            @Override
            public void e(@NotNull String s, @NotNull String s1) {
                Log.e(ANIME,s1+"---------"+s1);
            }

            @Override
            public void e(@NotNull String s, @NotNull String s1, @NotNull Throwable throwable) {
                Log.e(ANIME,s1+"---------"+s1);
            }
        });
        ALog.INSTANCE.setDebug(true);
        File file=new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES)+"/vap.mp4");
        File file3=new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES)+"/vap2.mp4");
        File file2=new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES)+"/666.mp4");
        Log.e(TAG, "onCreate: "+getExternalFilesDir(null) );
        launcherVideoView.setOnViewStatusChange(new LauncherVideoView.onViewStatusChange() {
            @Override
            public void onCreat() {
                launcherVideoView.setSource(file.getAbsolutePath());
            }

            @Override
            public void onPrepared() {
                if (countDownTimer==null){
                    countDownTimer = new CountDownTimer(PLAYTIME-position,1000) {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.e(TAG, "onTick: " );
                            button.setText("点击关闭 "+millisUntilFinished/1000+"s");
                        }

                        @Override
                        public void onFinish() {
                            Log.e(TAG, "onFinish: " );
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                            startService(new Intent(LauncherActivity.this,Empty.class));
                        }
                    };
                    countDownTimer.start();
                }
                if(position<PLAYTIME) {
                    launcherVideoView.startPlay(true);
                    if (position != 0) {
                        launcherVideoView.setPlayingTime(position);
                    }
                }
            }

            @Override
            public void onPlaying() {

            }

            @Override
            public void onDestory() {

            }
        });
        new CountDownTimer(80000,1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "onTick: " + millisUntilFinished);
                try {
                    startService(new Intent(LauncherActivity.this,Empty.class));
                    bindService(new Intent(LauncherActivity.this, Empty.class), connection, BIND_AUTO_CREATE);
                    unbindService(connection);
                    stopService(new Intent(LauncherActivity.this,Empty.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish: " );
                startService(new Intent(LauncherActivity.this,Empty.class));
                bindService(new Intent(LauncherActivity.this, Empty.class), connection, BIND_AUTO_CREATE);
                unbindService(connection);
                stopService(new Intent(LauncherActivity.this,Empty.class));
            }
        }.start();
        launcherVideoView.setLifecycleOwner(this);
        LifecycleHandler lifecycleHandler = new LifecycleHandler(this);
        lifecycleRegistry.addObserver(lifecycleHandler);
        lifecycleRegistry.addObserver(launcherVideoView);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(),MainActivity.class);
            startActivity(intent);
        });
        AnimView animView = new AnimView(this);
        ((ViewGroup)findViewById(android.R.id.content)).addView(animView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        animView.setAnimListener(new SimpleAnimeListener());
        animView.setLoop(-1);
        animView.startPlay(file);
    }

    private Context getContext() {
        return this;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        launcherVideoView.release();
        Log.e(TAG, "onDestroy: " );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " +position);
        if (position!=0&&exitTime!=0){
            position = (int) (position+System.currentTimeMillis()-exitTime);
            Log.e(TAG, "onResume: " +position);
            if (position> PLAYTIME){
                gotoMainActivity();
            }
        }
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: " );
    }

    @Override
    protected void onPause() {
        super.onPause();
        position = launcherVideoView.getPlayingTime();
        launcherVideoView.pauseVideo();
        exitTime=System.currentTimeMillis();
        if (countDownTimer!=null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        Log.e(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: " );
    }

    /**
     * 判断某个Activity 界面是否在前台
     * @param context
     * @param className 某个界面名称
     * @return
     */
    public static boolean  isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;

    }
}