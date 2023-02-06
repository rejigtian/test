package com.huiwan.base.util.lru;

/**
 * date 2020/10/9
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public interface WpLruCache<K, V> {
    V lruPut(K key, V value);
    V lruGet(K key);
}
