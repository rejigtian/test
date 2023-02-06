package com.huiwan.base.util.lru;

import android.util.LruCache;

import java.lang.ref.WeakReference;

/**
 * date 2020/10/9
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class WeakLruHelper<K, V> implements WpLruCache<K, V> {
    private final LruCache<K, WeakReference<V>> cacheMap;

    public WeakLruHelper(int maxSize) {
        cacheMap = new LruCache<>(maxSize);
    }

    @Override
    public V lruGet(K key) {
        WeakReference<V> ref = cacheMap.get(key);
        V value = null;
        if (ref != null) {
            value = ref.get();
            if (value == null) {
                cacheMap.remove(key);
            }
        }
        return value;
    }

    @Override
    public V lruPut(K key, V value) {
        WeakReference<V> preRef =  cacheMap.put(key, new WeakReference<>(value));
        if (preRef != null) {
            return preRef.get();
        }
        return null;
    }
}
