package com.example.test.gift;

import android.view.animation.Interpolator;

/**
 * @author rejig
 * date 2020-08-18
 * 用于加速度体现的分割器
 * 运动轨迹为向上的匀减速运动，速度到0后变为向下的匀加速运动
 * 根据getDuration获取总时间
 */
public class GiftInterpolator implements Interpolator {
    public static final float maxhRatio = 0.618f; //最高点
    public static final float finalhRatio = 0.568f; //UP则为终点，DOWN则为起点
    private boolean isUp;//0向上抛，1向下落
    private final static float maxt= (float) (Math.sqrt(maxhRatio)+Math.sqrt(maxhRatio - finalhRatio));

    public GiftInterpolator(boolean isUp) {
        this.isUp = isUp;
    }

    @Override
    public float getInterpolation(float t) {
        if (isUp){
            float x= (float) Math.pow(Math.sqrt(maxhRatio)-t*maxt,2);
            return 1-x/ maxhRatio;
        }else {
            float x= (float) Math.pow((t*maxt-Math.sqrt(maxhRatio - finalhRatio)),2);
            return x/ maxhRatio;
        }
    }

    /**
     * 获取运动总时间
     * @param g 加速度g
     * @param h 最高高度h
     * @return 运动总时间，单位毫秒
     */
    public static float getDuration(float g,float h){
        return (float) (Math.sqrt(maxhRatio*h/g)+Math.sqrt((maxhRatio - finalhRatio)*h/g))*1000;
    }

    /**
     * 获取上下运动的总时间
     * @param isUp 运动方向
     * @return 上下抛的时间
     */
    public static float getTimeRatio(boolean isUp){
        if (isUp){
            return (float) (Math.sqrt(maxhRatio) / maxt);
        }else {
            return (float) (Math.sqrt(maxhRatio - finalhRatio)/ maxt);
        }
    }

}
