package com.example.test.gl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.test.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class glview extends ConstraintLayout {
    private Context context;
    private GLSurfaceView glSurfaceView;
    private List<Coordinate> list=new ArrayList<>();
    private Handler handler=new Handler();
    private Random random=new Random();
    private TextureRenderer textureRenderer;
    private boolean showing=true;
    public glview(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.glview, this);
        glSurfaceView=findViewById(R.id.glsv_test);
        glSurfaceView.setEGLContextClientVersion(2);
        textureRenderer=new TextureRenderer(context,0);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.setZOrderOnTop(true);
        glSurfaceView.setRenderer(textureRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        textureRenderer.setRectFList(list);
        glSurfaceView.requestRender();
        for(int i=0;i<6;i++){
            Coordinate coordinate2=new Coordinate(i*150+50,random.nextInt(100),0);
            coordinate2.setSpeed(random.nextInt(20)+15);
            list.add(coordinate2);

        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        checkUpdate();
    }

    private void checkUpdate() {
        for (int i = 0; i < list.size(); i++) {
            Coordinate rectF1 = list.get(i);
            if (rectF1.getY()>2160) {
                rectF1.setY(-120);
                rectF1.setSpeed(random.nextInt(20)+15);
            }
            if (rectF1.getY()>1000&&list.size()<20){
                Coordinate tempR=new Coordinate(rectF1.getX()+random.nextInt(100)-50,0,0);
                tempR.setSpeed(random.nextInt(20)+15);
                list.add(tempR);
            }
            else
                rectF1.setY(rectF1.getY()+rectF1.getSpeed());
            list.set(i, rectF1);
        }
        textureRenderer.setRectFList(list);
        glSurfaceView.requestRender();
        invalidate();
    }
}
