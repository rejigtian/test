package com.example.test.util;

import java.util.ArrayList;
import java.util.List;

public class BagUtil {
    public static void main(String[] args) {
        List<Integer> values = new ArrayList<>();
        List<Integer> weight = new ArrayList<>();
        values.add(6);
        values.add(3);
        values.add(5);
        values.add(4);
        values.add(6);
        values.add(6);
        values.add(6);

        weight.add(2);
        weight.add(2);
        weight.add(6);
        weight.add(5);
        weight.add(4);
        weight.add(1);
        weight.add(1);
        System.out.println(getMaxValue(values, weight, 10));
    }

    public static int getMaxValue(List<Integer> values, List<Integer> weights, int space) {
        int max = 0;
        for (int i = 0; i < weights.size(); i++) {
            int curWeight= weights.get(i);
            if (space >= curWeight) {
                int curValue= values.get(i);
                values.remove(i);
                weights.remove(i);
                max = Math.max(curValue + getMaxValue(values, weights, space - curWeight), getMaxValue(values, weights, space));
                break;
            }
        }
        System.out.println("times");
        System.out.println(space + ":" + max);
        return max;
    }

    public static int getMaxValue2(List<Integer> values, List<Integer> weights, int space) {
        List<Integer> nextVaues = new ArrayList<>(values);
        List<Integer> nextWeight = new ArrayList<>(values);
        int max = 0;
        for (int i = 0; i < weights.size(); i++) {
            if (space > weights.get(i)) {
                nextVaues.remove(i);
                nextWeight.remove(i);
                max = Math.max(weights.get(i) + getMaxValue(values, weights, space - weights.get(i)), getMaxValue(nextVaues, nextWeight, space));
                break;
            }
        }
        System.out.println(space + ":" + max);
        return max;
    }
}
