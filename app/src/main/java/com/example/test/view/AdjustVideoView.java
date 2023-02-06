package com.example.test.view;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;

import com.rejig.base.widget.ScreenUtil;

/**
 * 增加适配方案的VideoView，继承自{@link SimpleVideoView}
 * 会根据选择的适配方案自动适应视频或者屏幕
 *
 * @author rejig
 * date 2021-12-20
 */
public class AdjustVideoView extends SimpleVideoView {
    public static int BOTTOM = 0x00000001; //底对齐，宽度拉满
    public static int TOP = 0x00000010; //顶对齐，宽度拉满
    public static int FULL_WIDTH = 0x00000100; //宽度拉满，默认上下居中。保持原比例（此属性与左对齐，右对齐，高度拉满互斥）

    public static int LEFT = 0x00001000; //左对齐
    public static int RIGHT = 0x00010000; //右对齐
    public static int FULL_HEIGHT = 0x00100000; //高度拉满，默认左右居中。保持原比例（此属性与顶对齐，底对齐，宽度拉满互斥）

    public static int FIT_XY = 0x00000000; //默认适配方案，为宽高拉满。

    private int adjustType = FIT_XY;
    private boolean needAdjust = false;
    private int rootHeight = 0;
    private int rootWidth = 0;

    public AdjustVideoView(Context context) {
        super(context);
    }

    public AdjustVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdjustVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        super.init();
    }

    /**
     * 设置适配类型，需要在{@link AdjustVideoView#setSource(String)}之前调用
     *
     * @param adjustType 参考{@link AdjustVideoView#FIT_XY}
     * @param rootHeight 适配的高度
     * @param rootWidth  适配的宽度
     */
    public void setAdjustType(int adjustType, int rootHeight, int rootWidth) {
        if (this.adjustType != adjustType || this.rootHeight != rootHeight || this.rootWidth != rootWidth) {
            this.adjustType = adjustType;
            this.rootHeight = rootHeight;
            this.rootWidth = rootWidth;
            needAdjust = true;
        }
    }

    /**
     * 设置适配类型，需要在{@link AdjustVideoView#setSource(String)}之前调用
     * 默认适配宽高为屏幕宽高，若需要自定义，则调用{@link AdjustVideoView#setAdjustType(int, int, int)}
     *
     * @param adjustType 参考{@link AdjustVideoView#FIT_XY}
     */
    public void setAdjustType(int adjustType) {
        if (this.adjustType != adjustType) {
            this.adjustType = adjustType;
            needAdjust = true;
        }
    }

    @Override
    public void setSource(String filePath) {
        if (needAdjust || !currentPath.equals(filePath)) {
            adjustViewBounds(filePath);
        }
        super.setSource(filePath);
    }

    private void adjustViewBounds(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        int vWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int vHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        retriever.release();
        int sWidth = rootWidth > 0 ? rootWidth : ScreenUtil.getScreenWidth(getContext());
        int sHeight = rootHeight > 0 ? rootHeight : ScreenUtil.getRealHeight(getContext());
        LayoutParams layoutParams = new LayoutParams(sWidth, sHeight);
        if ((adjustType & FULL_WIDTH) > 0) {
            sHeight = vHeight * sWidth / vWidth;
            if ((adjustType & TOP) > 0) {
                layoutParams.topToTop = LayoutParams.PARENT_ID;
            } else if ((adjustType & BOTTOM) > 0) {
                layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
            } else {
                layoutParams.topToTop = LayoutParams.PARENT_ID;
                layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
            }
            layoutParams.startToStart = LayoutParams.PARENT_ID;
            layoutParams.endToEnd = LayoutParams.PARENT_ID;
        } else if ((adjustType & FULL_HEIGHT) > 0) {
            sWidth = vWidth * sHeight / vHeight;
            if ((adjustType & LEFT) > 0) {
                layoutParams.startToStart = LayoutParams.PARENT_ID;
            } else if ((adjustType & RIGHT) > 0) {
                layoutParams.endToEnd = LayoutParams.PARENT_ID;
            } else {
                layoutParams.startToStart = LayoutParams.PARENT_ID;
                layoutParams.endToEnd = LayoutParams.PARENT_ID;
            }
            layoutParams.topToTop = LayoutParams.PARENT_ID;
            layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        } else if (adjustType == FIT_XY) {
            layoutParams.topToTop = LayoutParams.PARENT_ID;
            layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
            layoutParams.startToStart = LayoutParams.PARENT_ID;
            layoutParams.endToEnd = LayoutParams.PARENT_ID;
        }
        layoutParams.width = sWidth;
        layoutParams.height = sHeight;
        surfaceView.setLayoutParams(layoutParams);
        requestLayout();
    }
}
