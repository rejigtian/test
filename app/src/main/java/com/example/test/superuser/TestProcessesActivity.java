package com.example.test.superuser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by bigwen on 8/11/21.
 */
public class TestProcessesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linearLayout);

        Button javaCrash = new Button(this);
        javaCrash.setText("JavaCrash");
        javaCrash.setOnClickListener(v -> testJavaCrash());
        linearLayout.addView(javaCrash);

        Button nativeCrash = new Button(this);
        nativeCrash.setText("NativeCrash");
        nativeCrash.setOnClickListener(v -> testJavaCrash());
        linearLayout.addView(nativeCrash);

        Button anr = new Button(this);
        anr.setText("anr");
        anr.setOnClickListener(v -> testJavaCrash());
        linearLayout.addView(anr);

        Button kill = new Button(this);
        kill.setText("kill");
        kill.setOnClickListener(v -> android.os.Process.killProcess(android.os.Process.myPid()));
        linearLayout.addView(kill);

        Button exit = new Button(this);
        exit.setText("exit");
        kill.setOnClickListener(v -> System.exit(0));
        linearLayout.addView(exit);

    }

    public void testJavaCrash(){
    }

    public static void go(Context context) {
        context.startActivity(new Intent(context, TestProcessesActivity.class));
    }
}
