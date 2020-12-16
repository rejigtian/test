package com.example.test.collection;

/**
 * date 2019/1/9
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public interface IMap<T, V> {
    /**
     * transform the value to a new value
     * @param t the origin value
     * @return the new value
     */
    V transform(T t);
}
