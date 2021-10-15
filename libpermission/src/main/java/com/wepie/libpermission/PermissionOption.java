package com.wepie.libpermission;

import android.app.Activity;

import java.util.ArrayList;

/**
 * date 2018/9/4
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class PermissionOption {
    Activity activity;
    ArrayList<String> permissions = new ArrayList<>();
    String requestTip = "";
    String okTip = "确认";
    String cancelTip = "取消";
}
