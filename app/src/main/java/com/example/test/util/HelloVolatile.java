package com.example.test.util;

import java.util.concurrent.TimeUnit;

public class HelloVolatile {
    //对比有无Volitale的区别
    /*volatile*/
    volatile boolean runing = true;
    void process(){
        System.out.println("process start running ...");
        while (runing){
        }
        System.out.println("process end ...");
    }
    public static void main(String[] args) {
//主线程
        HelloVolatile A = new HelloVolatile();
        new Thread(A::process, "B").start();
        try{
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        A.runing = false;
    }
}