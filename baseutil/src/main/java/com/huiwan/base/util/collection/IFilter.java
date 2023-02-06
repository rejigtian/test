package com.huiwan.base.util.collection;

/**
 * date 2019/1/9
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public interface IFilter<T> {
    /**
     * filter the item if not true
     * @param t the param to check
     * @return true if t saved otherwise removed
     */
    boolean filter(T t);
}
