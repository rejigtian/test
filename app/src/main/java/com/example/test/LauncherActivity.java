package com.example.test;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleRegistry;

import com.example.test.util.StatusBarUtil;
import com.example.test.view.AdjustVideoView;
import com.example.test.view.FullLifecycleObserverAdapter;
import com.rejig.base.widget.ScreenUtil;
import com.tencent.qgame.animplayer.util.ALog;
import com.tencent.qgame.animplayer.util.IALog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class LauncherActivity extends AppCompatActivity implements SensorEventListener {
    private AdjustVideoView videoView;
    private Button button;
    private int position = 0;
    private String TAG = "LauncherActivity";
    private String ANIME = "AnimeView";
    private long exitTime = 0;
    public static final int PLAYTIME = 60000;
    private CountDownTimer countDownTimer;
    private Handler handler = new Handler();

    private SensorManager sensorManager;
    LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private long mTimestamp = 0;
    private static final float NS2S = 1.0f / 1000000000.0f; // 将纳秒转化为秒
    float[] mAngle = new float[3];
    float oldAngleX = (float) Math.toDegrees(mAngle[0]);
    // y轴的旋转角度，手机平放桌上，然后绕底边转动
    float oldAngleY = (float) Math.toDegrees(mAngle[1]);
    // z轴的旋转角度，手机平放桌上，然后水平旋转
    float oldAngleZ = (float) Math.toDegrees(mAngle[2]);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.initStatusBar(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launcher);
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
                startService(new Intent(LauncherActivity.this, Empty.class));
            }
        }, 60 * 1000);
        videoView = findViewById(R.id.launcher_lvv);
        int width = ScreenUtil.getScreenWidth(getContext()) + 400;
        videoView.getLayoutParams().width = width;
        videoView.getLayoutParams().height = width * 16 / 9;
        videoView.setVolume(100);
        videoView.setLooping(true);
        videoView.setAdjustType(AdjustVideoView.FULL_HEIGHT);
        getLifecycle().addObserver(new FullLifecycleObserverAdapter(videoView));
        button = findViewById(R.id.btn_close);
        ALog.INSTANCE.setLog(new IALog() {
            @Override
            public void i(@NotNull String s, @NotNull String s1) {
                Log.i(ANIME, s1 + "---------" + s1);
            }

            @Override
            public void d(@NotNull String s, @NotNull String s1) {
                Log.d(ANIME, s1 + "---------" + s1);
            }

            @Override
            public void e(@NotNull String s, @NotNull String s1) {
                Log.e(ANIME, s1 + "---------" + s1);
            }

            @Override
            public void e(@NotNull String s, @NotNull String s1, @NotNull Throwable throwable) {
                Log.e(ANIME, s1 + "---------" + s1);
            }
        });
        ALog.INSTANCE.setDebug(true);
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/vap2.mp4");
        File file3 = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/vap2.mp4");
        File file2 = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/ring.mp4");
        Log.e(TAG, "onCreate: " + getExternalFilesDir(null));
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(PLAYTIME - position, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.e(TAG, "onTick: ");
                    position += 1000;
                    button.setText("点击关闭 " + millisUntilFinished / 1000 + "s");
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "onFinish: ");
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            };
            countDownTimer.start();
        }
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (position < PLAYTIME) {
            videoView.setSource(file.getPath());
            videoView.startPlay();
            if (position != 0) {
                videoView.setPlayingTime(position);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        }
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
        sensorManager.unregisterListener(this);
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " + position);
        if (position != 0 && exitTime != 0) {
            position = (int) (position + System.currentTimeMillis() - exitTime);
            Log.e(TAG, "onResume: " + position);
            if (position > PLAYTIME) {
                gotoMainActivity();
            }
        }
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        exitTime = System.currentTimeMillis();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        Log.e(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    /**
     * 判断某个Activity 界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     * @return
     */
    public static boolean isForeground(Context context, String className) {
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) { // 陀螺仪角度变更事件
            if (mTimestamp != 0) {
                final float dT = (event.timestamp - mTimestamp) * NS2S;
                mAngle[0] += event.values[0] * dT;
                mAngle[1] += event.values[1] * dT;
                mAngle[2] += event.values[2] * dT;
                // x轴的旋转角度，手机平放桌上，然后绕侧边转动
                float angleX = (float) Math.toDegrees(mAngle[0]);
                // y轴的旋转角度，手机平放桌上，然后绕底边转动
                float angleY = (float) Math.toDegrees(mAngle[1]);
                // z轴的旋转角度，手机平放桌上，然后水平旋转
                float angleZ = (float) Math.toDegrees(mAngle[2]);
                String desc = String.format("陀螺仪检测到当前\nx轴方向的转动角度为%f\ny轴方向的转动角度为%f\nz轴方向的转动角度为%f",
                        angleX, angleY, angleZ);
                if (angleX > 1 || angleY > 1 || angleZ > 1) {
                    Log.e(TAG, "onSensorChanged: " + desc);
                }
                videoView.animate().translationX(3 * angleY).setDuration((long) (dT * 1000));
                if (Math.abs(angleY - oldAngleY) > 100000){
                    videoView.animate().cancel();
                    videoView.animate().translationX(2 * angleY).setDuration((long) (dT * 1000));
                    oldAngleX = (float) Math.toDegrees(mAngle[0]);
                    // y轴的旋转角度，手机平放桌上，然后绕底边转动
                    oldAngleY = (float) Math.toDegrees(mAngle[1]);
                    // z轴的旋转角度，手机平放桌上，然后水平旋转
                    oldAngleZ = (float) Math.toDegrees(mAngle[2]);
                }
            }
            mTimestamp = event.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}