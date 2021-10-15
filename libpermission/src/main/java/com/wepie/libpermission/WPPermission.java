package com.wepie.libpermission;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * date 2018/9/4
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class WPPermission {
    public static final String GET_INSTALLED_APPS = "com.android.permission.GET_INSTALLED_APPS";
    private final PermissionOption option = new PermissionOption();

    private WPPermission() { }

    public static WPPermission with(Activity activity) {
        WPPermission permission = new WPPermission();
        permission.option.activity = activity;
        return permission;
    }

    public WPPermission permission(String... permissions) {
        option.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    public WPPermission requestDialogTip(String tip) {
        option.requestTip = tip;
        return this;
    }

    /**
     * 该方法需要弹出对话框会导致所调用的 activity 出现 onResume。
     * @param callback 权限申请回调
     */
    public void request(PermissionCallback callback) {
        if (requesting()) {
            return;
        }

        if (option.activity == null) {
            Log.e("WPPermission", "activity should not be null");
            return;
        }

        if (option.activity.isFinishing() || option.activity.isDestroyed()) {
            Log.e("WPPermission", "activity destroyed");
            return;
        }

        final InterceptCallback cb = new InterceptCallback(option.activity, callback);

        ArrayList<String> failPermissions = PermissionUtil.getFailPermissions(option.activity, option.permissions);

        if (failPermissions == null || failPermissions.size() == 0) {
            cb.hasPermission(option.permissions, true, true);
        } else {
            //2021/07/21 去掉了检测是否永久拒绝的逻辑，以处理某些情况下会玩弹窗没弹就弹系统的权限弹窗的不合规
            if (!TextUtils.isEmpty(option.requestTip)) {
                Log.e("WPPermission", "show tip dialog");
                PermissionDialog dialog = PermissionDialog.newDialog(option.requestTip,
                        option.okTip, option.cancelTip);
                dialog.setOkClickListener(new PermissionDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogFragment dialog, boolean fromOkBtn) {
                        PermissionFragment.newInstance(option).prepareRequest(option.activity, cb);
                        dialog.dismiss();
                    }
                });
                dialog.setCancelClickListener(new PermissionDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogFragment dialog, boolean fromOkBtn) {
                        cb.noPermission(option.permissions, false);
                        dialog.dismiss();
                    }
                });
                dialog.showIfNeed(option.activity.getFragmentManager(), option.permissions.toString());
            } else {
                //申请没有授予过的权限
                PermissionFragment.newInstance(option).prepareRequest(option.activity, cb);
            }
        }
    }

    private boolean requesting() {
        Activity activity = option.activity;
        if (activity == null) {
            return false;
        }
        FragmentManager fm = activity.getFragmentManager();
        if (fm == null) {
            return false;
        }
        Fragment fragment = fm.findFragmentByTag(activity.getClass().getName());
        Fragment dialogFragment = fm.findFragmentByTag(option.permissions.toString());
        Log.e("WPPermission", "requesting: " + fragment + "\t" + dialogFragment);
        return fragment != null || dialogFragment != null;
    }

    private static class InterceptCallback implements PermissionCallback {
        PermissionCallback callback;
        Context context;

        InterceptCallback(Context context, PermissionCallback callback) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        public void hasPermission(List<String> granted, boolean isAll, boolean alreadyHas ) {
            if (granted.contains(Manifest.permission.RECORD_AUDIO)) {
                if (!PermissionUtil.hasPermission(context, Manifest.permission.RECORD_AUDIO)) {
                    //某些特殊手机在给了录音权限之后还是不能录音。这里更进一步检查。
                    noPermission(granted, true);
                    return;
                }
            }
            callback.hasPermission(granted, isAll, alreadyHas);
        }

        @Override
        public void noPermission(List<String> denied, boolean quick) {
            callback.noPermission(denied, quick);
        }
    }
}
