package com.wepie.libpermission;

import java.util.List;

/**
 * date 2018/9/4
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public interface PermissionCallback {

    /**
     * 有权限被授予时回调
     *
     * @param granted 请求成功的权限组
     * @param isAll   是否全部授予了
     * @param alreadyHas 本来就有权限
     */
    void hasPermission(List<String> granted, boolean isAll, boolean alreadyHas);

    /**
     * 有权限被拒绝授予时回调
     *
     * @param denied 请求失败的权限组
     * @param quick  是否有某个权限被永久拒绝了
     */
    void noPermission(List<String> denied, boolean quick);
}
