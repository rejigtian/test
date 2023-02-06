package com.huiwan.base.context.holder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewParent;

import com.huiwan.base.LibBaseUtil;
import com.huiwan.base.util.ContextUtil;
import com.huiwan.base.util.ToastUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ContextHolder {

    private static final Map<Class<?>, Object> cacheMap = new HashMap<>();


    /**
     * 根据接口寻找 c 的实现，一般适用于 activity 实现某接口, 底层直接通过 context 取到接口
     */
    public static <T> T of(Context context, Class<T> c) {
        Activity activity = ContextUtil.getActivityFromContext(context);
        if (c.isInstance(activity)) {
            return c.cast(activity);
        }
        return getCacheObjInstance(c);
    }

    /**
     * 根据接口寻找 c 的实现，一般适用于从 子view 到 父 view 的遍历，如果未找到，则寻找 activity 接口
     */
    public static <T> T of(View view, Class<T> c) {
        View v = view;
        ViewParent parent;
        if (c.isInstance(view)) {
            return c.cast(view);
        }
        while ((parent = v.getParent()) instanceof View) {
            v = (View)parent;
            if (c.isInstance(v)) {
                return c.cast(v);
            }
        }
        return of(view.getContext(), c);
    }

    private static <T> T getCacheObjInstance(Class<T> c) {
        ToastUtil.debugShow("Context Holder invoke none for class: {}", c);
        Object obj = cacheMap.get(c);
        if (obj != null) {
            return c.cast(obj);
        }
        if (c.isInterface()) {
            ClassLoader loader = c.getClassLoader();
            Class<?>[] arr = new Class[]{c};
            Object proxy = Proxy.newProxyInstance(loader, arr, new NoneImplHandler<>(c));
            T proxyApi = c.cast(proxy);
            cacheMap.put(c, proxyApi);
            return proxyApi;
        } else {
            return null;
        }
    }

    static class NoneImplHandler<T> implements InvocationHandler {
        private final Class<? extends T> c;

        public NoneImplHandler(Class<? extends T> c) {
            this.c = c;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            LibBaseUtil.logInfo("ContextHolder", "invoke none for class: {}", c);
            return null;
        }
    }
}
