package com.wepie.emoji.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;
/**
 * 辅助调节ViewPager页面切换动画速度
 * @author wh
 *
 */
public class FixedSpeedScroller extends Scroller {

    private int mDuration = 5000;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

	public void setmDuration(int mDuration) {
		this.mDuration = mDuration;
	}
    
    
}
