package com.huiwan.base.util.lru;

import java.util.LinkedHashMap;

/**
 * date 2018/8/9
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class SimpleLruHelper<K, V>  extends LinkedHashMap<K, V> implements WpLruCache<K, V> {
    private int maxSize = 8;

    public SimpleLruHelper(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor, true);
    }

    public void setMaxSize(int maxSize) {
        if (maxSize > 0) {
            this.maxSize = maxSize;
        }
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }

    @Override
    public V lruGet(K key) {
        return get(key);
    }

    @Override
    public V lruPut(K key, V value) {
        return put(key, value);
    }
}