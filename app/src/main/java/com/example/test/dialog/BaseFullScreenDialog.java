package com.example.test.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.test.R;
import com.example.test.util.AppUtil;
import com.example.test.util.ContextUtil;
import com.example.test.util.FullScreenUtil;
import com.rejig.base.widget.ScreenUtil;


/**
 * Created by three on 15/1/12.
 */
public class BaseFullScreenDialog extends Dialog {
    private static final float WIDTH_FACTOR = 0.8f;
    private static final float HEIGHT_FACTOR = 0.5f;

    private Context mContext;
    public BaseFullScreenDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public BaseFullScreenDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public void init() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = getDialogWidth();
        dialogWindow.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(dialogWindow);
    }

    public void init303() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ScreenUtil.dip2px(303);
        dialogWindow.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(dialogWindow);
    }

    public void initLandlordDialog() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ScreenUtil.dip2px(360);
        dialogWindow.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(dialogWindow);
    }

    public void initFullWidth() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ScreenUtil.getScreenWidth(AppUtil.getApplication());
        dialogWindow.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(dialogWindow);
    }

    public void initListDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = getDialogWidth();
        lp.height = getDialogHeight();
        window.setAttributes(lp);
    }

    public static int getDialogWidth() {
        return (int) (ScreenUtil.getScreenWidth(AppUtil.getApplication()) * WIDTH_FACTOR);
    }

    public static int getDialogHeight() {
        return (int) (ScreenUtil.getScreenHeight(AppUtil.getApplication()) * HEIGHT_FACTOR);
    }

    public void initBottomDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setWindowAnimations(R.style.bottom_dialog_anim);
        window.setAttributes(lp);
    }

    public void initBottomDialogSlow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setWindowAnimations(R.style.bottom_dialog_anim_slow);
        window.setAttributes(lp);
    }

    public void initFullBottomDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        window.setWindowAnimations(R.style.bottom_dialog_anim);
        window.setAttributes(lp);
    }

    public void initFullScreenBottomDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        window.setWindowAnimations(R.style.bottom_dialog_anim);
        FullScreenUtil.setFullScreenWithStatusBar(window);
        window.setAttributes(lp);
    }

    public void initCenterDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        window.setWindowAnimations(R.style.null_animation_dialog);
        window.setAttributes(lp);
    }

    public void initFullScreenDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    public void initFullCenterDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        window.setWindowAnimations(R.style.Animation_POP);
        window.setAttributes(lp);
        FullScreenUtil.setFullScreenWithStatusBar(window);
    }
    @Override
    public void show() {
        try {
            Activity activity = ContextUtil.getActivityFromContext(mContext);
            if (activity != null && activity.isFinishing()) return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.show();
    }

    @Override
    public void dismiss() {
        try {
            Activity activity = ContextUtil.getActivityFromContext(mContext);
            if (activity != null && activity.isFinishing()) return;
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
