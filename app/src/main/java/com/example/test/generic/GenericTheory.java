package com.example.test.generic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author：Jay On 2019/5/11 16:11
 * <p>
 * Description:泛型原理测试类
 */
public class GenericTheory {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("Key", "Value");
        System.out.println(map.get("Key"));
        GenericClass<String, String> genericClass = new GenericClass<>();
        genericClass.put("Key", "Value");
        System.out.println(genericClass.get("Key"));
    }

    public static class GenericClass<K, V> {
        private K key;
        private V value;

        public void put(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public V get(K key) {
            return value;
        }
    }

    /**
     * 类型擦除后GenericClass2<Object>
     * @param <T>
     */
    private static class GenericClass2<T> {

    }

    /**
     * 类型擦除后GenericClass3<ArrayList>
     * 当使用到Serializable时会将相应代码强制转换为Serializable
     * @param <T>
     */
    private class GenericClass3<T extends ArrayList & Serializable> {

    }
}