package com.example.test.util;

public class ClassTest {
    public static void main(String[] args) {
//        System.out.println(Test2.getInstance().id + "getid:" +Test2.getInstance().getId());
//        System.out.println(Test2.getInstance().getClass());
//        System.out.println(Test2.getInstance() instanceof Test2);
//
//        Priority priority = new Priority(1,1);
//        Priority priority2 = new Priority(2,2);
//        Priority priority3 = new Priority(3,3);
//        List<Priority> priorityList = new ArrayList<>();
//        priorityList.add(priority);
//        priorityList.add(priority2);
//        priorityList.add(priority3);
//        Collections.sort(priorityList);
//        System.out.println(priorityList.get(0).toString());
        testFinalkey("abc");
        testFinalkey("qwe");

    }

    public static void testFinalkey(final String a){
        testCallback(new Callback() {
            @Override
            public void onCall() {
                System.out.println(a);
            }
        });
    }

    public static void testCallback(Callback callback){
        callback.onCall();
    }

    interface Callback{
        void onCall();
    }
}
