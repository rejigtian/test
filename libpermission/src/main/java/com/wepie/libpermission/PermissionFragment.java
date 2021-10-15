package com.wepie.libpermission;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * date 2018/9/4
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class PermissionFragment extends Fragment {

    private static final String PERMISSION_GROUP = "permission_group";//请求的权限
    private static final String REQUEST_CODE = "request_code";
    private static final long CHECK_INTERVAL = 1000;
    private static long lastCheckTime = 0;

    private final static SparseArray<PermissionCallback> sContainer = new SparseArray<>();

    private int requestCode = 0;
    private ArrayList<String> permissions = new ArrayList<>();

    public static PermissionFragment newInstance(PermissionOption option) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle bundle = new Bundle();

        int requestCode;
        //请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            requestCode = new Random().nextInt(65535);//Studio编译的APK请求码必须小于65536
        } while (sContainer.get(requestCode) != null);

        bundle.putInt(REQUEST_CODE, requestCode);
        bundle.putStringArrayList(PERMISSION_GROUP, option.permissions);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 准备请求
     */
    public void prepareRequest(Context context, PermissionCallback callback) {

        if (System.currentTimeMillis() - lastCheckTime < CHECK_INTERVAL) {
            return;
        }
        lastCheckTime = System.currentTimeMillis();

        //将当前的请求码和对象添加到集合中
        sContainer.put(getArguments().getInt(REQUEST_CODE), callback);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            FragmentManager fm = activity.getFragmentManager();
            if (fm != null) {
                fm.beginTransaction().add(this, activity.getClass().getName()).commitAllowingStateLoss();
            }

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSION_GROUP);
        if (permissions != null) {
            this.permissions = permissions;
        }
        requestCode = getArguments().getInt(REQUEST_CODE, -1);
        requestPermission();
    }

    /**
     * 请求权限
     */
    public void requestPermission() {
        Log.e("PermissionFragment", "requestPermission: ");
        if (PermissionUtil.isOverMarshmallow()) {
            requestPermissions(permissions.toArray(new String[permissions.size() - 1]), requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionCallback callback = sContainer.get(requestCode);
        if (callback == null) return;

        List<String> succeedPermissions = PermissionUtil.getSucceedPermissions(permissions, grantResults);
        if (succeedPermissions.size() == permissions.length) {
            callback.hasPermission(succeedPermissions, true, false);
        } else {
            List<String> failPermissions = PermissionUtil.getFailPermissions(permissions, grantResults);

            callback.noPermission(failPermissions, PermissionUtil.checkMorePermissionPermanentDenied(getActivity(), failPermissions));
            if (!succeedPermissions.isEmpty()) {
                callback.hasPermission(succeedPermissions, false, false);
            }
        }

        sContainer.remove(requestCode);
        getFragmentManager().beginTransaction().remove(this).commit();
    }

}