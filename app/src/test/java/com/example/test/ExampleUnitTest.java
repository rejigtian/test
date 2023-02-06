package com.example.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        test();
//        byte[] bytes = ByteBuffer.allocate(4)
//                .order(ByteOrder.LITTLE_ENDIAN)
//                .putInt(48000)
//                .array();
//        System.out.println(Arrays.toString(bytes));
//        System.out.println(toInt(bytes));
//
//        int[] numArray = {1,3,2,6,10,7,9};
//        int uniqueNum = Integer.MAX_VALUE;
//        for (int num : numArray) {
//            uniqueNum = uniqueNum & num;
//        }
//        System.out.println(uniqueNum);
    }

    public static int toInt(byte[] bytes) {
        return (bytes[0] & 0xff)
                |((bytes[1] & 0xff) << 8)
                |((bytes[2] & 0xff) << 16)
                |((bytes[3] & 0xff) << 24);
    }

    public static short toShort(byte[] b) {
        return (short) ((b[1] << 8) + (b[0]));
    }

    public void test() {
        List<Integer> integerList = new ArrayList<Integer>(){{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
        }};
        List<Integer> test = integerList.subList(0,4);
        integerList.add(999);
        System.out.println( "test" + test.size() + "");
    }
}